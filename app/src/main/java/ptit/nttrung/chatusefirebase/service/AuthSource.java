package ptit.nttrung.chatusefirebase.service;

import io.reactivex.Completable;
import io.reactivex.Maybe;
import ptit.nttrung.chatusefirebase.model.User;

/**
 * Created by TrungNguyen on 9/17/2017.
 */

public interface AuthSource {
    Completable createAccount(String email, String passWord, String name);

    Completable attemptLogin(String email, String passWord);

    Maybe<User> getUser();

    Completable logUserOut();

    Completable resetEmail(String email);

    Completable reauthenticateUser(String newPassword);
}
