package catalyst.carousel;

import java.awt.event.ActionEvent;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.RollbackException;
import javax.swing.AbstractAction;
import javax.swing.Timer;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;

public class UserManager {

    private Timer _sessionTimer = null;
    private Timer _registrationTimer = null;

    public static final int LASTSEEN_CACHESIZE;
    private Map<String,Long> _userSeenCache = new HashMap<String,Long>(LASTSEEN_CACHESIZE);

    static{
        int value = 1000;        // 1000 user default cachesize
        try{
            value = Integer.parseInt(StringUtils.defaultString(Singletons.getPreferences().getProperty("carousel.user.session.lastSeen.cacheSize")));
        } catch (NumberFormatException e){}
        LASTSEEN_CACHESIZE = value;
    }

    //TODO checking every tenth could error with bad config parms since swing timer uses int and durations are long
    @SuppressWarnings("serial")
    public UserManager(){
        //force register root user
        User u = new User();
        u.givenName = StringUtils.defaultString(Singletons.getPreferences().getProperty("carousel.user.root.givenName"));
        u.surname = StringUtils.defaultString(Singletons.getPreferences().getProperty("carousel.user.root.surname"));
        u.email = StringUtils.defaultString(Singletons.getPreferences().getProperty("carousel.user.root.email"));
        u.userName = Singletons.getPreferences().getProperty("carousel.user.root.userName");
        u.password = Singletons.getPreferences().getProperty("carousel.user.root.password");
        try{
            register(u, false);
        } catch (UserException e){
            //this happens if user is already registered
            //TODO log this if not user already exists
        }

        _sessionTimer = new Timer((int)UserSession.DURATION/100,new AbstractAction(){                        //check every 1/10 of the duration
            @Override public void actionPerformed(ActionEvent arg0) { expireSessions(); }
        });
        _sessionTimer.start();
        _registrationTimer = new Timer((int)UserVerificationTicket.duration/10,new AbstractAction(){        //check every 1/10 of the duration
            @Override public void actionPerformed(ActionEvent arg0) { expireRegistrations(); }
        });
        _registrationTimer.start();

    }

    public String login(String userName, String password) throws UserException{
        if( userName==null || password==null ) throw new UserException("User invalid.");
        String uniqueKey = null;
        User user = null;
        UserSession session = null;
        EntityManager em = Singletons.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try{
            tx.begin();
            user = em.find(User.class, userName);
            if( user==null ) throw new UserException("User not found.");
            if( !user.active ) throw new UserException("User not verified.");
            if( user.session!=null ) return user.session.sessionKey;
            session = new UserSession();
            user.session = session;
            session.user = user;
            do{
                uniqueKey = RandomStringUtils.randomAlphanumeric(16);
            } while(em.find(UserSession.class, uniqueKey)!=null);
            session.sessionKey = uniqueKey;
            em.persist(session);
            tx.commit();
        } catch (EntityExistsException e){
            System.out.println(e);
            //TODO log exception
            throw new UserException("User session already exists.",e);
        } catch (RollbackException e){
            //TODO log exception
            throw new UserException("Data access error.",e);
        } finally {
            if( tx!=null && tx.isActive() ) tx.rollback();
            em.close();
        }
        return uniqueKey;
    }
    public void logout(String userName){
        EntityManager em = Singletons.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try{
            tx.begin();
            User user = em.find(User.class, userName);
            if( user.session==null ) return;
            em.remove(user.session);
            user.session=null;
            tx.commit();
        } catch (RollbackException e){
            //TODO log exception
        } finally {
            if( tx!=null && tx.isActive() ) tx.rollback();
            em.close();
        }
    }
    public String register(User user) throws UserException{
        return this.register(user, true);
    }
    public String register(User user, boolean authenticate) throws UserException{
        if( user==null ) throw new UserException("User invalid.");
        if( !isValidUser(user) ) throw new UserException("User invalid.");
        String uniqueKey = null;
        EntityManager em = Singletons.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try{
            tx.begin();
            if( em.find(User.class, user.userName)!=null ) throw new UserException("User already exists.");
            UserVerificationTicket ticket = null;
            if( authenticate ) {                                                                        //flag active to false if authenticate is true
                user.active=false;
                do{
                    uniqueKey = RandomStringUtils.randomAlphanumeric(16);
                } while(em.find(UserVerificationTicket.class, uniqueKey)!=null);
                ticket = new UserVerificationTicket();
                ticket.user = user;
                ticket.verificationKey = uniqueKey;
            } else {
                user.active = true;
            }
            em.persist(user);
            if(ticket!=null) em.persist(ticket);
            tx.commit();
        } catch (EntityExistsException e){
            System.out.println(e);
            //TODO log exception
            throw new UserException("User already exists.",e);
        } catch (RollbackException e){
            //TODO log exception
            throw new UserException("Data access error.",e);
        } finally {
            if( tx!=null && tx.isActive() ) {uniqueKey=null; tx.rollback();}
            em.close();
        }
        return uniqueKey;
    }
    public boolean authenticateTicket(String key) throws UserException{
        EntityManager em = Singletons.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try{
            tx.begin();
            UserVerificationTicket ticket = em.find(UserVerificationTicket.class,key);
            if( ticket==null ) throw new UserException("User key invalid.");
            if( !ticket.verificationKey.equals(key) ) throw new UserException("User key invalid.");
            ticket.user.active = true;
            em.remove(ticket);
            tx.commit();
        } catch (RollbackException e){
            //TODO log exception
            throw new UserException("Data access error.",e);
        } finally {
            if( tx!=null && tx.isActive() ) tx.rollback();
            em.close();
        }
        return true;
    }
    public boolean isUserNameAvailable(String userName){
        if( userName==null ) return false;                                                            //TODO need to check if name is valid
        EntityManager em = Singletons.getEntityManagerFactory().createEntityManager();
        try{
            if( em.find(User.class,userName)==null ) return true;
        } finally {
            em.close();
        }
        return false;
    }
    public boolean update(User userUpdate) throws UserException{
        if( userUpdate==null ) throw new UserException("User invalid.");
        if( !isValidUser(userUpdate) ) throw new UserException("User information invalid.");
        EntityManager em = Singletons.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try{
            tx.begin();
            User user = em.find(User.class,userUpdate.userName);
            if( user==null ) throw new UserException("User not found.");
            if( !StringUtils.defaultString(userUpdate.givenName).equals("") )     user.givenName = userUpdate.givenName;
            if( !StringUtils.defaultString(userUpdate.surname).equals("") )     user.surname = userUpdate.surname;
            if( !StringUtils.defaultString(userUpdate.email).equals("") )         user.email = userUpdate.email;
            if( !StringUtils.defaultString(userUpdate.password).equals("") )     user.password = userUpdate.password;
            if( userUpdate.nicknames!=null && user.nicknames.size()!=0 )        user.nicknames = userUpdate.nicknames;
            tx.commit();
        } catch (RollbackException e){
            //TODO log exception
            throw new UserException("Data access error.",e);
        } finally {
            if( tx!=null && tx.isActive() ) tx.rollback();
            em.close();
        }
        return true;
    }
    public boolean userSeen(String userName){
        return userSeen(userName, true);
    }
    public boolean userSeen(String userName, boolean cache){
        if( userName==null ) return false;

        _userSeenCache.put(userName, System.currentTimeMillis());
        if( cache ){
            if( _userSeenCache.size()>LASTSEEN_CACHESIZE ) flushUserSeenCache();
        } else {
            flushUserSeenCache();
        }
        return true;
    }
    public void destroy(){
        flushUserSeenCache();
        //TODO destroy all usersessions?
    }

    //note that this method will eat errors, e.g. if a user is deleted during call a last seen will be dropped from cache
    //TODO does using that many transactions eat resources?
    private void flushUserSeenCache(){
        if( _userSeenCache.size()==0 ) return;
        EntityManager em = Singletons.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx;
        try{
            for( String userName : _userSeenCache.keySet() ){
                tx = em.getTransaction();
                try{
                    tx.begin();
                    User user = em.find(User.class,userName);
                    if( user!=null && user.session!=null ) user.session.lastSeen = new Date(_userSeenCache.get(userName));
                    tx.commit();
                } catch (RollbackException e){
                    //TODO log exception
                } finally {
                    if( tx!=null && tx.isActive() ) tx.rollback();
                }
            }
        } finally {
            em.close();
            _userSeenCache.clear();
        }
    }

    private boolean isValidUser(User user){
        if( user==null ) return false;
        if( (user.userName.length()<8 || !StringUtils.isAlphanumeric(user.userName) )
                && !user.userName.equals(Singletons.getPreferences().getProperty("carousel.user.root.userName")) ) return false;
        if( (user.password.length()<8 || !StringUtils.isAlphanumeric(user.password) )
                && !user.password.equals(Singletons.getPreferences().getProperty("carousel.user.root.password")) ) return false;
        if( user.registeredOn==null || user.email==null || user.givenName==null || user.surname==null ) return false;
        return true;
    }
    private void expireSessions() {
        flushUserSeenCache();
        EntityManager em = Singletons.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try{
            tx.begin();
            int count = em.createNamedQuery("deleteExpiredSessions")
                                                .setParameter("expires",new Date(System.currentTimeMillis()-UserSession.DURATION))
                                                .executeUpdate();
            tx.commit();
            System.out.println("expiring "+count+" sessions");
        } catch (RollbackException e){
            //TODO log exception
            throw new UserException("Data access error.",e);
        }  finally {
            em.close();
        }
    }
    private void expireRegistrations(){
        EntityManager em = Singletons.getEntityManagerFactory().createEntityManager();
        EntityTransaction tx = em.getTransaction();
        try{
            tx.begin();
            int count = em.createNamedQuery("deleteExpiredVerificationTickets")
                                                .setParameter("expires",new Date(System.currentTimeMillis()-UserVerificationTicket.duration))
                                                .executeUpdate();
            tx.commit();
            System.out.println("expiring "+count+" registrations");
        } catch (RollbackException e){
            //TODO log exception
            throw new UserException("Data access error.",e);
        } finally {
            em.close();
        }
    }

    public static void main(String[] args){
        UserManager um = Singletons.getUserManager();
        System.out.println("root avail: "+um.isUserNameAvailable("root"));
        System.out.println("test avail: "+um.isUserNameAvailable("test1234"));
        try{
            System.out.println("root login " + um.login("root", "password"));
        } catch(UserException e){
            System.out.println(e.getMessage());
        }
        User u = new User();
        u.userName = "test1234";
        u.password = "password";
        try{
            System.out.println("register test1234 " + um.register(u));
        } catch(UserException e){
            System.out.println(e.getMessage());
        }
        System.out.println("root seen " + um.userSeen("root"));
        um.logout("root");
        Singletons.destroy();
    }

}
