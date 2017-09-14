package ptit.nttrung.chatusefirebase.service;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import ptit.nttrung.chatusefirebase.base.BaseFirebase;
import ptit.nttrung.chatusefirebase.data.define.Constants;
import ptit.nttrung.chatusefirebase.data.prefence.PrefenceUserInfo;
import ptit.nttrung.chatusefirebase.listener.LoginListener;
import ptit.nttrung.chatusefirebase.model.User;

/**
 * Created by TrungNguyen on 9/12/2017.
 */

public class LoginService extends BaseFirebase {
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;
    private Activity activity;

    public LoginService(Activity activity) {
        this.activity = activity;
        auth = getFirebaseAuth();
        mDatabase = getDatabaseReference();
    }

    /**
     * Xác Thực Tài Khoản
     *
     * @param email
     * @param passWord
     * @param listener
     */
    public void loginAccountEmail(String email, String passWord, final LoginListener listener) {
        auth.signInWithEmailAndPassword(email, passWord)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.loginFailure(e.getMessage());
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser userFB = task.getResult().getUser();
                            if (userFB != null) {
                                saveUserInfo(userFB.getUid());
                                listener.loginSuccess();
                            }

                        } else {
                            listener.loginComplete();
                        }
                    }
                });
    }

    private void saveUserInfo(String uid) {
        mDatabase.child(Constants.DB_USERS)
                .child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        HashMap hashUser = (HashMap) dataSnapshot.getValue();
                        User userInfo = new User();
                        userInfo.setUid((String) hashUser.get("uid"));
                        userInfo.setEmail((String) hashUser.get("name"));
                        userInfo.setUid((String) hashUser.get("email"));
                        userInfo.setUid((String) hashUser.get("avata"));
                        PrefenceUserInfo.getInstance(activity).saveUserInfo(userInfo);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

}
