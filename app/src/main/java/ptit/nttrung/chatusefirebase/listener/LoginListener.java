package ptit.nttrung.chatusefirebase.listener;

/**
 * Created by TrungNguyen on 9/12/2017.
 */

public interface LoginListener {
    void loginSuccess();
    void loginFailure(String message);
}
