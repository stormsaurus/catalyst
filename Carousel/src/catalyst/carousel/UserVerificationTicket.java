package catalyst.carousel;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.apache.commons.lang.StringUtils;
import org.apache.openjpa.persistence.jdbc.Unique;

@Entity
public class UserVerificationTicket {

    @Id String verificationKey = "";

    @OneToOne User user = null;
    @Basic Date created = new Date();
    public static final long duration;

    static{
        Long value = 3*24*60*60*1000L;        // 3 day ticket default
        try{
            value = Long.parseLong(StringUtils.defaultString(Singletons.getPreferences().getProperty("carousel.user.verification.ticket.duration")));
        } catch (NumberFormatException e){}
        duration = value;
    }

    public UserVerificationTicket(){
    }


}
