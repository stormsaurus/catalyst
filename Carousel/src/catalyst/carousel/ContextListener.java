package catalyst.carousel;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.jabsorb.JSONRPCBridge;

public class ContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        //maintain a reference to this so it isn't gc-ed
        sce.getServletContext().setAttribute("catalyst.carousel.Singletons", Singletons.getInstance());
        //put the watcher service in the JSONRPC bridge
        JSONRPCBridge.getGlobalBridge().registerObject("service", Singletons.getWatcherService());

        //alternate way
        /*
        try {
            Context envCtx = (Context) (new InitialContext()).lookup("java:comp/env");
            singletonFactory = (SingletonFactory) envCtx.lookup("bean/SingletonFactory");
        } catch (NamingException e) {
            log("JNDI lookup on the ServiceFactory failed.  This is bad Ray, so we are not starting.");
            e.printStackTrace();
            throw new ServletException(e);
        }
        */
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        Singletons.destroy();
    }


}
