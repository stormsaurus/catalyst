package catalyst.carousel.irc;

public class IRCException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public IRCException(String message){
        super(message);
    }
    public IRCException(String message, Throwable e){
        super(message,e );
    }
}
