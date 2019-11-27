package com.example.study_buddy.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.study_buddy.GroupActivity;
import com.example.study_buddy.R;
import com.example.study_buddy.model.Group;
import com.google.gson.Gson;

import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.ViewHolder>{
    private Context mContext;
    private List<Group> mGroup;

    public GroupAdapter(Context mContext, List<Group> mGroup){
        this.mGroup = mGroup;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public GroupAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.group_item, parent, false);
        return new GroupAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupAdapter.ViewHolder holder, int position) {
        final Group group = mGroup.get(position);
        holder.group_name.setText(group.getGroupName());
        holder.profile_img.setImageResource(R.mipmap.ic_group_default_round);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, GroupActivity.class);
                Gson gson = new Gson();
                String group_info = gson.toJson(group);
                intent.putExtra("group_into", group_info);

                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mGroup.size();
    }
    public  class ViewHolder extends RecyclerView.ViewHolder{
        public TextView group_name;
        public ImageView profile_img;
        public RelativeLayout viewBackground, viewForeground;
        public ViewHolder(View itemView){
            super(itemView);
            group_name = itemView.findViewById(R.id.group_name);
            profile_img = itemView.findViewById(R.id.profile_image);
            viewBackground = itemView.findViewById(R.id.view_background);
            viewForeground = itemView.findViewById(R.id.view_foreground);
        }
    }

    public void removeUser(int position) {
        mGroup.remove(position);
        // notify the item removed by position
        // to perform recycler view delete animations
        // NOTE: don't call notifyDataSetChanged()
        notifyItemRemoved(position);
    }

    public void restoreItem(int position) {
//        mGroup.add(position, user);
//        // notify item added by position
//        notifyItemInserted(position);
        notifyItemChanged(position);
    }
}
