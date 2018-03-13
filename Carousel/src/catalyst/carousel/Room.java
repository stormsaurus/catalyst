package catalyst.carousel;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
public class Room {

    @SuppressWarnings("unused")
    @Id @GeneratedValue
    private int id;

    @Basic String name = "";
    @Basic boolean active = true;
    @OneToOne User owner = null;
    @OneToMany(cascade=CascadeType.REMOVE, mappedBy="room") Set<RoomKey> roomKeys = new HashSet<RoomKey>();

}
