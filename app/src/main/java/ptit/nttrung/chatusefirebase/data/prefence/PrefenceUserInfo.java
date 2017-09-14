package ptit.nttrung.chatusefirebase.data.prefence;

import android.content.Context;

import ptit.nttrung.chatusefirebase.model.User;
import ptit.nttrung.chatusefirebase.ulti.SharedPreferencesUtil;

/**
 * Created by TrungNguyen on 9/14/2017.
 */

public class PrefenceUserInfo {
    private static PrefenceUserInfo instance;
    private SharedPreferencesUtil preferencesUtil;

    private static String SHARE_KEY_NAME = "name";
    private static String SHARE_KEY_EMAIL = "email";
    private static String SHARE_KEY_AVATA = "avata";
    private static String SHARE_KEY_UID = "uid";

    private PrefenceUserInfo(Context context) {
        preferencesUtil = new SharedPreferencesUtil(context);
    }

    public static PrefenceUserInfo getInstance(Context context) {
        if (instance == null) {
            instance = new PrefenceUserInfo(context);
        }
        return instance;
    }

    public void saveUserInfo(User user) {
        preferencesUtil.saveString(SHARE_KEY_NAME, user.getName());
        preferencesUtil.saveString(SHARE_KEY_EMAIL, user.getEmail());
        preferencesUtil.saveString(SHARE_KEY_AVATA, user.getAvata());
        preferencesUtil.saveString(SHARE_KEY_UID, user.getUid());
    }

    public User getUserInfo() {
        String userName = preferencesUtil.getString(SHARE_KEY_NAME, "");
        String email = preferencesUtil.getString(SHARE_KEY_EMAIL, "");
        String avatar = preferencesUtil.getString(SHARE_KEY_AVATA, "default");

        User user = new User();
        user.setEmail(email);
        user.setAvata(avatar);
        user.setName(userName);
        user.setUid(getUid());

        return user;
    }

    public String getUid() {
        return preferencesUtil.getString(SHARE_KEY_UID, "");
    }
}
