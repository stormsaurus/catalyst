package catalyst.watcher;

import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;

public class ChannelParser extends Parser{

    protected SortedSet<String> channels = new TreeSet<String>();
    protected SortedSet<String> channelsView = Collections.unmodifiableSortedSet(channels);
    protected String channel = "";

    public ChannelParser(){
    }

    public ChannelParser(String channel){
        if( channels.contains(channel) ) this.channel = channel;
    }

    @Override
    public Message buildMessage(String data) {
        Message m = new Message();
        matcher.reset(data);
        if( matcher.find()){
            m.speaker = "";
            m.text = matcher.group(1);
            m.time = System.currentTimeMillis();
            return m;
        } else {
            return null;
        }
    }

    public SortedSet<String> getAvailableChannelNames(){
        return channelsView;
    }


}
