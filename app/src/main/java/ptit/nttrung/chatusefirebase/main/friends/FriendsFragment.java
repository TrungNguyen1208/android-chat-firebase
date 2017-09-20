package ptit.nttrung.chatusefirebase.main.friends;


import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;
import com.yarolegovich.lovelydialog.LovelyProgressDialog;
import com.yarolegovich.lovelydialog.LovelyTextInputDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ptit.nttrung.chatusefirebase.R;
import ptit.nttrung.chatusefirebase.adapter.ListFriendsAdapter;
import ptit.nttrung.chatusefirebase.chat.ChatActivity;
import ptit.nttrung.chatusefirebase.data.database.FriendDB;
import ptit.nttrung.chatusefirebase.data.define.Constants;
import ptit.nttrung.chatusefirebase.data.define.StaticConfig;
import ptit.nttrung.chatusefirebase.model.Friend;
import ptit.nttrung.chatusefirebase.model.ListFriend;
import ptit.nttrung.chatusefirebase.service.ServiceUtils;
import ptit.nttrung.chatusefirebase.ulti.RecyclerItemClickListener;

import static ptit.nttrung.chatusefirebase.adapter.ListFriendsAdapter.mapMark;

public class FriendsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.recycleListFriend)
    RecyclerView mRvFriends;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    Unbinder unbinder;

    private ListFriend dataListFriend = null;
    private ArrayList<String> listFriendId = null;
    private ListFriendsAdapter adapter;
    private LovelyProgressDialog dialogFindAllFriend;

    private CountDownTimer detectFriendOnline;
    public static int ACTION_START_CHAT = 1;
    public FragFriendClickFloatButton onClickFloatButton;

    public FriendsFragment() {
        onClickFloatButton = new FragFriendClickFloatButton();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        detectFriendOnline = new CountDownTimer(System.currentTimeMillis(), StaticConfig.TIME_TO_REFRESH) {
            @Override
            public void onTick(long l) {
                ServiceUtils.updateFriendStatus(getContext(), dataListFriend);
                ServiceUtils.updateUserStatus(getContext());
            }

            @Override
            public void onFinish() {

            }
        };

        if (dataListFriend == null) {
            dataListFriend = FriendDB.getInstance(getContext()).getListFriend();
            if (dataListFriend.getListFriend().size() > 0) {
                listFriendId = new ArrayList<>();
                for (Friend friend : dataListFriend.getListFriend()) {
                    listFriendId.add(friend.getId());
                }
                detectFriendOnline.start();
            }
        }

        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        unbinder = ButterKnife.bind(this, view);

        setupRecyclerView();

        if (listFriendId == null) {
            listFriendId = new ArrayList<>();
            showDialogFindAllFriend();
            getListFriendUId();
        }

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (ACTION_START_CHAT == requestCode && data != null && mapMark != null) {
            mapMark.put(data.getStringExtra("idFriend"), false);
        }
    }

    @Override
    public void onRefresh() {
        listFriendId.clear();
        dataListFriend.getListFriend().clear();
        adapter.notifyDataSetChanged();
        FriendDB.getInstance(getContext()).dropDB();
        detectFriendOnline.cancel();
        getListFriendUId();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void setupRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRvFriends.setLayoutManager(linearLayoutManager);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        adapter = new ListFriendsAdapter(getContext(), dataListFriend, this);
        mRvFriends.setAdapter(adapter);
        mRvFriends.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), mRvFriends, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // TODO SEND DATA FRIEND TO CHAT ACTIVITY

                        final String name = dataListFriend.getListFriend().get(position).getName();
                        final String id = dataListFriend.getListFriend().get(position).getId();
                        final String idRoom = dataListFriend.getListFriend().get(position).getIdRoom();
                        final String avata = dataListFriend.getListFriend().get(position).getAvata();

                        TextView txtMessage = (TextView) view.findViewById(R.id.txtMessage);
                        TextView txtName = (TextView) view.findViewById(R.id.txtName);
                        txtMessage.setTypeface(Typeface.DEFAULT);
                        txtName.setTypeface(Typeface.DEFAULT);

                        Intent intent = new Intent(getContext(), ChatActivity.class);
                        intent.putExtra(StaticConfig.INTENT_KEY_CHAT_FRIEND, name);
                        ArrayList<CharSequence> idFriend = new ArrayList<CharSequence>();
                        idFriend.add(id);
                        intent.putCharSequenceArrayListExtra(StaticConfig.INTENT_KEY_CHAT_ID, idFriend);
                        intent.putExtra(StaticConfig.INTENT_KEY_CHAT_ROOM_ID, idRoom);

//                        ChatActivity.bitmapAvataFriend = new HashMap<>();
//                        if (!avata.equals(StaticConfig.STR_DEFAULT_BASE64)) {
//                            byte[] decodedString = Base64.decode(avata, Base64.DEFAULT);
//                            ChatActivity.bitmapAvataFriend.put(id, BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
//                        } else {
//                            ChatActivity.bitmapAvataFriend.put(id, BitmapFactory.decodeResource(context.getResources(), R.drawable.default_avata));
//                        }

                        mapMark.put(id, null);
                        startActivityForResult(intent, FriendsFragment.ACTION_START_CHAT);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        //TODO DELETE FRIEND
                    }
                }));


    }

    private void showDialogFindAllFriend() {
        if (dialogFindAllFriend == null) {
            dialogFindAllFriend = new LovelyProgressDialog(getContext());
        }
        dialogFindAllFriend.setCancelable(false)
                .setIcon(R.drawable.ic_add_friend)
                .setTitle("Get all friend....")
                .setTopColorRes(R.color.colorPrimary)
                .show();
    }

    /**
     * Get All Friend In FirebaseDatabase
     */
    private void getListFriendUId() {
        FirebaseDatabase.getInstance().getReference().child(Constants.DB_FRIEND)
                .child(StaticConfig.UID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null) {
                            HashMap mapRecord = (HashMap) dataSnapshot.getValue();
                            Iterator listKey = mapRecord.keySet().iterator();
                            while (listKey.hasNext()) {
                                String key = listKey.next().toString();
                                Log.e("Key Friend", key);
                                listFriendId.add(mapRecord.get(key).toString());
                            }
                            getAllFriendInfo(0);
                        } else {
                            dialogFindAllFriend.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    /**
     * Truy cap bang user lay thong tin id nguoi dung
     */
    private void getAllFriendInfo(final int index) {
        if (index == listFriendId.size()) {
            //save list friend
            adapter.notifyDataSetChanged();
            dialogFindAllFriend.dismiss();
            mSwipeRefreshLayout.setRefreshing(false);
            detectFriendOnline.start();
        } else {
            final String id = listFriendId.get(index);
            FirebaseDatabase.getInstance().getReference().child(Constants.DB_USERS)
                    .child(id)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() != null) {
                                Friend user = new Friend();
                                HashMap mapUserInfo = (HashMap) dataSnapshot.getValue();
                                user.setName((String) mapUserInfo.get("name"));
                                user.setEmail((String) mapUserInfo.get("email"));
                                user.setAvata((String) mapUserInfo.get("avata"));
                                user.setId(id);
                                String idRoom = id.compareTo(StaticConfig.UID) > 0 ? (StaticConfig.UID + id).hashCode() + "" : "" + (id + StaticConfig.UID).hashCode();
                                user.setIdRoom(idRoom);

                                dataListFriend.getListFriend().add(user);
                                FriendDB.getInstance(getContext()).addFriend(user);
                            }
                            getAllFriendInfo(index + 1);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }
    }


    public class FragFriendClickFloatButton implements View.OnClickListener {
        private Context context;
        private LovelyProgressDialog dialogWait;

        public FragFriendClickFloatButton() {
        }

        public FragFriendClickFloatButton getInstance(Context context) {
            this.context = context;
            this.dialogWait = new LovelyProgressDialog(context);
            return this;
        }

        @Override
        public void onClick(View view) {
            new LovelyTextInputDialog(view.getContext(), R.style.EditTextTintTheme)
                    .setTopColorRes(R.color.colorPrimary)
                    .setTitle("Add friend")
                    .setMessage("Enter friend email")
                    .setIcon(R.drawable.ic_add_friend)
                    .setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS)
                    .setInputFilter("Email not found", new LovelyTextInputDialog.TextFilter() {
                        @Override
                        public boolean check(String text) {
                            Pattern VALID_EMAIL_ADDRESS_REGEX =
                                    Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
                            Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(text);
                            return matcher.find();
                        }
                    })
                    .setConfirmButton(android.R.string.ok, new LovelyTextInputDialog.OnTextInputConfirmListener() {
                        @Override
                        public void onTextInputConfirmed(String text) {
                            //TODO FIND USER AND CHECK USER IS HAVE IN FIREBASE
                            findIDEmail(text);
                        }
                    })
                    .show();
        }

        /**
         * Find Id Of Email In Firebase
         *
         * @param email
         */
        private void findIDEmail(String email) {
            dialogWait.setCancelable(false)
                    .setIcon(R.drawable.ic_add_friend)
                    .setTitle("Finding friend....")
                    .setTopColorRes(R.color.colorPrimary)
                    .show();
            FirebaseDatabase.getInstance().getReference().child(Constants.DB_USERS)
                    .orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    dialogWait.dismiss();
                    if (dataSnapshot.getValue() == null) {
                        //email not found
                        new LovelyInfoDialog(context)
                                .setTopColorRes(R.color.colorAccent)
                                .setIcon(R.drawable.ic_add_friend)
                                .setTitle("Fail")
                                .setMessage("Email not found")
                                .show();
                    } else {
                        String id = ((HashMap) dataSnapshot.getValue()).keySet().iterator().next().toString();
                        if (id.equals(StaticConfig.UID)) {
                            new LovelyInfoDialog(context)
                                    .setTopColorRes(R.color.colorAccent)
                                    .setIcon(R.drawable.ic_add_friend)
                                    .setTitle("Fail")
                                    .setMessage("Email not valid")
                                    .show();
                        } else {
                            HashMap userMap = (HashMap) ((HashMap) dataSnapshot.getValue()).get(id);
                            Friend user = new Friend();
                            user.setName((String) userMap.get("name"));
                            user.setEmail((String) userMap.get("email"));
                            user.setAvata((String) userMap.get("avata"));
                            user.setId(id);
                            String idRoom = id.compareTo(StaticConfig.UID) > 0 ? (StaticConfig.UID + id).hashCode() + "" : "" + (id + StaticConfig.UID).hashCode();
                            user.setIdRoom(idRoom);
                            checkBeforAddFriend(id, user);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        /**
         * Get Friend List By uid
         */
        private void checkBeforAddFriend(final String idFriend, Friend userInfo) {
            dialogWait.setCancelable(false)
                    .setIcon(R.drawable.ic_add_friend)
                    .setTitle("Add friend....")
                    .setTopColorRes(R.color.colorPrimary)
                    .show();

            //Check xem da ton tai id trong danh sach id chua
            if (listFriendId.contains(idFriend)) {
                dialogWait.dismiss();
                new LovelyInfoDialog(context)
                        .setTopColorRes(R.color.colorPrimary)
                        .setIcon(R.drawable.ic_add_friend)
                        .setTitle("Friend")
                        .setMessage("User " + userInfo.getEmail() + " has been friend")
                        .show();
            } else {
                addFriend(idFriend, true);
                listFriendId.add(idFriend);
                dataListFriend.getListFriend().add(userInfo);
                FriendDB.getInstance(getContext()).addFriend(userInfo);
                adapter.notifyDataSetChanged();
            }
        }

        /**
         * Add friend
         *
         * @param idFriend
         */
        private void addFriend(final String idFriend, boolean isIdFriend) {
            if (idFriend != null) {
                if (isIdFriend) {
                    FirebaseDatabase.getInstance().getReference().child(Constants.DB_FRIEND)
                            .child(StaticConfig.UID).push().setValue(idFriend)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        addFriend(idFriend, false);
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    dialogWait.dismiss();
                                    new LovelyInfoDialog(context)
                                            .setTopColorRes(R.color.colorAccent)
                                            .setIcon(R.drawable.ic_add_friend)
                                            .setTitle("False")
                                            .setMessage("False to add friend success")
                                            .show();
                                }
                            });
                } else {
                    FirebaseDatabase.getInstance().getReference().child(Constants.DB_FRIEND)
                            .child(idFriend).push().setValue(StaticConfig.UID).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                addFriend(null, false);
                            }
                        }
                    })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    dialogWait.dismiss();
                                    new LovelyInfoDialog(context)
                                            .setTopColorRes(R.color.colorAccent)
                                            .setIcon(R.drawable.ic_add_friend)
                                            .setTitle("False")
                                            .setMessage("False to add friend success")
                                            .show();
                                }
                            });
                }
            } else {
                dialogWait.dismiss();
                new LovelyInfoDialog(context)
                        .setTopColorRes(R.color.colorPrimary)
                        .setIcon(R.drawable.ic_add_friend)
                        .setTitle("Success")
                        .setMessage("Add friend success")
                        .show();
            }
        }
    }
}
