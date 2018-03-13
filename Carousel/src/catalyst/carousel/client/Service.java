package catalyst.carousel.client;

public interface Service {

    public ServiceResponse login();
    public ServiceResponse login(String name, String password);

    public ServiceResponse sendMessage(String session, String roomKey, String source, String context, String speaker, String text, long time);
    public ServiceResponse sendMessages(Object[] messages);

    public void logout();

    //public String getMode(String room, String channel);
}
