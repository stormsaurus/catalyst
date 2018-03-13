package catalyst.watcher;

public enum OS {

    Windows, Mac, Other;

    public static OS get(){
        String os = System.getProperty("os.name");
        if( os.contains("Windows") ) return Windows;
        if( os.contains("Mac") ) return Mac;
        return Other;
    }

}
