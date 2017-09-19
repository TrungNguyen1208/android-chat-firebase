package ptit.nttrung.chatusefirebase.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import ptit.nttrung.chatusefirebase.R;
import ptit.nttrung.chatusefirebase.main.friends.FriendsFragment;
import ptit.nttrung.chatusefirebase.model.ListFriend;

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
    public void onBindViewHolder(ViewHolder holder, int position) {
        final String name = listFriend.getListFriend().get(position).getName();
        final String id = listFriend.getListFriend().get(position).getId();
        final String idRoom = listFriend.getListFriend().get(position).getIdRoom();
        final String avata = listFriend.getListFriend().get(position).getAvata();

        holder.txtName.setText(name);
    }

    @Override
    public int getItemCount() {
        return 0;
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
