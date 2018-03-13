package catalyst.carousel;

import catalyst.carousel.client.Service;
import catalyst.carousel.client.ServiceResponse;
import catalyst.carousel.irc.WoWBot;

public class WatcherService implements Service{

    WoWBot bot = null;

    public WatcherService(){
/*
        bot = new WoWBot();
        //bot.setVerbose(true);
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
        bot.joinChannel("#carousel");
*/
    }

    @Override
    public ServiceResponse login() {
        ServiceResponse sr = new ServiceResponse();
        sr.setCode(ServiceResponse.OK);
        System.out.println("login");
        return sr;
    }

    @Override
    public ServiceResponse login(String name, String password) {
        ServiceResponse sr = new ServiceResponse();
        sr.setCode(ServiceResponse.OK);
        System.out.println("login: name="+name+", password="+password);
        return sr;
    }

    @Override
    public void logout() {
        System.out.println("logout");
    }

    @Override
    public ServiceResponse sendMessage(String session, String roomKey, String source, String context,
            String speaker, String text, long time) {
        ServiceResponse sr = new ServiceResponse();
        sr.setCode(ServiceResponse.OK);
        if( bot!=null ) bot.sendMessage("#carousel", text);
        System.out.println("message: session="+session+", roomkey="+roomKey+", source="+source+", context="+context+", speaker="+speaker+", text="+text+", time="+time);
        return sr;
    }

    @Override
    public ServiceResponse sendMessages(Object[] messages) {
        ServiceResponse sr = new ServiceResponse();
        sr.setCode(ServiceResponse.OK);
        System.out.println("messages");
        return sr;
    }

    public void destroy(){
        //TODO destroy, clean up Bot etc
    }

}
