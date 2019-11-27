package com.example.study_buddy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.study_buddy.R;
import com.example.study_buddy.model.User;

import java.util.ArrayList;
import java.util.List;

public class SelectUserAdapter extends RecyclerView.Adapter<SelectUserAdapter.ViewHolder> {
    private Context mContext;
    private List<User> mUser;
    private List<User> selected_list;

    public SelectUserAdapter(Context mContext, List<User> mUser){
        this.mUser = mUser;
        this.mContext = mContext;
        selected_list = new ArrayList<>();
    }

    @NonNull
    @Override
    public SelectUserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.available_user_item, parent, false);
        return new SelectUserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectUserAdapter.ViewHolder holder, int position) {
        final User user = mUser.get(position);
        holder.username.setText(user.getFirstName());
        holder.profile_img.setImageResource(R.mipmap.ic_user_default_round);

        if(selected_list.contains(user)) {
            holder.checkBox.setChecked(true);
        }
        else {
            holder.checkBox.setChecked(false);
        }

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!selected_list.contains(user)){
                    selected_list.add(user);
                }
                else if(selected_list.contains(user)){
                    selected_list.remove(user);
                }

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
        public CheckBox checkBox;

        public ViewHolder(View itemView){
            super(itemView);

            username = itemView.findViewById(R.id.username);
            profile_img = itemView.findViewById(R.id.profile_image);
            checkBox = itemView.findViewById(R.id.checkbox);
        }
    }

    public List<User> getSelectedUsers() {
        return selected_list;
    }
}
