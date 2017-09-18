package ptit.nttrung.chatusefirebase.service;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.Maybe;
import io.reactivex.MaybeEmitter;
import io.reactivex.MaybeOnSubscribe;
import ptit.nttrung.chatusefirebase.data.define.Constants;
import ptit.nttrung.chatusefirebase.model.User;

/**
 * Created by TrungNguyen on 9/17/2017.
 */

public class FirebaseDatabaseService implements DatabaseSource {

    private FirebaseDatabaseService() {
    }

    public static DatabaseSource getInstance() {
        return new FirebaseDatabaseService();
    }

    @Override
    public Completable createUserInfo(final User user) {
        return Completable.create(
                new CompletableOnSubscribe() {
                    @Override
                    public void subscribe(final CompletableEmitter e) throws Exception {
                        user.setName(user.getEmail().substring(0, user.getEmail().indexOf("@")));
                        user.setAvata(Constants.STR_DEFAULT_AVATAR);

                        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                        final DatabaseReference idRef = rootRef.child(Constants.DB_USERS).child(user.getUid());
                        idRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                if (!snapshot.exists()) {
                                    idRef.setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                e.onComplete();
                                            } else {
                                                e.onError(task.getException());
                                            }
                                        }
                                    });
                                } else {
                                    e.onComplete();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.d("FIREBASE", databaseError.toString());
                            }
                        });
                    }
                }
        );
    }

    @Override
    public Maybe<User> getUserInfo(final String uid) {
        return Maybe.create(
                new MaybeOnSubscribe<User>() {
                    @Override
                    public void subscribe(final MaybeEmitter<User> e) throws Exception {
                        final DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                        DatabaseReference idRef = rootRef.child(Constants.DB_USERS).child(uid);
                        idRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            //does this check node for activeUser exists?
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    User user = snapshot.getValue(User.class);
                                    e.onSuccess(user);
                                } else {
                                    e.onComplete();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.d("FIREBASE", databaseError.toString());
                            }
                        });
                    }
                }
        );
    }

    @Override
    public Completable updateUserInfo(final User user) {
        return Completable.create(
                new CompletableOnSubscribe() {
                    @Override
                    public void subscribe(final CompletableEmitter e) throws Exception {
                        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                        rootRef.child(Constants.DB_USERS)
                                .child(user.getUid())
                                .setValue(user)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            e.onComplete();
                                        } else {
                                            e.onError(task.getException());
                                        }
                                    }
                                });
                    }
                }
        );
    }

    @Override
    public Completable changeNameUser(final String uid, final String newName) {
        return Completable.create(
                new CompletableOnSubscribe() {
                    @Override
                    public void subscribe(final CompletableEmitter e) throws Exception {
                        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                        rootRef.child(Constants.DB_USERS)
                                .child(uid)
                                .child("name")
                                .setValue(newName)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            e.onComplete();
                                        } else {
                                            e.onError(task.getException());
                                        }
                                    }
                                });
                    }
                }
        );
    }

}
