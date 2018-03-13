package catalyst.carousel;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class RoomKey {

    @Id String roomKey = null;

    @ManyToOne Room room = null;

}
