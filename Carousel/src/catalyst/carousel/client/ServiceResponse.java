package catalyst.carousel.client;

import java.util.HashMap;
import java.util.Map;

public class ServiceResponse {

    public static final int OK = 0;
    public static final int ERROR = 2;
    public static final int SESSION_TIMEOUT = 4;

    private int _type = 0;
    private String _message = "";
    private Map<String,String> _data = null;

    public void setCode(int type) { _type = type; }
    public int getCode() { return _type; }

    public void setMessage(String message) { _message = message; }
    public String getMessage() { return _message; }

    public void setData(Map<String,String> data) { _data=data; }
    public Map<String,String> getData() { return _data; }

    public void put(String key, String value) {
        if( _data==null ) _data=new HashMap<String,String>();
        _data.put(key, value);
    }
    public String get(String key) {
        if( _data==null ) return null;
        return _data.get(key);
    }

}
