package ptit.nttrung.chatusefirebase.main.profile;

import android.app.Activity;

import java.util.List;

import ptit.nttrung.chatusefirebase.base.BasePresenter;
import ptit.nttrung.chatusefirebase.base.BaseView;
import ptit.nttrung.chatusefirebase.model.Configuration;
import ptit.nttrung.chatusefirebase.model.User;

/**
 * Created by TrungNguyen on 9/17/2017.
 */

public interface UserProfileContract {
    interface View extends BaseView<Presenter> {
        void setAdapterData(List<Configuration> configurations);

        Activity getActivityContext();

        void setPresenter(Presenter presenter);

        void setTextName(String name);

        void showRenameDialog(User myAccount);

        void showOkCofimDialog();

        void showCofimDialogResetPass();

        void showConfimDialogErrorSent();

        void notifyDataSetChanged(List<Configuration> configList);
    }

    interface Presenter extends BasePresenter {
        void onSignOutClick();

        void onUserNameLabelClick();

        void onResetPassLabelClick();

        void onConfimRenameClick(User myAccount, String newName);

        List<Configuration> setupArrayListInfo(User myAccount);

        void resetPassword(User myAccount);

        User getUserInfoPrefence();

//        void setValueEventUser();
    }
}
