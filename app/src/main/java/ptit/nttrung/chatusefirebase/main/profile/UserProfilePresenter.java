package ptit.nttrung.chatusefirebase.main.profile;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableMaybeObserver;
import io.reactivex.schedulers.Schedulers;
import ptit.nttrung.chatusefirebase.R;
import ptit.nttrung.chatusefirebase.data.define.StaticConfig;
import ptit.nttrung.chatusefirebase.data.prefence.PrefenceUserInfo;
import ptit.nttrung.chatusefirebase.model.Configuration;
import ptit.nttrung.chatusefirebase.model.User;
import ptit.nttrung.chatusefirebase.service.AuthSource;
import ptit.nttrung.chatusefirebase.service.DatabaseSource;
import ptit.nttrung.chatusefirebase.service.FirebaseAuthService;
import ptit.nttrung.chatusefirebase.service.FirebaseDatabaseService;

/**
 * Created by TrungNguyen on 9/17/2017.
 */

public class UserProfilePresenter implements UserProfileContract.Presenter {

    private static final String USERNAME_LABEL = "Username";
    private static final String EMAIL_LABEL = "Email";
    private static final String SIGNOUT_LABEL = "Sign out";
    private static final String RESETPASS_LABEL = "Change Password";

    private AuthSource authSource;
    private UserProfileContract.View view;
    private DatabaseSource databaseSource;
    private CompositeDisposable compositeDisposable;
    private Context context;
    private PrefenceUserInfo prefenceUserInfo;
    private List<Configuration> listConfig = new ArrayList<>();

    private User myAccount;

    public UserProfilePresenter(UserProfileContract.View view, Context context) {
        this.authSource = FirebaseAuthService.getInstance();
        this.databaseSource = FirebaseDatabaseService.getInstance();
        this.view = view;
        this.context = context;
        this.compositeDisposable = new CompositeDisposable();
        this.prefenceUserInfo = PrefenceUserInfo.getInstance(context);
        view.setPresenter(this);
    }

    @Override
    public void subscribe() {
        getUserInfo(StaticConfig.UID);
    }

    @Override
    public void unsubscribe() {
        compositeDisposable.clear();
    }

    @Override
    public void onSignOutClick() {
        compositeDisposable.add(
                authSource.logUserOut()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableCompletableObserver() {
                            @Override
                            public void onComplete() {
                                view.hideProgressDialog();
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
    public void onUserNameLabelClick() {
        view.showRenameDialog(prefenceUserInfo.getUserInfo());
    }

    @Override
    public void onResetPassLabelClick() {
        view.showOkCofimDialog();
    }

    @Override
    public void onConfimRenameClick(final User myAccount, final String newName) {
        compositeDisposable.add(
                databaseSource.changeNameUser(StaticConfig.UID, newName)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableCompletableObserver() {
                            @Override
                            public void onComplete() {
                                myAccount.setName(newName);
                                prefenceUserInfo.saveUserInfo(myAccount);

                                view.setTextName(newName);
                                listConfig = setupArrayListInfo(myAccount);
                                view.notifyDataSetChanged(listConfig);
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e("Rename Error", e.getMessage());
                            }
                        })
        );
    }

    @Override
    public List<Configuration> setupArrayListInfo(User myAccount) {
        listConfig.clear();

        Configuration userNameConfig = new Configuration(USERNAME_LABEL,
                myAccount.getName(), R.mipmap.ic_account_box);
        listConfig.add(userNameConfig);

        Configuration emailConfig = new Configuration(EMAIL_LABEL,
                myAccount.getEmail(), R.mipmap.ic_email);
        listConfig.add(emailConfig);

        Configuration resetPass = new Configuration(RESETPASS_LABEL,
                "", R.mipmap.ic_restore);
        listConfig.add(resetPass);

        Configuration sigout = new Configuration(SIGNOUT_LABEL,
                "", R.mipmap.ic_power_settings);
        listConfig.add(sigout);

        return listConfig;
    }

    @Override
    public void resetPassword(User myAccount) {
        compositeDisposable.add(
                authSource.resetEmail(myAccount.getEmail())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableCompletableObserver() {
                            @Override
                            public void onComplete() {
                                view.showCofimDialogResetPass();
                            }

                            @Override
                            public void onError(Throwable e) {
                                view.showConfimDialogErrorSent();
                            }
                        })
        );
    }

    @Override
    public User getUserInfoPrefence() {
        PrefenceUserInfo prefenceUserInfo = PrefenceUserInfo.getInstance(context);
        return prefenceUserInfo.getUserInfo();
    }

    private void getUserInfo(final String uid) {
        compositeDisposable.add(
                databaseSource.getUserInfo(uid)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableMaybeObserver<User>() {
                            @Override
                            public void onSuccess(User user) {
                                Log.e("change userinfo", "aaaaa");
                                listConfig.clear();
                                myAccount = user;

                                listConfig = setupArrayListInfo(myAccount);
                                view.notifyDataSetChanged(listConfig);
                                view.setTextName(myAccount.getName());

                                prefenceUserInfo.saveUserInfo(myAccount);
                                view.hideProgressDialog();
                            }

                            @Override
                            public void onError(Throwable e) {
                                view.hideProgressDialog();
                            }

                            @Override
                            public void onComplete() {
                                view.hideProgressDialog();
                            }
                        })
        );
    }
}
