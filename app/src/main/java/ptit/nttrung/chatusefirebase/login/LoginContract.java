package ptit.nttrung.chatusefirebase.login;

import ptit.nttrung.chatusefirebase.base.BasePresenter;
import ptit.nttrung.chatusefirebase.base.BaseView;

/**
 * Created by TrungNguyen on 9/17/2017.
 */

public interface LoginContract {
    interface View extends BaseView<Presenter> {

        boolean checkValidate();

        void makeToast(String message);

        String getEmail();

        String getPassword();

        void startMainActivity();

        void startSignUpActivity();

        void setPresenter(LoginContract.Presenter presenter);

    }

    interface Presenter extends BasePresenter {
        void onLogInClick();

        void onSignUpClick();
    }
}
