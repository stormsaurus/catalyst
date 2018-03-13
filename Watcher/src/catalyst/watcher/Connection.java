package catalyst.watcher;

import java.lang.reflect.Constructor;

public class Connection {

    public static enum Status {Disconnected, Connected, Error};
    private Status _status = Status.Disconnected;
    private String _note = "";

    private Server _server = null;
    private Parser _parser = null;
    private Log _log = null;
    private boolean _active = false;
    private long _messageCount = 0;

    protected ConnectionConfig config = null;

    public Connection(){
        this(new ConnectionConfig());
    }
    public Connection(ConnectionConfig config){
        this.config = config;
    }

    public void sendMessage(Message m){
        if( m==null || _server==null) return;
        _messageCount++;
        _server.sendMessage(this, m);
    }

    protected void setLog(Log log){                                                            //this should be set before start is called
        if( log==null ) return;
        stop();
        _log = log;
    }
    protected void setServer(Server server){                                                //this should be set before start is called
        if( server==null ) return;
        stop();
        _server = server;
    }

    public boolean start(){
        if( _active || !config.isActivate() ) return false;
        if( _log==null || _server==null ) {
            _status = Status.Error;
            _note = this.getClass().getSimpleName()+" could not start.  Server or log is null.";
            App.log(_note);
            return false;
        }

        Class<Parser> parserClass = config.getGame().getParserClass();                        //setup parser
        _parser = null;
        if( ChannelParser.class.isAssignableFrom(parserClass)){                                //if it's a channel parser send channel info to constructor
            Constructor<Parser> constructor = null;
            try {
                constructor = parserClass.getConstructor(new Class[]{String.class});
                _parser = constructor.newInstance(new Object[]{config.getChannel()});
            } catch (Exception e) {
                _status = Status.Error;
                _note = this.getClass().getSimpleName()+" could not create "+parserClass.getSimpleName();
                App.log(_note);
                e.printStackTrace();
            }
        } else {                                                                            //otherwise just create a simple parser
            try {
                _parser = parserClass.newInstance();
            } catch (Exception e) {
                _status = Status.Error;
                _note = this.getClass().getSimpleName()+" could not create "+parserClass.getSimpleName();
                App.log(_note);
                e.printStackTrace();
            }
        }

        _log.addLogListener(_parser);                                                        //connect parser to log
        _parser.setConnection(this);
        //send connection message to server
        if( true ){
            _status = Status.Connected;
            _note = "";
        }
        _active = true;
        return true;
    }
    public void stop(){
        //send disconnect message to server
        _status = Status.Disconnected;
        _note = "";
        _messageCount = 0;
        if( _log!=null && _parser!=null ) _log.removeLogListener(_parser);                    //remove the parser from the log listener, so it knows when to close itself
        _active = false;
    }

    public Status getStatus() {
        if( _log==null || _server==null || _parser==null) return Connection.Status.Disconnected;

        if( _status==Connection.Status.Disconnected || _log.getStatus()==Log.Status.Disconnected    || _server.getStatus()==Server.Status.Disconnected  ) return Connection.Status.Disconnected;

        if( _status==Connection.Status.Error || _log.getStatus()==Log.Status.Error || _server.getStatus()==Server.Status.Error  ) return Connection.Status.Error;

        return Connection.Status.Connected;
    }
    public String getNote() {
        if( _status==Connection.Status.Connected && _log.getStatus()==Log.Status.Connected        && _server.getStatus()==Server.Status.Connected  ) return ""+_messageCount+" messages";

        if( _status==Connection.Status.Disconnected || _log.getStatus()==Log.Status.Disconnected || _server.getStatus()==Server.Status.Disconnected ) return "Disconnected";

        if( _status==Connection.Status.Error ) return _note;

        if( _log.getStatus()==Log.Status.Error ) return _log.getNote();

        if( _server.getStatus()==Server.Status.Error ) return _server.getNote();

        return "";
    }

}
