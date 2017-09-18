package ptit.nttrung.chatusefirebase.register;

import ptit.nttrung.chatusefirebase.base.BasePresenter;
import ptit.nttrung.chatusefirebase.base.BaseView;

/**
 * Created by TrungNguyen on 9/17/2017.
 */

public interface RegisterContract {
    interface View extends BaseView<Presenter> {
        void makeToast(String message);

        boolean checkValidate();

        String getEmail();

        String getPassword();

        String getPasswordConfirm();

        String getName();

        void startLoginActivity();

        void setPresenter(RegisterContract.Presenter presenter);
    }

    interface Presenter extends BasePresenter {
        void onCreateAccountClick();

        void subscribe();

        void unsubscribe();
    }
}
