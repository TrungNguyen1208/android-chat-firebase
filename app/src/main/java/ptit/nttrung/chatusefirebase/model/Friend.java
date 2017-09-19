package ptit.nttrung.chatusefirebase.model;

/**
 * Created by TrungNguyen on 9/19/2017.
 */

public class Friend extends User {
    private String id;
    private String idRoom;

    public Friend() {
    }

    public Friend(String id, String idRoom) {
        super();
        this.id = id;
        this.idRoom = idRoom;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdRoom() {
        return idRoom;
    }

    public void setIdRoom(String idRoom) {
        this.idRoom = idRoom;
    }
}
