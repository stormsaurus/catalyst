package catalyst.watcher;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.HashSet;

import javax.swing.AbstractAction;
import javax.swing.Timer;


public class Log {

    public static enum Status {Connected, Disconnected, Error};
    private Status _status = Status.Disconnected;
    private String _note = "";

    private boolean _active = false;
    @SuppressWarnings("unused")
    private int _lscCount = 0; //how many characters separate new lines
    private File _log = null;
    private String _logFileName = null;
    private LineNumberReader _lr = null;

    private HashSet<LogListener> _listeners = new HashSet<LogListener>();

    private Timer _timer = null;
    private int _checkInterval = 2000; //2 secs

    @SuppressWarnings("serial")
    private class CheckLog extends AbstractAction{
        public void actionPerformed(ActionEvent e){ checkLog(); }
    }

    public Log(String filename){
        _log = new File(filename);
        _logFileName = filename;
    }

    //start and stop are probably unneeded, but they conserve some resources for low end machines
    private boolean start(){
        //sanity checks and set character line separator count
        if( !_log.canRead() || !isLSCValid() ) {
            _note = this.getClass().getSimpleName()+" could not start. Could not read "+_logFileName+" or format is invalid.";
            _status = Log.Status.Error;
            App.log(_note);
            return false;
        }

        //open log file
        try {
            _lr = new LineNumberReader( new FileReader(_log) );
        } catch (FileNotFoundException e) {
            _note = this.getClass().getSimpleName()+" could not start. Could not open "+_logFileName+".";
            _status = Log.Status.Error;
            App.log(_note);
            e.printStackTrace();
            return false;
        }
        //skip ahead to the end
        try {
            while( _lr.readLine()!=null ) {}
        } catch (IOException e) {
            e.printStackTrace();
            try { _lr.close(); } catch (IOException e1) {e1.printStackTrace(); }
            _note = this.getClass().getSimpleName()+" could not start. Error reading "+_logFileName+".";
            _status = Log.Status.Error;
            App.log(_note);
            return false;
        }
        //setup timer to check for new entries
        _timer = new Timer(_checkInterval,new CheckLog());
        _timer.start();
        //return
        _note = this.getClass().getSimpleName()+" started for "+_logFileName+".";
        _status = Log.Status.Connected;
        //App.log(_note);
        return (_active = true);
    }
    private void stop(){
        if( _active==false ) return;
        try { _lr.close(); } catch (IOException e) {    e.printStackTrace(); }
        _timer.stop();
        _active = false;
        _note = "";
        _status = Log.Status.Disconnected;
        //App.log(this.getClass().getSimpleName()+" stopped for "+_logFileName+".");
    }

    public Log.Status getStatus(){
        return _status;
    }
    public String getNote(){
        return _note;
    }

    public String getLogFileName(){
        return _logFileName;
    }

    public void addLogListener(LogListener l){
        _listeners.add(l);
        if( _listeners.size() == 1 ) start();
    }
    public void removeLogListener(LogListener l){
        _listeners.remove(l);
        if( _listeners.size() == 0 ) stop();
    }

    private void checkLog(){
        String line;
        try {
            while((line=_lr.readLine()) !=null) {
                for( LogListener listener: _listeners ){
                    listener.newLogEntry(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            stop();
        }
    }

    private boolean isLSCValid(){
        BufferedReader br = null;
        try {
            br = new BufferedReader( new FileReader(_log) );
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }

        try{                                                                             //determine characters for line ending
            int lsc = 0;
            while( ((lsc=br.read())!=-1) ){
                if( lsc==(int)'\r' || lsc==(int)'\n' ){
                    int nextChar = br.read();
                    if( nextChar==-1 ) { _lscCount=1; break;}                              //line separator is r or n
                    if( lsc==(int)'\r' && nextChar==(int)'\n' )    { _lscCount=2; break;}     //line separator is rn
                }
            }
            br.close();
        } catch(IOException e){
            e.printStackTrace();
            try { br.close(); } catch (IOException e1) {e1.printStackTrace();}
            return false;
        }
        return true;
    }

    public static void main(String[] args){
        Log test = new Log("test.txt");
        LogListener x = new LogListener(){public void newLogEntry(String e){System.out.println(e);}};
        test.addLogListener(x);
    }

}
