package catalyst.carousel;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.apache.commons.lang.StringUtils;


@Entity
public class UserSession {

    @Id String sessionKey = null;

    @OneToOne User user = null;
    @Basic Date created = new Date();
    @Basic Date lastSeen = new Date();

    public static final long DURATION;

    static{
        Long value = 30*60*1000L;        // 30 min default
        try{
            value = Long.parseLong(StringUtils.defaultString(Singletons.getPreferences().getProperty("carousel.user.session.duration")));
        } catch (NumberFormatException e){}
        DURATION = value;
    }

}
