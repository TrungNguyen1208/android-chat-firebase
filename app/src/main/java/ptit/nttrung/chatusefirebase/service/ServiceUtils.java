package ptit.nttrung.chatusefirebase.service;

import android.app.ActivityManager;
import android.content.Context;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import ptit.nttrung.chatusefirebase.data.define.Constants;
import ptit.nttrung.chatusefirebase.data.define.StaticConfig;
import ptit.nttrung.chatusefirebase.data.prefence.PrefenceUserInfo;
import ptit.nttrung.chatusefirebase.model.Friend;
import ptit.nttrung.chatusefirebase.model.ListFriend;

/**
 * Created by TrungNguyen on 9/20/2017.
 */

public class ServiceUtils {
    private static ServiceConnection connectionServiceFriendChatForStart = null;
    private static ServiceConnection connectionServiceFriendChatForDestroy = null;

    public static boolean isServiceFriendChatRunning(Context context) {
        Class<?> serviceClass = FriendChatService.class;
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static void updateUserStatus(Context context) {
        if (isNetworkConnected(context)) {
            String uid = PrefenceUserInfo.getInstance(context).getUid();
            if (!uid.equals("")) {
                FirebaseDatabase.getInstance().getReference().child(Constants.DB_USERS).child(uid)
                        .child("status/isOnline").setValue(true);
                FirebaseDatabase.getInstance().getReference().child(Constants.DB_USERS).child(uid)
                        .child("status/timestamp").setValue(System.currentTimeMillis());
            }
        }
    }

    public static void updateFriendStatus(Context context, ListFriend listFriend) {
        if (isNetworkConnected(context)) {
            for (Friend friend : listFriend.getListFriend()) {
                final String fid = friend.getId();
                FirebaseDatabase.getInstance().getReference().child(Constants.DB_USERS).child(fid)
                        .child("status").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            HashMap mapStatus = (HashMap) dataSnapshot.getValue();
                            if ((boolean) mapStatus.get("isOnline") && (System.currentTimeMillis() - (long) mapStatus.get("timestamp")) > StaticConfig.TIME_TO_OFFLINE) {
                                FirebaseDatabase.getInstance().getReference().child(Constants.DB_USERS).child(fid)
                                        .child("status/isOnline").setValue(false);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }
    }

    public static boolean isNetworkConnected(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            return cm.getActiveNetworkInfo() != null;
        } catch (Exception e) {
            return true;
        }
    }
}
