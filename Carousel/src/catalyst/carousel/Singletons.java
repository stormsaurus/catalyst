package catalyst.carousel;

import java.util.HashSet;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.apache.commons.lang.StringUtils;

import catalyst.carousel.irc.IRCManager;
import catalyst.carousel.irc.IRCNetwork;
import catalyst.carousel.irc.IRCServer;

public class Singletons {

    private static EntityManagerFactory _emf = null;
    private static WatcherService _watcher = null;
    private static UserManager _um = null;
    private static RoomManager _rm = null;
    private static IRCManager _ircm = null;

    private static Properties _preferences = null;

    private static Singletons _thisFactory = new Singletons(); //use null if you want to lazy load

    private Singletons(){

        _preferences = new Properties();
        try {
            _preferences.loadFromXML(this.getClass().getClassLoader().getResourceAsStream("/META-INF/preferences-dev.xml"));
        } catch (Exception e) {
            System.out.println(e);
            System.out.println(getClass().getSimpleName()+" could find or load preferences.  Using default.");
            _preferences.setProperty("carousel.user.root.userName", "root");
            _preferences.setProperty("carousel.user.root.password", "password");
        }

        //create singletons
        _emf = Persistence.createEntityManagerFactory("Carousel");
        _watcher = new WatcherService();
        _um = new UserManager();
        _rm = new RoomManager();
        _ircm = new IRCManager();

    }

    public static WatcherService getWatcherService(){
        return _watcher;
    }
    public static EntityManagerFactory getEntityManagerFactory(){
        return _emf;
    }
    public static UserManager getUserManager(){
        return _um;
    }
    public static RoomManager getRoomManager(){
        return _rm;
    }

    public static Properties getPreferences(){
        return _preferences;
    }

    public static synchronized Singletons getInstance(){
        if( _thisFactory==null ){
            _thisFactory = new Singletons();
        }
        return _thisFactory;
    }
    public static synchronized void destroy(){
        if(_thisFactory!=null){
            _watcher.destroy();
            _um.destroy();
            _rm.destroy();
            _ircm.destroy();
            _thisFactory = null;
        }
    }
}
