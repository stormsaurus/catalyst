package catalyst.watcher;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class ConnectionConfigurationEditor extends JFrame implements ActionListener{

    private JComboBox _gameList = null;
    private JComboBox _channelList = null;
    private JComboBox _serverList = null;

    private JTextField _roomName = null;
    private JTextField _roomKey = null;
    private JTextField _logFileName = null;

    private JButton _okButton = null;
    private JButton _cancelButton = null;
    private JButton _logFileButton = null;

    private JFileChooser _logFileChooser = new JFileChooser();

    private Connection _connection = new Connection();
    private ConnectionConfig _newConfig = new ConnectionConfig();
    private ConnectionTableModel _connectionModel = null;

    // TODO group fields under source and destination
    // TODO pick nickname
    public ConnectionConfigurationEditor(){

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        getContentPane().setLayout(gridbag);
        c.insets = new Insets(5,5,5,5);

        JLabel gameLabel = new JLabel("Game");
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.LINE_END;
        c.weightx = 0;
        c.weighty = 0;
        c.gridx = 0;
        c.gridy = 0;
        getContentPane().add(gameLabel,c);
        _gameList = new JComboBox(Game.values());
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 1;
        c.weighty = 0;
        c.gridx = 1;
        c.gridy = 0;
        _gameList.setActionCommand("gameChange");
        _gameList.addActionListener(this);
        getContentPane().add(_gameList,c);

        JLabel channelLabel = new JLabel("Channel");
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.LINE_END;
        c.weightx = 0;
        c.weighty = 0;
        c.gridx = 0;
        c.gridy = 1;
        getContentPane().add(channelLabel,c);
        _channelList = new JComboBox(((Game)_gameList.getSelectedItem()).getChannels().toArray());
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0;
        c.weighty = 1;
        c.gridx = 1;
        c.gridy = 1;
        //_channelList.setActionCommand("channelChange");
        //_channelList.addActionListener(this);
        getContentPane().add(_channelList,c);

        JLabel serverLabel = new JLabel("Chat Server");
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.LINE_END;
        c.weightx = 0;
        c.weighty = 0;
        c.gridx = 0;
        c.gridy = 2;
        getContentPane().add(serverLabel,c);
        _serverList = new JComboBox(new String[]{"Giant Robot"});
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0;
        c.weighty = 1;
        c.gridx = 1;
        c.gridy = 2;
        //serverList.setActionCommand("serverChange");
        //serverList.addActionListener(this);
        getContentPane().add(_serverList,c);

        JLabel roomNameLabel = new JLabel("Room Name");
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.LINE_END;
        c.weightx = 0;
        c.weighty = 0;
        c.gridx = 0;
        c.gridy = 3;
        //getContentPane().add(roomNameLabel,c);
        _roomName = new JTextField("");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0;
        c.weighty = 1;
        c.gridx = 1;
        c.gridy = 3;
        //roomName.setActionCommand("roomNameChange");
        //roomName.addActionListener(this);
        //getContentPane().add(_roomName,c);

        JLabel roomKeyLabel = new JLabel("Room Key");
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.LINE_END;
        c.weightx = 0;
        c.weighty = 0;
        c.gridx = 0;
        c.gridy = 3;
        getContentPane().add(roomKeyLabel,c);
        _roomKey = new JTextField("");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0;
        c.weighty = 1;
        c.gridx = 1;
        c.gridy = 3;
        //roomKey.setActionCommand("roomKeyChange");
        //roomKey.addActionListener(this);
        getContentPane().add(_roomKey,c);

        JLabel logFileNameLabel = new JLabel("Log File");
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.LINE_END;
        c.weightx = 0;
        c.weighty = 0;
        c.gridx = 0;
        c.gridy = 4;
        getContentPane().add(logFileNameLabel,c);
        _logFileName = new JTextField("");
        _logFileName.setColumns(80);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 0;
        c.weighty = 1;
        c.gridx = 1;
        c.gridy = 4;
        //logFileName.setActionCommand("logFileChange");
        //logFileName.addActionListener(this);
        getContentPane().add(_logFileName,c);

        _logFileButton = new JButton("Browse");
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.LINE_START;
        c.weightx = 0;
        c.weighty = 0;
        c.gridx = 2;
        c.gridy = 4;
        _logFileButton.setActionCommand("logFileButton");
        _logFileButton.addActionListener(this);
        getContentPane().add(_logFileButton,c);

        JPanel buttonPanel = new JPanel();
        _okButton = new JButton("OK");
        _okButton.setActionCommand("okButton");
        _okButton.addActionListener(this);
        buttonPanel.add(_okButton);
        _cancelButton = new JButton("Cancel");
        _cancelButton.setActionCommand("cancelButton");
        _cancelButton.addActionListener(this);
        buttonPanel.add(_cancelButton);
        c.fill = GridBagConstraints.NONE;
        c.anchor = GridBagConstraints.CENTER;
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.weightx = 0;
        c.weighty = 0;
        c.gridx = 0;
        c.gridy = 5;
        getContentPane().add(buttonPanel,c);

        setEditorToConfig();
        pack();
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        setLocation(screenSize.width/2-getWidth()/2, screenSize.height/2-getHeight()/2);
        setTitle("Editor");

    }

    public void edit(Connection c, ConnectionTableModel cm){
        if( c==null || cm==null ) return;
        _connection = c;
        _newConfig = (ConnectionConfig)c.config.clone();
        _connectionModel = cm;
        setEditorToConfig();
        setVisible(true);
    }

    private void setEditorToConfig(){
        _gameList.setSelectedItem(_newConfig.getGame());
        _channelList.setSelectedItem(_newConfig.getChannel());
        _roomKey.setText(_newConfig.getKey());
        _logFileName.setText(_newConfig.getLogFileName());
        _roomName.setText(_newConfig.getRoom());
        _serverList.setSelectedItem(_newConfig.getServerName());
    }
    private void setConfigToEditor(){
        _newConfig.setChannel((String)_channelList.getSelectedItem());
        _newConfig.setGame((Game)_gameList.getSelectedItem());
        _newConfig.setKey(_roomKey.getText());
        _newConfig.setLogFileName(_logFileName.getText());
        _newConfig.setRoom(_roomName.getText());
        _newConfig.setServerName((String)_serverList.getSelectedItem());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if( command.equals("gameChange") ){
            Game game = (Game)_gameList.getSelectedItem();
            _channelList.removeAllItems();
            for( String channel : game.getChannels() ) _channelList.addItem(channel);
            _logFileName.setText(game.getDefaultFileLocation());
        } else if( command.equals("logFileButton") ) {
            int returnVal = _logFileChooser.showOpenDialog(this);
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                try {
                    _logFileName.setText(_logFileChooser.getSelectedFile().getCanonicalPath());
                } catch (IOException e1) {
                    App.log(getClass().getSimpleName()+" could get canonical path reference to log file.");
                    e1.printStackTrace();
                }
            }
        } else if( command.equals("okButton") ) {
            setConfigToEditor();
            _connectionModel.disposeConnection(_connection);
            _connectionModel.createConnection(_newConfig);
            setVisible(false);
        } else if( command.equals("cancelButton") ) {
            setVisible(false);
        }

    }
}
