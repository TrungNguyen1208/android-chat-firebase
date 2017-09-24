package ptit.nttrung.chatusefirebase.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import ptit.nttrung.chatusefirebase.R;
import ptit.nttrung.chatusefirebase.data.define.Constants;
import ptit.nttrung.chatusefirebase.data.define.StaticConfig;
import ptit.nttrung.chatusefirebase.main.friends.FriendsFragment;
import ptit.nttrung.chatusefirebase.model.Friend;
import ptit.nttrung.chatusefirebase.model.ListFriend;
import ptit.nttrung.chatusefirebase.model.Message;

/**
 * Created by TrungNguyen on 9/19/2017.
 */

public class ListFriendsAdapter extends RecyclerView.Adapter<ListFriendsAdapter.ViewHolder> {

    private Context context;
    private ListFriend listFriend;
    public static Map<String, Query> mapQuery;
    public static Map<String, DatabaseReference> mapQueryOnline;
    public static Map<String, ChildEventListener> mapChildListener;
    public static Map<String, ChildEventListener> mapChildListenerOnline;
    public static Map<String, Boolean> mapMark;
    private FriendsFragment fragment;

    public ListFriendsAdapter(Context context, ListFriend listFriend, FriendsFragment fragment) {
        this.listFriend = listFriend;
        this.context = context;
        mapQuery = new HashMap<>();
        mapChildListener = new HashMap<>();
        mapMark = new HashMap<>();
        mapChildListenerOnline = new HashMap<>();
        mapQueryOnline = new HashMap<>();
        this.fragment = fragment;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_friend, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        ArrayList<Friend> friends = listFriend.getListFriend();
        final String name = listFriend.getListFriend().get(position).getName();
        final String id = listFriend.getListFriend().get(position).getId();
        final String idRoom = listFriend.getListFriend().get(position).getIdRoom();
        final String avata = listFriend.getListFriend().get(position).getAvata();

        holder.txtName.setText(name);

        //Message
        final Message message = listFriend.getListFriend().get(position).getMessage();
        if (message.getText().length() > 0) {
            //Have Message
            holder.txtMessage.setVisibility(View.VISIBLE);
            holder.txtTime.setVisibility(View.VISIBLE);
            if (!message.getText().startsWith(id)) {
                holder.txtMessage.setText(message.getText());
                holder.txtMessage.setTypeface(Typeface.DEFAULT);
                holder.txtName.setTypeface(Typeface.DEFAULT);
            } else {
                holder.txtMessage.setText(message.getText().substring((id + "").length()));
                holder.txtMessage.setTypeface(Typeface.DEFAULT_BOLD);
                holder.txtName.setTypeface(Typeface.DEFAULT_BOLD);
            }
            String time = new SimpleDateFormat("EEE, d MMM yyyy").format(new Date(message.getTimestamp()));
            String today = new SimpleDateFormat("EEE, d MMM yyyy").format(new Date(System.currentTimeMillis()));

            if (today.equals(time)) {
                holder.txtTime.setText(new SimpleDateFormat("HH:mm").format(new Date(message.getTimestamp())));
            } else {
                holder.txtTime.setText(new SimpleDateFormat("MMM d").format(new Date(message.getTimestamp())));
            }

        } else {
            //No Message
            holder.txtMessage.setVisibility(View.GONE);
            holder.txtTime.setVisibility(View.GONE);

            if (mapQuery.get(id) == null && mapChildListener.get(id) == null) {
                mapQuery.put(id, FirebaseDatabase.getInstance().getReference()
                        .child("message/" + idRoom).limitToLast(1));
                mapChildListener.put(id, new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        HashMap mapMessage = (HashMap) dataSnapshot.getValue();
                        if (mapMark.get(id) != null) {
                            if (!mapMark.get(id)) {
                                message.setText(id + mapMessage.get("text"));
                            } else {
                                message.setText((String) mapMessage.get("text"));
                            }
                            notifyDataSetChanged();
                            mapMark.put(id, false);
                        } else {
                            message.setText((String) mapMessage.get("text"));
                            notifyDataSetChanged();
                        }
                        message.setTimestamp((long) mapMessage.get("timestamp"));
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                mapQuery.get(id).addChildEventListener(mapChildListener.get(id));
                mapMark.put(id, true);
            } else {
                mapQuery.get(id).removeEventListener(mapChildListener.get(id));
                mapQuery.get(id).addChildEventListener(mapChildListener.get(id));
                mapMark.put(id, true);
            }
        }

        //Set Avatar Image
        if (avata.equals(StaticConfig.STR_DEFAULT_BASE64)) {
            holder.iconAvata.setImageResource(R.drawable.default_avata);
        } else {
            byte[] decodedString = Base64.decode(avata, Base64.DEFAULT);
            Bitmap src = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.iconAvata.setImageBitmap(src);
        }

        //Status
        if (mapQueryOnline.get(id) == null && mapChildListenerOnline.get(id) == null) {
            mapQueryOnline.put(id, FirebaseDatabase.getInstance().getReference()
                    .child(Constants.DB_USERS)
                    .child(id)
                    .child("status"));
//                    .child("user/" + id + "/status"));
            mapChildListenerOnline.put(id, new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Log.e("FriendsFragment add", "Key " + dataSnapshot.getKey() + " Value " + dataSnapshot.getValue());
                    if (dataSnapshot.getValue() != null && dataSnapshot.getKey().equals("isOnline")) {
                        Log.d("FriendsFragment add " + id, (boolean) dataSnapshot.getValue() + "");
                        listFriend.getListFriend().get(position)
                                .getStatus().setIsOnline((boolean) dataSnapshot.getValue());
                        notifyDataSetChanged();
                    }
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    Log.e("FriendsFragment add", "Key " + dataSnapshot.getKey() + " Value " + dataSnapshot.getValue());
                    if (dataSnapshot.getValue() != null && dataSnapshot.getKey().equals("isOnline")) {
                        Log.d("FriendsFragment add " + id, (boolean) dataSnapshot.getValue() + "");
                        listFriend.getListFriend().get(position)
                                .getStatus().setIsOnline((boolean) dataSnapshot.getValue());
                        notifyDataSetChanged();
                    }
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            mapQueryOnline.get(id).addChildEventListener(mapChildListenerOnline.get(id));
        }

        //Online
        if (listFriend.getListFriend().get(position).getStatus().getIsOnline()) {
            holder.iconAvata.setBorderWidth(10);
        } else {
            holder.iconAvata.setBorderWidth(0);
        }
    }

    @Override
    public int getItemCount() {
        if (listFriend.getListFriend() == null) return 0;
        return listFriend.getListFriend().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.icon_avata)
        CircleImageView iconAvata;
        @BindView(R.id.txtName)
        TextView txtName;
        @BindView(R.id.txtTime)
        TextView txtTime;
        @BindView(R.id.txtMessage)
        TextView txtMessage;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
