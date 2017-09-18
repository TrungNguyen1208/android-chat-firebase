package ptit.nttrung.chatusefirebase.base;

/**
 * Created by TrungNguyen on 9/16/2017.
 */

public interface BaseListence {
    void onSuccess();

    void onComplete();

    void onFailure(String message);
}
