package catalyst.carousel.irc;

import org.jibble.pircbot.PircBot;

public class WoWBot extends PircBot {

    public WoWBot() {
        this.setName("WoWBotTestSubject2534");
    }

    public void onMessage(String channel, String sender, String login, String hostname, String message) {
        sendMessage(channel, sender + ": I hear you, tell me more.");
    }


}
