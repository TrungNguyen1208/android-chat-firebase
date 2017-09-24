package ptit.nttrung.chatusefirebase.model;

/**
 * Created by TrungNguyen on 9/14/2017.
 */

public class Status {
    private boolean isOnline;
    private long timestamp;

    public Status() {
        isOnline = false;
        timestamp = 0;
    }

    public boolean getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
