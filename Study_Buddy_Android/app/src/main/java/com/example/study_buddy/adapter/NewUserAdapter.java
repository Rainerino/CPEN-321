package com.example.study_buddy.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.study_buddy.AddFriendActivity;
import com.example.study_buddy.R;
import com.example.study_buddy.model.User;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class NewUserAdapter extends RecyclerView.Adapter<NewUserAdapter.ViewHolder>{
    private Context mContext;
    private List<User> mUser;
    private AddFriendActivity mActivity;


    public NewUserAdapter(Context mContext, List<User> mUser, AddFriendActivity mActivity){
        this.mUser = mUser;
        this.mContext = mContext;
        this.mActivity = mActivity;
    }

    @NonNull
    @Override
    public NewUserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.new_user_item, parent, false);
        return new NewUserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final NewUserAdapter.ViewHolder holder, int position) {
        final User user = mUser.get(position);
        String name = user.getFirstName() + " " + user.getLastName();

        SharedPreferences prefs = mContext.getSharedPreferences(
                "",
                MODE_PRIVATE);

        final String cur_userId = prefs.getString(
                "current_user_id",
                "");

        holder.username.setText(name);
        holder.profile_img.setImageResource(R.drawable.ic_profile_pic_name);

        holder.add_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                mActivity.addUserRequest(mUser.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return mUser.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder{
        public TextView username;
        public ImageView profile_img;
        public Button add_button;
        public ViewHolder(View itemView){
            super(itemView);
            username = itemView.findViewById(R.id.username);
            profile_img = itemView.findViewById(R.id.profile_image);
            add_button = itemView.findViewById((R.id.add_btn));
        }
    }
}
