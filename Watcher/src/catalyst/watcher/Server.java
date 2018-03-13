package catalyst.watcher;

import org.jabsorb.client.Client;
import org.jabsorb.client.ClientError;
import org.jabsorb.client.TransportRegistry;

import catalyst.carousel.client.Service;
import catalyst.carousel.client.ServiceResponse;

public class Server{

    // TODO add buffer for messages
    // TODO add timer that periodically flushes the buffer
    // TODO add flush method
    // TODO query if connection is in standby mode
    // TODO retry login once if watcher times out

    public static enum Status {Connected, Disconnected, Error};
    private Status _status = Status.Connected;
    private String _note = "";

    private TransportRegistry _registry = new TransportRegistry();
    private Client _client = null;
    private Service _test = null;

    public Server(){
        //setup Service Proxy
        _client = new Client(_registry.createSession("http://localhost:8080/carousel/service"));
        _test = (Service)_client.openProxy("service", Service.class);

        //try to login
        try{
            ServiceResponse sr = _test.login("Anonymous","");
            System.out.println(sr.getCode());
        } catch (ClientError e){
            _note = this.getClass().getSimpleName()+" "+e.getMessage();
            _status = Server.Status.Error;
            App.log(_note);
        }
    }

    //TODO optimize step, remove some fields from Message object and read them right off the connection when sending to server, less object creation
    //TODO this breaks if I don't use a channel parser because I am using getChannel
    public void sendMessage(Connection c, Message m){
        try{
            _test.sendMessage("", c.config.getKey(), c.config.getGame().getShortName(), c.config.getChannel(), m.speaker, m.text, m.time);
        } catch (ClientError e){
            _note = this.getClass().getSimpleName()+" "+e.getMessage();
            _status = Server.Status.Error;
            App.log(_note);
        }
    }

    public Status getStatus(){
        return _status;
    }
    public String getNote(){
        return _note;
    }

}
