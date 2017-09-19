package ptit.nttrung.chatusefirebase.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ptit.nttrung.chatusefirebase.R;
import ptit.nttrung.chatusefirebase.model.Configuration;

/**
 * Created by TrungNguyen on 9/15/2017.
 */

public class UserInfoAdapter extends RecyclerView.Adapter<UserInfoAdapter.ViewHolder> {

    private static final String USERNAME_LABEL = "Username";
    private static final String EMAIL_LABEL = "Email";
    private static final String SIGNOUT_LABEL = "Sign out";
    private static final String RESETPASS_LABEL = "Change Password";

    private List<Configuration> profileConfig;
    private Context context;

    public UserInfoAdapter(Context context, List<Configuration> profileConfig) {
        this.profileConfig = profileConfig;
        this.context = context;
    }

    public void notifyData(List<Configuration> profileConfig) {
        this.profileConfig = profileConfig;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_info, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Configuration config = profileConfig.get(position);
        final String title = config.getLabel();

        holder.imgIcon.setImageResource(config.getIcon());
        holder.tvTitle.setText(title);
        holder.tvDetail.setText(config.getValue());
    }

    @Override
    public int getItemCount() {
        return profileConfig.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.img_icon)
        ImageView imgIcon;
        @BindView(R.id.tv_title)
        TextView tvTitle;
        @BindView(R.id.tv_detail)
        TextView tvDetail;
        @BindView(R.id.config_item)
        RelativeLayout rlConfigItem;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
