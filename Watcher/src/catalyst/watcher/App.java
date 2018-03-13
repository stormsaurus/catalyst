package catalyst.watcher;
import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.jnlp.ServiceManager;
import javax.jnlp.SingleInstanceListener;
import javax.jnlp.SingleInstanceService;
import javax.jnlp.UnavailableServiceException;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

@SuppressWarnings("serial")
public class App extends JFrame implements SingleInstanceListener{

    private Properties _preferences = null;

    protected JToolBar _toolbar = null;

    protected JButton _deleteButton = null;
    protected JButton _configureButton = null;
    protected JButton _chatButton = null;

    protected JMenuItem _deleteConnection = null;
    protected JMenuItem _configureConnection = null;

    protected JTable _connectionTable = null;
    protected ConnectionManager _connectionManager = null;
    protected SystemTray _systemTray = null;
    protected TrayIcon _trayIcon = null;

    private static File _applicationDirectory = new File(System.getProperty("user.home")+"/.catalyst-watcher/");
    private static JTextArea _console = new JTextArea(3,20);
    private boolean _firstRun = true;

    public final static String version = "0.1";

    private SingleInstanceService _sis = null;

    public void init(){

        //TODO investigate what happens if app crashes and doesn't get to unregister from service
        //TODO wizard to step through auto-creation of WoW connection on _firstRun
        try {
            _sis = (SingleInstanceService)ServiceManager.lookup("javax.jnlp.SingleInstanceService");
            _sis.addSingleInstanceListener(this);
        } catch (UnavailableServiceException e) {
            //if this happens we are likely running outside jnlp for testing
        }


        //do some sanity checks on writing to application directory
        //vista might have trouble with write access unless it's Application Data directory
        if( !_applicationDirectory.exists() ){                                            //check to see if app directory exists
                _applicationDirectory.mkdir();                                            //if not create it
        }
        File test = new File(_applicationDirectory,"temp");                                //create a temp file to check for write permission
        try {test.createNewFile();} catch (IOException e1) {e1.printStackTrace();}
        if( !test.canWrite() ) {                                                        //if we can't write to app directory
            _applicationDirectory = new File(System.getProperty("user.home"));            //fall back to home directory
            test.delete();                                                                //delete first test file
            test = new File(_applicationDirectory,"temp");                                //and make a new one
            try {test.createNewFile();} catch (IOException e1) {e1.printStackTrace();}     //create another temp file in home directory and try again
            if( test.canWrite() ){
                App.log("Can not write to application directory.  Writing to home directory instead.");
            } else {
                App.log("Can not write to any directory.  Configurations will not be saved.");
            }
           } else {
               test.delete();                                                                //delete test file
           }

        ClassLoader cl = this.getClass().getClassLoader();

        //load up preferences
        _preferences = new Properties();
        try {
            _preferences.loadFromXML(new FileInputStream(new File(_applicationDirectory, "preferences-config.xml")));
        } catch (Exception e) {
            App.log(getClass().getSimpleName()+" could find or load preferences.  Using default.");
            _firstRun = true;                                                            //if there is no preferences file treat as first time the program has run
        }
        //if first time program has run, setup defaults for preferences
        if( _firstRun ){
            _preferences.setProperty("version", version);
            _preferences.setProperty("useTray","true");
            _preferences.setProperty("autoLaunch", "true");
        }

        App.log("Watcher v"+version);

        //set app look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            JFrame.setDefaultLookAndFeelDecorated(false);
        }

        //instantiate main components
        _connectionManager = new ConnectionManager();
        _connectionTable = new JTable(new ConnectionTableModel(_connectionManager));
        AppCommandProcesser commandProcessor = new AppCommandProcesser(this);

        //setup system tray if we have one
        if ( _preferences.containsKey("useTray") && ((String)_preferences.get("useTray")).equalsIgnoreCase("true") && SystemTray.isSupported()) {

            _systemTray = SystemTray.getSystemTray();
            Image trayImage = null;
            if( OS.get() == OS.Windows ){
                trayImage = (new ImageIcon(cl.getResource("icons/globe16.png"))).getImage();                //could do this with the default window toolkit
            } else {
                trayImage = (new ImageIcon(cl.getResource("icons/globe22.png"))).getImage();
            }

            PopupMenu popup = new PopupMenu();
            MenuItem exitItem = new MenuItem("Exit");
            exitItem.setActionCommand("exit");
            exitItem.addActionListener(commandProcessor);
            popup.add(exitItem);

            _trayIcon = new TrayIcon(trayImage, "Watcher", popup);
            _trayIcon.setImageAutoSize(true);
            _trayIcon.addMouseListener(commandProcessor);

            try {
                _systemTray.add(_trayIcon);
            } catch (AWTException e) {
                System.err.println("TrayIcon could not be added.");
            }

        }

        //Create and set up the window.
        setTitle("Watcher");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(commandProcessor);

        _connectionTable.getSelectionModel().addListSelectionListener(commandProcessor);
        _connectionTable.setDefaultRenderer(Connection.Status.class, new StatusCellRenderer());
        _connectionTable.setFillsViewportHeight(true);
        _connectionTable.setAutoCreateRowSorter(true);
        _connectionTable.setShowVerticalLines(false);
        TableColumnModel tcm = _connectionTable.getColumnModel();
        TableColumn col = null;
        for (int i = 0; i < tcm.getColumnCount(); i++) {
            col = tcm.getColumn(i);
            switch (i){
            case 0: col.setPreferredWidth(40);            //status
                    break;
            case 1: col.setPreferredWidth(60);            //game
                    break;
            case 2: col.setPreferredWidth(80);            //channel
                    break;
            case 3: col.setPreferredWidth(80);            //room
                    break;
            case 4: col.setPreferredWidth(400);            //note
                    break;
            case 5: col.setPreferredWidth(30);            //activate
                    col.setMinWidth(30);
                    col.setMaxWidth(30);
                    break;
            default:     col.setPreferredWidth(60);            //unknown
                        break;
            }
        }
        _connectionTable.addMouseListener(commandProcessor);

        JScrollPane channelsPane = new JScrollPane(_connectionTable);
        channelsPane.setPreferredSize(new Dimension(600, 150));

        //Create the menu bar.
        JMenuBar menuBar = new JMenuBar();
        menuBar.setOpaque(true);

        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        fileMenu.getAccessibleContext().setAccessibleDescription("Open the file menu.");
        menuBar.add(fileMenu);

        JMenuItem newChannel = new JMenuItem("New Channel ...",KeyEvent.VK_N);
        newChannel.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        newChannel.setMnemonic(KeyEvent.VK_N);
        newChannel.getAccessibleContext().setAccessibleDescription("Create a new chat.");
        newChannel.setActionCommand("newConnection");
        newChannel.addActionListener(commandProcessor);
        fileMenu.add(newChannel);

        fileMenu.addSeparator();

        JMenuItem exit = new JMenuItem("Exit",KeyEvent.VK_N);
        exit.setMnemonic(KeyEvent.VK_X);
        exit.getAccessibleContext().setAccessibleDescription("Exit program.");
        exit.setActionCommand("exit");
        exit.addActionListener(commandProcessor);
        fileMenu.add(exit);

        JMenu editMenu = new JMenu("Edit");
        editMenu.setMnemonic(KeyEvent.VK_E);
        editMenu.getAccessibleContext().setAccessibleDescription("Edit Menu");
        menuBar.add(editMenu);

        _deleteConnection = new JMenuItem("Delete",KeyEvent.VK_D);
        _deleteConnection.getAccessibleContext().setAccessibleDescription("Delete Connection");
        _deleteConnection.setActionCommand("deleteConnection");
        _deleteConnection.addActionListener(commandProcessor);
        _deleteConnection.setEnabled(false);
        editMenu.add(_deleteConnection);

        _configureConnection = new JMenuItem("Configure ...",KeyEvent.VK_C);
        _configureConnection.getAccessibleContext().setAccessibleDescription("Connfigure Connection");
        _configureConnection.setActionCommand("configureConnection");
        _configureConnection.addActionListener(commandProcessor);
        _configureConnection.setEnabled(false);
        editMenu.add(_configureConnection);

        editMenu.addSeparator();
        JCheckBoxMenuItem toolbarMenuItem = new JCheckBoxMenuItem("Toolbar");
        toolbarMenuItem.setMnemonic(KeyEvent.VK_T);
        toolbarMenuItem.setSelected(true);
        toolbarMenuItem.getAccessibleContext().setAccessibleDescription("Toggle Toolbar");
        toolbarMenuItem.addItemListener(commandProcessor);
        editMenu.add(toolbarMenuItem);


        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);
        helpMenu.getAccessibleContext().setAccessibleDescription("Help Menu");
        menuBar.add(helpMenu);

        JMenuItem visit = new JMenuItem("Help",KeyEvent.VK_V);                            // TODO change this to go to actual help docs
        visit.getAccessibleContext().setAccessibleDescription("Visit website");
        visit.setActionCommand("website");
        visit.addActionListener(commandProcessor);
        helpMenu.add(visit);

        JMenuItem about = new JMenuItem("About",KeyEvent.VK_A);
        about.getAccessibleContext().setAccessibleDescription("About the program");
        about.setActionCommand("aboutApp");
        about.addActionListener(commandProcessor);
        helpMenu.add(about);

        ImageIcon imageIcon;
        _toolbar = new JToolBar("Toolbar");
        //_toolbar.setPreferredSize(new Dimension(100,32));
        _toolbar.setRollover(true);

        imageIcon = new ImageIcon(cl.getResource("icons/new.png"));
        JButton newButton = new JButton(imageIcon);
        newButton.setToolTipText("New");
        newButton.setBorderPainted(false);
        newButton.setActionCommand("newConnection");
        newButton.addActionListener(commandProcessor);
        _toolbar.add(newButton);

        imageIcon = new ImageIcon(cl.getResource("icons/delete.png"));
        _deleteButton = new JButton(imageIcon);
        _deleteButton.setToolTipText("Delete");
        _deleteButton.setBorderPainted(false);
        _deleteButton.setActionCommand("deleteConnection");
        _deleteButton.addActionListener(commandProcessor);
        _deleteButton.setEnabled(false);
        _toolbar.add(_deleteButton);

        _toolbar.add(Box.createHorizontalStrut(8));
        _toolbar.addSeparator();
        _toolbar.add(Box.createHorizontalStrut(8));

        imageIcon = new ImageIcon(cl.getResource("icons/chat.png"));
        _chatButton = new JButton(imageIcon);
        _chatButton.setToolTipText("Chat");
        _chatButton.setBorderPainted(false);
        _chatButton.setActionCommand("chatConnection");
        _chatButton.addActionListener(commandProcessor);
        _chatButton.setEnabled(false);
        _toolbar.add(_chatButton);

        imageIcon = new ImageIcon(cl.getResource("icons/configure.png"));
        _configureButton = new JButton(imageIcon);
        _configureButton.setToolTipText("Configure");
        _configureButton.setBorderPainted(false);
        if( Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE) ){
            _configureButton.setActionCommand("configureConnection");
            _configureButton.addActionListener(commandProcessor);
        }
        _configureButton.setEnabled(false);
        _toolbar.add(_configureButton);

        _toolbar.add(Box.createHorizontalGlue());

        imageIcon = new ImageIcon(cl.getResource("icons/website.png"));
        JButton websiteButton = new JButton(imageIcon);
        websiteButton.setToolTipText("Open Website");
        websiteButton.setBorderPainted(false);
        if( Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE) ){
            websiteButton.setActionCommand("website");
            websiteButton.addActionListener(commandProcessor);
        }
        _toolbar.add(websiteButton);

        _console.setEditable(false);
        JScrollPane scrollConsole = new JScrollPane(_console);
        scrollConsole.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        //Create a split pane with the two scroll panes in it.
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,channelsPane,scrollConsole);
        splitPane.setOneTouchExpandable(true);
        splitPane.setResizeWeight(1);
        //splitPane.setDividerSize(10);

        //Set the menu bar and add the label to the content pane.
        setJMenuBar(menuBar);
        add(_toolbar, BorderLayout.PAGE_START);
        getContentPane().add(splitPane, BorderLayout.CENTER);

        //Display the window.
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        pack();
        setLocation(screenSize.width/2-getWidth()/2, screenSize.height/2-getHeight()/2);
        setVisible(true);

    }

    public void destroy(){
        _connectionManager.shutdown();
        try {
            _preferences.storeToXML(new FileOutputStream(new File(_applicationDirectory, "preferences-config.xml")),null);
        } catch (Exception e) {
            App.log(getClass().getSimpleName()+" could not save preferences.");
            e.printStackTrace();
        }
        if(_sis!=null) _sis.removeSingleInstanceListener(this);
        App.log("Shutting down");
    }

    public static File getApplicationDirectory(){
        return _applicationDirectory;
    }

    public static void log(String message){
        if( _console!=null ){
            _console.append(message+"\n");
            Document doc = _console.getDocument();
            if( doc.getLength()> 10000 ){                                                // limit console length to 10000 characters
                try {
                    doc.remove(0,doc.getLength()-10000);
                } catch (BadLocationException e) {
                    //this should never happen
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args){
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new App().init();
            }
        });

    }

    @Override
    public void newActivation(String[] arg0) {
    }
}
