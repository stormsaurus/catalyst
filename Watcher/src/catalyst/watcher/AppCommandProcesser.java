package catalyst.watcher;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.URI;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class AppCommandProcesser implements ActionListener, ListSelectionListener, WindowListener, MouseListener, ItemListener {

    private App _app = null;
    private ConnectionConfigurationEditor _connectionConfigurationEditor = new ConnectionConfigurationEditor();
    private JFrame _aboutFrame = null;

    //_app._trayIcon.displayMessage("Event","Event!",TrayIcon.MessageType.INFO);  //use this if need to display something in the tray

    public AppCommandProcesser(App frame){
        _app = frame;
        _aboutFrame = new AboutFrame();
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        ConnectionTableModel connectionTableModel = (ConnectionTableModel)_app._connectionTable.getModel();
        String command = event.getActionCommand();
        if( command.equals("newConnection") ) {
            connectionTableModel.createConnection();
        } else if( command.equals("deleteConnection") ){
            if( _app._connectionTable.getSelectedRowCount()==0 ) return;
            int option = JOptionPane.showConfirmDialog(_app,"Delete selected connections?","Confirm",JOptionPane.OK_CANCEL_OPTION,JOptionPane.WARNING_MESSAGE);
            if( option==0 )    connectionTableModel.disposeConnections(_app._connectionTable.getSelectedRows());
        } else if( command.equals("configureConnection") ){
            configureConnection();
        } else if( command.equals("aboutApp")){
            _aboutFrame.setVisible(true);
        } else if( command.equals("chatConnection")){
            try {
                Desktop.getDesktop().browse(new URI("http://flux.io/chat?"));
            } catch (Exception e) {
                App.log("Desktop couldn't browse the address.");
                e.printStackTrace();
            }
        } else if( command.equals("website")){
            try {
                Desktop.getDesktop().browse(new URI("http://flux.io/"));
            } catch (Exception e) {
                App.log("Desktop couldn't browse the address.");
                e.printStackTrace();
            }
        } else if( command.equals("exit")){
            closeApp();
        }

    }

    @Override
    public void valueChanged(ListSelectionEvent event) {
        if( !_app._connectionTable.getSelectionModel().isSelectionEmpty() ){
            _app._deleteButton.setEnabled(true);
            _app._configureButton.setEnabled(true);
            _app._chatButton.setEnabled(true);
            _app._deleteConnection.setEnabled(true);
            _app._configureConnection.setEnabled(true);
        } else {
            _app._deleteButton.setEnabled(false);
            _app._configureButton.setEnabled(false);
            _app._chatButton.setEnabled(false);
            _app._deleteConnection.setEnabled(false);
            _app._configureConnection.setEnabled(false);
        }

    }

    private void configureConnection(){
        int[] selectedRows = _app._connectionTable.getSelectedRows();
        if( selectedRows.length==0 ) return;
        _connectionConfigurationEditor.edit(_app._connectionManager.getConnections().get(selectedRows[0]), (ConnectionTableModel)_app._connectionTable.getModel());
    }

    private void closeApp(){
        _app.destroy();
        System.exit(0);
    }

    @Override
    public void windowActivated(WindowEvent e) {}

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
        //if( _app._trayIcon==null ){                                                                                //if we don't have a system tray close the app
            closeApp();
        //} else {
        //    _app.setVisible(false);                                                                                //if we do minimize to it
        //}
    }

    @Override
    public void windowDeactivated(WindowEvent e) {}

    @Override
    public void windowDeiconified(WindowEvent e) {}

    @Override
    public void windowIconified(WindowEvent e) {
        if( _app._trayIcon!=null ){
            _app.setVisible(false);
        }
    }

    @Override
    public void windowOpened(WindowEvent e) {}

    @Override
    public void mouseClicked(MouseEvent e) {
        Object source = e.getSource();
        if( source==_app._connectionTable ){
            if ( e.getClickCount() == 2 ) {
                configureConnection();
            }
        } else if (_app._trayIcon!=null && source==_app._trayIcon){
            if ( e.getButton() == MouseEvent.BUTTON1 ) {
                _app.setState(JFrame.NORMAL);
                _app.setVisible(true);
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent arg0) {

    }

    @Override
    public void mouseExited(MouseEvent arg0) {

    }

    @Override
    public void mousePressed(MouseEvent arg0) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.DESELECTED){
            _app._toolbar.setVisible(false);
        } else {
            _app._toolbar.setVisible(true);
        }

    }



}
