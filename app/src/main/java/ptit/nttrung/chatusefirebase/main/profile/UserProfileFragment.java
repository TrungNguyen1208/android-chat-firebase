package ptit.nttrung.chatusefirebase.main.profile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ptit.nttrung.chatusefirebase.R;
import ptit.nttrung.chatusefirebase.adapter.UserInfoAdapter;
import ptit.nttrung.chatusefirebase.data.prefence.PrefenceUserInfo;
import ptit.nttrung.chatusefirebase.model.Configuration;
import ptit.nttrung.chatusefirebase.model.User;
import ptit.nttrung.chatusefirebase.ulti.ActivityUtils;
import ptit.nttrung.chatusefirebase.ulti.RecyclerItemClickListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserProfileFragment extends Fragment implements UserProfileContract.View {

    private static final String TAG = UserProfileFragment.class.getName();

    private static final String USERNAME_LABEL = "Username";
    private static final String EMAIL_LABEL = "Email";
    private static final String SIGNOUT_LABEL = "Sign out";
    private static final String RESETPASS_LABEL = "Change Password";

    @BindView(R.id.img_avatar)
    ImageView imgAvatar;
    @BindView(R.id.tv_username)
    TextView tvUserName;
    @BindView(R.id.info_recycler_view)
    RecyclerView mRecyclerView;
    Unbinder unbinder;

    private UserInfoAdapter infoAdapter;

    private UserProfileContract.Presenter presenter;
    private ProgressDialog mProgressDialog;
    private List<Configuration> configList = new ArrayList<>();
    private User myAccount;
    private Context context;
    private PrefenceUserInfo prefenceUserInfo;

    public UserProfileFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        unbinder = ButterKnife.bind(this, view);

        context = view.getContext();
        prefenceUserInfo = PrefenceUserInfo.getInstance(context);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (presenter == null) {
            presenter = new UserProfilePresenter(this, context);
        }
        myAccount = presenter.getUserInfoPrefence();
        configList = presenter.setupArrayListInfo(myAccount);
        setAdapterData(configList);
        tvUserName.setText(myAccount.getName());

        presenter.subscribe();
    }

    @Override
    public void onDestroyView() {
        presenter.unsubscribe();
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void showProgressDialog(String message) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getContext());
            mProgressDialog.setMessage(message);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
        }

        mProgressDialog.show();
    }

    @Override
    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void makeToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setAdapterData(List<Configuration> configurations) {
        infoAdapter = new UserInfoAdapter(getActivity(), configurations);
        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivityContext(), mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
                        String title = tvTitle.getText().toString();
                        if (title.equals(SIGNOUT_LABEL)) {
                            presenter.onSignOutClick();
                        } else if (title.equals(USERNAME_LABEL)) {
                            presenter.onUserNameLabelClick();
                        } else if (title.equals(RESETPASS_LABEL)) {
                            presenter.onResetPassLabelClick();
                        }
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                })
        );

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(infoAdapter);
    }

    @Override
    public Activity getActivityContext() {
        return getActivity();
    }

    @Override
    public void setPresenter(UserProfileContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void setTextName(String name) {
        if (tvUserName != null)
            tvUserName.setText(name);
    }

    @Override
    public void showRenameDialog(final User myAccount) {
        View vewInflater = LayoutInflater.from(context)
                .inflate(R.layout.dialog_edit_username, (ViewGroup) getView(), false);
        final EditText input = (EditText) vewInflater.findViewById(R.id.edit_username);
        input.setText(myAccount.getName());

         /*Hiển thị dialog với dEitText cho phép người dùng nhập username mới*/
        new AlertDialog.Builder(context)
                .setTitle("Edit username")
                .setView(vewInflater)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String newName = input.getText().toString();
                        if (!myAccount.getName().equals(newName)) {
                            presenter.onConfimRenameClick(myAccount, newName);
                        }
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();
    }

    @Override
    public void showOkCofimDialog() {
        ActivityUtils.alert(context, true, "Password",
                "Are you sure want to reset password?",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        myAccount = prefenceUserInfo.getUserInfo();
                        presenter.resetPassword(myAccount);
                        dialog.dismiss();
                    }
                },
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
    }

    @Override
    public void showCofimDialogResetPass() {
        ActivityUtils.showAlertCofirm(context, true, "Password Recovery",
                "Sent email to " + myAccount.getEmail(),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
    }

    @Override
    public void showConfimDialogErrorSent() {
        ActivityUtils.showAlertCofirm(context, true, "Fail",
                "False to sent email to " + myAccount.getEmail(),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
    }

    @Override
    public void notifyDataSetChanged(List<Configuration> configList) {
        if (infoAdapter != null) {
            infoAdapter.notifyData(configList);
        }
    }
}
