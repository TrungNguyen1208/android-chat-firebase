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
import ptit.nttrung.chatusefirebase.model.Configuration;
import ptit.nttrung.chatusefirebase.model.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserProfileFragment extends Fragment implements UserProfileContract.View {

    private static final String TAG = UserProfileFragment.class.getName();

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

//    private ValueEventListener userListenner = new ValueEventListener() {
//        @Override
//        public void onDataChange(DataSnapshot dataSnapshot) {
//            //Lấy thông tin của user về và cập nhật lên giao diện
//            listConfig.clear();
//            myAccount = dataSnapshot.getValue(User.class);
//
//            listConfig = presenter.setupArrayListInfo(myAccount);
//            notifyDataSetChanged();
//            setTextName(myAccount.getName());
//
//            PrefenceUserInfo preUserInfo = PrefenceUserInfo.getInstance(context);
//            preUserInfo.saveUserInfo(myAccount);
//        }
//
//        @Override
//        public void onCancelled(DatabaseError databaseError) {
//            Log.e(TAG, "loadPost:onCancelled", databaseError.toException());
//        }
//    };

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
        infoAdapter.setOnLabelItemnClick(new UserInfoAdapter.OnLabelItemnClick() {
            @Override
            public void onSignOutLabelClick() {
                presenter.onSignOutClick();
            }

            @Override
            public void onUserNameLabelClick() {
                presenter.onUserNameLabelClick();
            }

            @Override
            public void onResetPassLabelClick() {
                presenter.onResetPassLabelClick();
            }
        });

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
    public void showRenameDialog(final String name) {
        View vewInflater = LayoutInflater.from(context)
                .inflate(R.layout.dialog_edit_username, (ViewGroup) getView(), false);
        final EditText input = (EditText) vewInflater.findViewById(R.id.edit_username);
        input.setText(name);

         /*Hiển thị dialog với dEitText cho phép người dùng nhập username mới*/
        new AlertDialog.Builder(context)
                .setTitle("Edit username")
                .setView(vewInflater)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String newName = input.getText().toString();
                        if (!name.equals(newName)) {
                            presenter.onConfimRenameClick(newName);
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
    public void notifyDataSetChanged(List<Configuration> configList) {
        if (infoAdapter != null) {
            infoAdapter.notifyData(configList);
        }
    }
}
