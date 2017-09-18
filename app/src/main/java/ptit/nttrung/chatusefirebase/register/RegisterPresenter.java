package ptit.nttrung.chatusefirebase.register;

import android.content.Context;
import android.util.Log;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableMaybeObserver;
import io.reactivex.schedulers.Schedulers;
import ptit.nttrung.chatusefirebase.model.User;
import ptit.nttrung.chatusefirebase.service.AuthSource;
import ptit.nttrung.chatusefirebase.service.DatabaseSource;
import ptit.nttrung.chatusefirebase.service.FirebaseAuthService;
import ptit.nttrung.chatusefirebase.service.FirebaseDatabaseService;

/**
 * Created by TrungNguyen on 9/17/2017.
 */

public class RegisterPresenter implements RegisterContract.Presenter {

    private AuthSource authSource;
    private RegisterContract.View view;
    private DatabaseSource databaseSource;
    private CompositeDisposable compositeDisposable;
    private Context context;

    public RegisterPresenter(RegisterContract.View view, Context context) {
        this.authSource = FirebaseAuthService.getInstance();
        this.databaseSource = FirebaseDatabaseService.getInstance();
        this.view = view;
        this.context = context;
        this.compositeDisposable = new CompositeDisposable();
        view.setPresenter(this);
    }

    @Override
    public void onCreateAccountClick() {
        if (view.checkValidate()) {
            view.showProgressDialog("Creating Account...");
            String name = view.getName();
            String email = view.getEmail();
            String password = view.getPassword();

            attemptAccountCreation(email, password, name);
        }
    }

    private void attemptAccountCreation(String email, String password, String name) {
        compositeDisposable.add(
                authSource.createAccount(email, password, name)
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
                                view.makeToast(e.getMessage());
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
                                addUserInfoToDatabase(user);
                            }

                            @Override
                            public void onError(Throwable e) {
                                view.hideProgressDialog();
                                Log.e("Get User", e.getMessage());
                            }

                            @Override
                            public void onComplete() {
                                view.hideProgressDialog();
                            }
                        })
        );
    }

    private void addUserInfoToDatabase(User user) {
        compositeDisposable.add(
                databaseSource.createUserInfo(user)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableCompletableObserver() {
                            @Override
                            public void onComplete() {
                                logout();
                            }

                            @Override
                            public void onError(Throwable e) {
                                view.hideProgressDialog();
                                view.makeToast(e.getMessage());
                            }
                        })
        );
    }

    private void logout() {
        compositeDisposable.add(
                authSource.logUserOut()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableCompletableObserver() {
                            @Override
                            public void onComplete() {
                                view.hideProgressDialog();
                                view.startLoginActivity();
                            }

                            @Override
                            public void onError(Throwable e) {
                                view.hideProgressDialog();
                                view.makeToast(e.getMessage());
                            }
                        })
        );
    }

    @Override
    public void subscribe() {
    }

    @Override
    public void unsubscribe() {
        compositeDisposable.clear();
    }
}
