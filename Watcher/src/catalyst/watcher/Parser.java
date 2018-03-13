package catalyst.watcher;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Parser implements LogListener, MessageParser{

    protected Matcher matcher = Pattern.compile("^(.*)$").matcher("");
    protected Connection _connection = null;

    public Parser(){
    }

    public void setConnection(Connection connection){
        _connection = connection;
    }

    @Override
    public Message buildMessage(String data) {
        Message m = new Message();
        matcher.reset(data);
        if( matcher.find()){
            m.speaker = "";
            m.text = matcher.group(1);
            m.time = System.currentTimeMillis();
            return m;
        } else {
            return null;
        }
    }

    @Override
    public void newLogEntry(String entry) {
        Message m = buildMessage(entry);
        if( m==null ) return;
        _connection.sendMessage(m);

    }

    public static void main(String[] args){
        Log log = new Log("test.txt");
        Parser p = new Parser();

        log.addLogListener(p);
    }

}
