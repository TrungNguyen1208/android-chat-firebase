package ptit.nttrung.chatusefirebase.service;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import ptit.nttrung.chatusefirebase.base.BaseFirebase;
import ptit.nttrung.chatusefirebase.listener.LoginListener;

/**
 * Created by TrungNguyen on 9/12/2017.
 */

public class LoginService extends BaseFirebase{
    private FirebaseAuth auth;

    public LoginService(){
        auth = getFirebaseAuth();
    }

    /**
     * Xác Thực Tài Khoản
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
                }).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                listener.loginSuccess();
            }
        });
    }

}
