package catalyst.carousel.irc;

import java.io.IOException;
import java.util.HashSet;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;

import catalyst.carousel.Singletons;


public class IRCManager {

    private WoWBot  bot = null;

    public IRCManager(){

        EntityManager em = Singletons.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        IRCNetwork defaultNet = null;
        try{
            tx.begin();
            defaultNet = em.find(IRCNetwork.class, "QuakeNet");
            if( defaultNet==null ) {
                defaultNet = new IRCNetwork();
                defaultNet.name = "QuakeNet";
                defaultNet.website = "http://www.quakenet.org/";
                HashSet<IRCServer> servers = new HashSet<IRCServer>();
                IRCServer server1 = new IRCServer();
                server1.network = defaultNet;
                server1.country = "United States";
                server1.server = "gameservers.tx.us.quakenet.org";
                servers.add(server1);
                defaultNet.servers = servers;
                em.persist(defaultNet);
            }
            tx.commit();
        } finally {
            if( tx!=null && tx.isActive() ) tx.rollback();
            em.close();
        }


        bot = new WoWBot();
        bot.setVerbose(true);
        try {
            bot.connect("gameservers.il.us.quakenet.org");
        } catch (NickAlreadyInUseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IrcException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void sendMessage(){

    }

    public void destroy(){
        bot.disconnect();
        bot.dispose();
    }


}
