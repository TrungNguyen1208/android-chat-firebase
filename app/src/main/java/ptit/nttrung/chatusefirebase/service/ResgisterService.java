package ptit.nttrung.chatusefirebase.service;

import android.app.Activity;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;

import ptit.nttrung.chatusefirebase.base.BaseFirebase;
import ptit.nttrung.chatusefirebase.define.Constants;
import ptit.nttrung.chatusefirebase.listener.RegisterListener;
import ptit.nttrung.chatusefirebase.model.UserDb;

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
                                //Luu ten
                                UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(name)
                                        .build();

                                userFB.updateProfile(profileUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            //Tiến hành thông tin user vào Database
                                            UserDb users = new UserDb();
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
                                                public void registerFailure(String message) {
                                                    listener.registerFailure(message);
                                                }
                                            });
                                        }
                                    }
                                });


                                //Gửi 1 email xác thực tài khoản
//                                userFB.sendEmailVerification().addOnCompleteListener(activity, new OnCompleteListener<Void>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<Void> task) {
//                                        if (task.isSuccessful()) {
//                                            //Tiến hành thông tin user vào Database
//                                        }
//                                    }
//                                });
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
    public void createAccountInDatabase(UserDb users, final RegisterListener listener) {
        mDatabase.child(Constants.USERS)
                .child(users.getUid())
                .setValue(users)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        listener.registerSuccess();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.registerFailure(e.getMessage());
            }
        });
    }

}
