package ptit.nttrung.chatusefirebase.listener;

/**
 * Created by TrungNguyen on 9/12/2017.
 */

public interface RegisterListener {
    void registerSuccess();

    void registerComplete();
    void registerFailure(String message);
}
