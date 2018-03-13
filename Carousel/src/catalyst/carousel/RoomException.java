package catalyst.carousel;

public class RoomException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RoomException(String message){
        super(message);
    }
    public RoomException(String message, Throwable e){
        super(message,e );
    }
}
