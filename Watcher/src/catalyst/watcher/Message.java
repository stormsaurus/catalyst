package catalyst.watcher;

public class Message{

    public String speaker = "";
    public String text = "";
    public long time = 0;

    public String toString(){
        return ""+Object.class+", Time="+time+", Speaker="+speaker+", Text="+text;
    }

}
