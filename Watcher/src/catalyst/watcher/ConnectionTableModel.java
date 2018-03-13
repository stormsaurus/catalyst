package catalyst.watcher;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Timer;
import javax.swing.table.AbstractTableModel;

import org.apache.commons.lang.StringUtils;

@SuppressWarnings("serial")
public class ConnectionTableModel extends AbstractTableModel {

    private String[] columnNames = {"","Game","Channel","Room","","On"};
    private ConnectionManager connectionManager = null;

    private Timer _timer = null;
    private int _checkInterval = 5000; //5 secs

    private class CheckUpdates extends AbstractAction{
        public void actionPerformed(ActionEvent e){
            if( getRowCount()>0 ) fireTableRowsUpdated(0,getRowCount()-1);
        }
    }

    public ConnectionTableModel(ConnectionManager cm){
        connectionManager = cm;
        //setup timer to check for new entries
        _timer = new Timer(_checkInterval,new CheckUpdates());
        _timer.start();
    }

    @Override
    public int getColumnCount() {
        return 6;
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public int getRowCount() {
        return connectionManager.getConnections().size();
    }

    @Override
    public Object getValueAt(int row, int col) {
        Connection c = connectionManager.getConnections().get(row);
        switch (col){
            case 0: return c.getStatus();
            case 1: return c.config.getGame();
            case 2: return StringUtils.defaultString(c.config.getChannel());
            case 3: return StringUtils.defaultString(c.config.getRoom());
            case 4: return StringUtils.defaultString(c.getNote());
            case 5: return c.config.isActivate();
            default: return null;
        }
    }

    @SuppressWarnings("unchecked")
    public Class getColumnClass(int col) {
        switch (col){
            case 0: return Connection.Status.class;
            case 1: return Game.class;
            case 2: return String.class;
            case 3: return String.class;
            case 4: return String.class;
            case 5: return Boolean.class;
            default: return null;
        }
    }

    public boolean isCellEditable(int row, int col) {
        if( col==5 ) return true;
        return false;
    }

    public void setValueAt(Object value, int row, int col) {
        Connection c = connectionManager.getConnections().get(row);
        if( col==5 ) {
            boolean activate = (Boolean)value;
            c.config.setActivate(activate);
            connectionManager.saveConnections();
            if(activate) { c.start();
            } else { c.stop();    }
        }
        fireTableRowsUpdated(row, row);
    }

    public void createConnection(){
        createConnection(new ConnectionConfig());
    }
    public void createConnection(ConnectionConfig cc){
        connectionManager.createConnection(cc);
        int rowChange = connectionManager.getConnections().size();
        fireTableRowsInserted(rowChange-1, rowChange-1);
    }
    public void disposeConnection(Connection c){
        connectionManager.disposeConnection(c);
        fireTableDataChanged();
    }
    public void disposeConnections(int[] indicies){
        Set<Connection> s = new HashSet<Connection>();
        for(int i=0; i<indicies.length; i++){
            s.add(connectionManager.getConnections().get(indicies[i]));
        }
        for(Connection c : s) connectionManager.disposeConnection(c);
        fireTableDataChanged();
    }


}
