package catalyst.carousel.irc;

import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class IRCNetwork {

    @Id String name = "";

    @Basic String website = "";
    @OneToMany (cascade=CascadeType.ALL, mappedBy="network") Set<IRCServer> servers = null;

}
