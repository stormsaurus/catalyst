package catalyst.watcher;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import catalyst.watcher.parsers.WoWChannel;


public enum Game {

    //rename enum to something non game specific
    WoW ("World of Warcraft", "WoW", WoWChannel.class, "C:\\Program Files\\World of Warcraft\\Launcher.exe", "Macintosh Main HD/Applications/World of Warcraft/Launcher.exe", "", "C:\\Program Files\\World of Warcraft\\Logs\\WoWChatLog.txt", "Macintosh Main HD/Applications/World of Warcraft/Logs/WoWChatLog.txt", ""),
    WoWCombat ("World of Warcraft Combat", "WoWCombat", Parser.class, "C:\\Program Files\\World of Warcraft\\Launcher.exe", "Macintosh Main HD/Applications/World of Warcraft/Launcher.exe", "", "C:\\Program Files\\World of Warcraft\\Logs\\WoWCombatLog.txt", "Macintosh Main HD/Applications/World of Warcraft/Logs/WoWCombatLog.txt", ""),
    AoC ("Age of Conan", "AoC", Parser.class, "", "", "", "", "", ""),
    NWN2 ("Neverwinter Nights 2", "NWN2", Parser.class, "", "", "", "", "", ""),
    Other ("Unknown","Unknown", Parser.class, "", "", "", "", "", "");

    private String _fullName = null;
    private String _shortName = null;
    private Class<Parser> _parser = null;
    private String _defaultLocationWin = null;
    private String _defaultLocationMac = null;
    private String _defaultLocationOther = null;
    private String _defaultLogFileLocationWin = null;
    private String _defaultLogFileLocationMac = null;
    private String _defaultLogFileLocationOther = null;
    private Map<Class<Parser>,SortedSet<String>> _availableChannels = new HashMap<Class<Parser>,SortedSet<String>>();

    @SuppressWarnings("unchecked")
    private Game(String fullName, String shortName, Class parser, String defaultLocationWin, String defaultLocationMac, String defaultLocationOther, String defaultLogFileLocationWin, String defaultLogFileLocationMac, String defaultLogFileLocationOther){
        _fullName = fullName;
        _shortName = shortName;
        _parser = parser;
        _defaultLocationWin = defaultLocationWin;
        _defaultLocationMac = defaultLocationMac;
        _defaultLocationOther = defaultLocationOther;
        _defaultLogFileLocationWin = defaultLogFileLocationWin;
        _defaultLogFileLocationMac = defaultLogFileLocationMac;
        _defaultLogFileLocationOther = defaultLogFileLocationOther;
        //find the available parsers
        //need to automatically search for and find parsers of type Parser

        //check to see if parser is a channel parser and add it's names to the availableChannels
        SortedSet<String> channelSet = new TreeSet<String>();
        if( parser!=null && ChannelParser.class.isAssignableFrom(parser)){
            ChannelParser tempInstance = null;
            try {
                tempInstance = (ChannelParser)parser.newInstance();
                channelSet.addAll(tempInstance.getAvailableChannelNames());
            } catch (Exception e) {
                App.log(getClass().getSimpleName()+" could not create "+parser.getSimpleName()+"");
                e.printStackTrace();
            }
        }
        _availableChannels.put(parser, channelSet);
    }

    public String getFullName(){
        return _fullName;
    }
    public String getShortName(){
        return _shortName;
    }
    public String getDefaultFileLocation(){
        if( OS.get()==OS.Windows ) return _defaultLogFileLocationWin;
        if( OS.get()==OS.Mac ) return _defaultLogFileLocationMac;
        return _defaultLogFileLocationOther;
    }
    public String getLauncherLocation(){
        if( OS.get()==OS.Windows ) return _defaultLocationWin;
        if( OS.get()==OS.Mac ) return _defaultLocationMac;
        return _defaultLocationOther;
    }
    public Class<Parser> getParserClass(){
        return _parser;
    }
    public SortedSet<String> getChannels(){
        return Collections.unmodifiableSortedSet(_availableChannels.get(_parser));
    }
}
