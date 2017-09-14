package ptit.nttrung.chatusefirebase.service;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import ptit.nttrung.chatusefirebase.base.BaseFirebase;
import ptit.nttrung.chatusefirebase.data.define.Constants;
import ptit.nttrung.chatusefirebase.listener.RegisterListener;
import ptit.nttrung.chatusefirebase.model.User;

/**
 * Created by TrungNguyen on 9/12/2017.
 */

public class ResgisterService extends BaseFirebase {
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;
    private Activity activity;

    public ResgisterService(Activity activity) {
        this.activity = activity;
        auth = getFirebaseAuth();
        mDatabase = getDatabaseReference();
    }

    /**
     * Đăng lý tài khoản bằng Email
     *
     * @param email
     * @param passWord
     * @param name
     * @param listener
     */
    public void registerAccount(String email, String passWord, final String name, final RegisterListener listener) {
        auth.createUserWithEmailAndPassword(email, passWord)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            final FirebaseUser userFB = task.getResult().getUser();
                            if (userFB != null) {
                                //Save user into Firebase database
                                User users = new User();
                                users.setUid(userFB.getUid());
                                users.setName(name);
                                users.setEmail(userFB.getEmail());
                                createAccountInDatabase(users, new RegisterListener() {
                                    @Override
                                    public void registerSuccess() {
                                        // Đăng xuất.
                                        auth.signOut();
                                        listener.registerSuccess();
                                    }

                                    @Override
                                    public void registerComplete() {
                                        listener.registerComplete();
                                    }


                                    @Override
                                    public void registerFailure(String message) {
                                        listener.registerFailure(message);
                                    }
                                });
                            } else {
                                listener.registerComplete();
                            }
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.registerFailure(e.getMessage());
            }
        });
    }

    /**
     * Lưu thông tin user
     *
     * @param users
     */
    public void createAccountInDatabase(User users, final RegisterListener listener) {
        users.setAvata(Constants.STR_DEFAULT_AVATAR);
        mDatabase.child(Constants.DB_USERS)
                .child(users.getUid())
                .setValue(users)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            listener.registerSuccess();
                        } else {
                            listener.registerComplete();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.registerFailure(e.getMessage());
                    }
                });
    }

}
