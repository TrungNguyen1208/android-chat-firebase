package ptit.nttrung.chatusefirebase.service;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Maybe;
import io.reactivex.MaybeEmitter;
import io.reactivex.MaybeOnSubscribe;
import ptit.nttrung.chatusefirebase.model.User;

/**
 * Created by TrungNguyen on 9/17/2017.
 */

public class FirebaseAuthService implements AuthSource {
    private static final String TAG = FirebaseAuthService.class.getName();

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener listener;

    private FirebaseAuthService() {
        auth = FirebaseAuth.getInstance();
    }

    public static AuthSource getInstance() {
        return new FirebaseAuthService();
    }

    @Override
    public Completable createAccount(final String email, final String password, final String name) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(final CompletableEmitter e) throws Exception {
                if (auth == null) {
                    auth = FirebaseAuth.getInstance();
                }

                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    e.onComplete();
                                } else {
                                    e.onError(task.getException());
                                }
                            }
                        });

            }
        });
    }

    @Override
    public Completable attemptLogin(final String email, final String passWord) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(final CompletableEmitter e) throws Exception {
                if (auth == null) {
                    auth = FirebaseAuth.getInstance();
                }

                auth.signInWithEmailAndPassword(email, passWord)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    e.onComplete();
                                } else {
                                    e.onError(task.getException());
                                }
                            }
                        });
            }
        });
    }

    @Override
    public Maybe<User> getUser() {
        return Maybe.create(
                new MaybeOnSubscribe<User>() {
                    @Override
                    public void subscribe(final MaybeEmitter<User> e) throws Exception {
                        if (auth == null) {
                            auth = FirebaseAuth.getInstance();
                        }

                        if (listener != null) {
                            auth.removeAuthStateListener(listener);
                        }

                        listener = new FirebaseAuth.AuthStateListener() {
                            @Override
                            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                auth.removeAuthStateListener(listener);
                                if (firebaseUser != null) {
                                    User user = new User();
                                    user.setUid(firebaseUser.getUid());
                                    user.setEmail(firebaseUser.getEmail());

                                    Maybe.just(user);
                                    e.onSuccess(user);
                                } else {
                                    e.onComplete();
                                }
                            }
                        };

                        auth.addAuthStateListener(listener);
                    }
                }
        );
    }

    @Override
    public Completable logUserOut() {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(final CompletableEmitter e) throws Exception {
                if (auth == null) {
                    auth = FirebaseAuth.getInstance();
                }

                listener = new FirebaseAuth.AuthStateListener() {
                    @Override
                    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                        auth.removeAuthStateListener(listener);
                        if (firebaseAuth.getCurrentUser() == null) {
                            e.onComplete();
                        } else {
                            e.onError(new Exception());
                        }
                    }
                };

                auth.addAuthStateListener(listener);
                auth.signOut();
            }
        });
    }

    @Override
    public Completable resetEmail(final String email) {
        return Completable.create(new CompletableOnSubscribe() {
            @Override
            public void subscribe(final CompletableEmitter e) throws Exception {
                if (auth == null) {
                    auth = FirebaseAuth.getInstance();
                }

                auth.sendPasswordResetEmail(email)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                e.onComplete();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception t) {
                                e.onError(t);
                            }
                        });
            }
        });
    }


    @Override
    public Completable reauthenticateUser(String password) {
        return null;
    }
}
