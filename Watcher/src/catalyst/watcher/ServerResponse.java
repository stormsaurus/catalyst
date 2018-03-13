package catalyst.watcher;

public class ServerResponse {

    public static enum Code { Success, Error, Unavailable };

    public Code code;
    public String message;
}
