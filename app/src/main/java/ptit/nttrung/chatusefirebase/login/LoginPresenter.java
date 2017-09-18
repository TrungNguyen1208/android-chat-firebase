package ptit.nttrung.chatusefirebase.login;

import android.content.Context;
import android.util.Log;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableMaybeObserver;
import io.reactivex.schedulers.Schedulers;
import ptit.nttrung.chatusefirebase.data.define.StaticConfig;
import ptit.nttrung.chatusefirebase.data.prefence.PrefenceUserInfo;
import ptit.nttrung.chatusefirebase.model.User;
import ptit.nttrung.chatusefirebase.service.AuthSource;
import ptit.nttrung.chatusefirebase.service.DatabaseSource;
import ptit.nttrung.chatusefirebase.service.FirebaseAuthService;
import ptit.nttrung.chatusefirebase.service.FirebaseDatabaseService;

/**
 * Created by TrungNguyen on 9/17/2017.
 */

public class LoginPresenter implements LoginContract.Presenter {

    private AuthSource authSource;
    private LoginContract.View view;
    private DatabaseSource databaseSource;
    private CompositeDisposable compositeDisposable;
    private Context context;

    public LoginPresenter(LoginContract.View view, Context context) {
        this.authSource = FirebaseAuthService.getInstance();
        this.databaseSource = FirebaseDatabaseService.getInstance();
        this.view = view;
        this.context = context;
        this.compositeDisposable = new CompositeDisposable();
        view.setPresenter(this);
    }

    @Override
    public void subscribe() {
        getUser();
    }

    @Override
    public void unsubscribe() {
        compositeDisposable.clear();
    }

    @Override
    public void onLogInClick() {
        if (view.checkValidate())
            attemptLogIn(view.getEmail(), view.getPassword());
    }

    @Override
    public void onSignUpClick() {
        view.startSignUpActivity();
    }

    public void attemptLogIn(String email, String password) {
        view.showProgressDialog("Loading");
        compositeDisposable.add(
                authSource.attemptLogin(email, password)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableCompletableObserver() {
                            @Override
                            public void onComplete() {
                                getUser();
                            }

                            @Override
                            public void onError(Throwable e) {
                                view.hideProgressDialog();
                                view.makeToast(e.toString());
                            }
                        })
        );
    }

    public void getUser() {
        view.showProgressDialog("Loading");
        compositeDisposable.add(
                authSource.getUser()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableMaybeObserver<User>() {

                            @Override
                            public void onSuccess(User user) {
                                getUserInfo(user.getUid());
                            }

                            @Override
                            public void onError(Throwable e) {
                                view.hideProgressDialog();
                                view.makeToast(e.getMessage());
                                Log.e("Get User", e.getMessage());
                            }

                            @Override
                            public void onComplete() {
                                view.hideProgressDialog();
                            }
                        })
        );
    }

    private void getUserInfo(final String uid) {
        compositeDisposable.add(
                databaseSource.getUserInfo(uid)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableMaybeObserver<User>() {
                            @Override
                            public void onSuccess(User user) {
                                PrefenceUserInfo preUserInfo = PrefenceUserInfo.getInstance(context);
                                preUserInfo.saveUserInfo(user);
                                StaticConfig.UID = user.getUid();
                                view.hideProgressDialog();
                                view.startMainActivity();
                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onComplete() {
                                view.hideProgressDialog();
                            }
                        })
        );
    }
}
