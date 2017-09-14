package ptit.nttrung.chatusefirebase.model;

/**
 * Created by TrungNguyen on 9/12/2017.
 */

public class User {
    private String uid;
    private String name;
    private String email;
    private String avata;
    private Status status;
    private Message message;

    public User() {
        status = new Status();
        message = new Message();
        status.setOnline(false);
        status.setTimestamp(0);
        message.setIdReceiver("0");
        message.setIdSender("0");
        message.setText("");
        message.setTimestamp(0);
    }

    public User(String uid, String name, String email) {
        this.uid = uid;
        this.name = name;
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvata() {
        return avata;
    }

    public void setAvata(String avata) {
        this.avata = avata;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Users{" +
                "uid='" + uid + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
