package catalyst.watcher.parsers;

import java.util.regex.Pattern;

import catalyst.watcher.ChannelParser;
import catalyst.watcher.Message;



public class WoWChannel extends ChannelParser{

    public WoWChannel(){
        this("");
    }

    public WoWChannel(String channel){
        super();
        //predefined channels
        channels.add("Guild");
        channels.add("Party");
        channels.add("Raid");
        channels.add("Raid Leader");
        channels.add("Raid Warning");
        channels.add("Battleground");
        channels.add("General");
        channels.add("Trade");
        channels.add("LocalDefense");
        channels.add("WorldDefense");
        if( channels.contains(channel) ) this.channel = channel;
        this.matcher = Pattern.compile("^([\\S]+) ([\\S]+)  \\[([0-9]{0,2})(\\. )?"+channel+"\\] ([\\S]+): (.+)$").matcher("");
    }
    //Pattern.compile("^([\\S]+) ([\\S]+)  \\[([0-9]{0,2})(\\. )?([^\\[\\]]+)\\] ([\\S]+): (.+)$");

    //TODO parse out actual time instead of received time
    @Override
    public Message buildMessage(String data) {
        Message m = new Message();
        matcher.reset(data);
        if( matcher.find() ){
            m.speaker = matcher.group(5);
            m.text = matcher.group(6);
            m.time = System.currentTimeMillis();

            //Calendar cal = Calendar.getInstance();
            //cal.set(year, month, date, hourOfDay, minute, second);
            //m.time = matcher.group(2);
            return m;
        } else {
            return null;
        }
    }

}
