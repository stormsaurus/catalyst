package catalyst.watcher;

public class ConnectionConfig implements Cloneable{

    private String serverName = "Giant Robot";
    private String logFileName = "";
    private Game game = Game.Other;
    private String channel = "";
    private String room = "";
    private String key = "";
    private boolean activate = false;

    public Object clone(){
        Object copy = null;
        try {
            copy = super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return copy;
    }

    public void setServerName(String serverName) { this.serverName = serverName; }
    public String getServerName() { return serverName; }

    public void setLogFileName(String logFileName) { this.logFileName = logFileName; }
    public String getLogFileName() { return logFileName; }

    public void setGame(Game game) { this.game = game; }
    public Game getGame() { return game; }

    public void setChannel(String channel) { this.channel = channel; }
    public String getChannel() { return channel; }

    public void setRoom(String room) { this.room = room; }
    public String getRoom() { return room; }

    public void setKey(String key) { this.key = key; }
    public String getKey() { return key; }

    public void setActivate(boolean active) { this.activate = active; }
    public boolean isActivate() { return activate; }

}
