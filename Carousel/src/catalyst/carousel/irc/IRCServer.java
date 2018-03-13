package catalyst.carousel.irc;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.ManyToOne;

@Entity
@IdClass(IRCServer.PK.class)
public class IRCServer {

    @Id String server = "";
    @Id String port = "6667";

    @Basic String country = "Unknown";
    @ManyToOne IRCNetwork network = null;

    public static class PK implements Serializable{
        private static final long serialVersionUID = 1L;
        public String server = "";
        public String port = "";

        public boolean equals(Object other) {
            if (other == this)
                return true;
            if (!(other instanceof PK))
                return false;

            PK key = (PK) other;
            return (server == key.server
                || (server != null && server.equals(key.server)))
                && (port == key.port
                || (port != null && port.equals(key.port)));
        }

        /**
         * Hashcode must also depend on identity values.
         */
        public int hashCode() {
            return ((server == null) ? 0 : server.hashCode())
                ^ ((port == null) ? 0 : port.hashCode());
        }

        public String toString() {
            return server + ":" + port;
        }

    }

}
