package ptit.nttrung.chatusefirebase.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by TrungNguyen on 9/20/2017.
 */

public class FriendChatService extends Service {
    private static String TAG = "FriendChatService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
