package catalyst.watcher;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class ConnectionManager{

    private ArrayList<Connection> _connections = new ArrayList<Connection>();;
    private List<Connection> _connectionsView = (List<Connection>)Collections.unmodifiableList(_connections);;

    private HashMap<String,Server> _serversByName = new HashMap<String,Server>();
    private HashMap<String,Log> _logsByFileName = new HashMap<String,Log>(); //used to enforce single log

    private String _defaultSaveName = "connection-config.xml";
    private File _saveFile = null;

    private boolean _saveConnections = true;

    public ConnectionManager(){
        _serversByName.put("Carousel One", new Server());
        _saveFile = new File(App.getApplicationDirectory(), _defaultSaveName);
        _saveConnections = false; //loading many connections, don't save until done
        loadConnections();
        _saveConnections = true;
        saveConnections();
    }

    public void shutdown(){
        // TODO interate over connections and shut them down, so server can log them out
    }

    @SuppressWarnings("unchecked")
    private void loadConnections(){
        for( Connection c : _connections ) disposeConnection(c);                                    //dispose of all connections
        Set<ConnectionConfig> configs = new HashSet<ConnectionConfig>();                            //load up set of stored configs
        XMLDecoder xmld = null;
        try {
            xmld = new XMLDecoder( new BufferedInputStream( new FileInputStream(_saveFile)));
        } catch (FileNotFoundException e) {
            App.log(this.getClass().getSimpleName()+" could not find "+_defaultSaveName);
            //e.printStackTrace();
        }
        if( xmld!= null ) {
            try{
                configs = (Set<ConnectionConfig>)xmld.readObject();
                if( configs.contains(null)) {
                    configs = new HashSet<ConnectionConfig>();
                    App.log(this.getClass().getSimpleName()+" could not load connections from "+_defaultSaveName+".  Format may have changed.");
                } else {
                    for( ConnectionConfig config : configs ) createConnection(config); //iterate over configs and create new connections
                }
            } catch (Exception e){
                App.log(this.getClass().getSimpleName()+" could not load connections from "+_defaultSaveName+".  Format may have changed.");
            }
            xmld.close();
        }
    }
    protected void saveConnections(){
        if( !_saveConnections || _connections.size()==0 ) return; //don't do anything if there are no connections
        Set<ConnectionConfig> configs = new HashSet<ConnectionConfig>();                            //extract configs from current connections and save
        for(Connection c : _connections) configs.add(c.config);
        XMLEncoder xmle = null;
        try {
            xmle = new XMLEncoder( new BufferedOutputStream( new FileOutputStream(_saveFile)));
        } catch (FileNotFoundException e1) {
            App.log(this.getClass().getSimpleName()+" could not save connections to "+_defaultSaveName);
            e1.printStackTrace();
        }
        if( xmle!=null ){
            xmle.writeObject(configs);
            xmle.close();
        }

    }

    public List<Connection> getConnections(){
        return _connectionsView;
    }
    public boolean createConnection(ConnectionConfig config) {
        if( config==null ) {
            App.log(this.getClass().getSimpleName()+" could create connection.  Null configuration.");
            return false;
        }
        Connection c = new Connection(config); //save config on connection

        Log log = _logsByFileName.get(config.getLogFileName());                                        //listen to existing log in use
        if( log==null ) {
            //setup new log if one doesn't exist
            log = new Log(config.getLogFileName());
        }
        c.setLog(log);

        Server server = _serversByName.get("Carousel One"); //listen to existing log in use
        if( server==null ) {                                                                        //setup new log if one doesn't exist
            //TODO setup server reading
        }
        c.setServer(server);

        //connect everything
        c.start();
        _logsByFileName.put(config.getLogFileName(),log);                                           //remember log for use in other parsers (one Log to many Parser)
        _connections.add(c);                                                                        //remember connection
        saveConnections();                                                                          //save changes
        return true;
    }
    public void disposeConnection(Connection c){
        if( c==null ) return; //shouldn't happen, being safe
        if( !_connections.remove(c) ) {
            App.log(this.getClass().getSimpleName()+" could not dispose connection, it is unavailable.");
            return;
        }
        c.stop();                                                                                   //remove the parser from the log listener, so it knows when to close itself
        saveConnections();                                                                          //persist the changes
        return;
    }

    public static void main(String[] args){
    }
}
