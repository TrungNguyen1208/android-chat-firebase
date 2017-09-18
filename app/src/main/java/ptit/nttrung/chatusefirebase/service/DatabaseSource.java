package ptit.nttrung.chatusefirebase.service;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import ptit.nttrung.chatusefirebase.model.User;

/**
 * Created by TrungNguyen on 9/17/2017.
 */

public interface DatabaseSource {
    Completable createUserInfo(User user);

    Maybe<User> getUserInfo(String uid);

    Completable updateUserInfo(User user);

    Completable changeNameUser(String uid, String newName);


}
