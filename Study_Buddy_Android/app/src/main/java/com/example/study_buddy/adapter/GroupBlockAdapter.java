package com.example.study_buddy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.study_buddy.GroupCalendarActivity;
import com.example.study_buddy.R;
import com.example.study_buddy.model.Event;

import java.util.ArrayList;
import java.util.List;

public class GroupBlockAdapter extends RecyclerView.Adapter<GroupBlockAdapter.ViewHolder> {
    private Context mContext;
    private List<String> mTimes;
    private List<List<Event>> mEvent;
    private GroupCalendarActivity mActivity;
    private List<String> mUsers;

    public GroupBlockAdapter(Context mContext, GroupCalendarActivity mActivity, List<List<Event>> mEvent, List<String> mUsers) {
        this.mContext = mContext;
        this.mActivity = mActivity;
        this.mEvent = mEvent;
        this.mUsers = mUsers;
        mTimes = new ArrayList<>();
        mTimes.add("6:00");
        mTimes.add("7:00");
        mTimes.add("8:00");
        mTimes.add("9:00");
        mTimes.add("10:00");
        mTimes.add("11:00");
        mTimes.add("12:00");
        mTimes.add("13:00");
        mTimes.add("14:00");
        mTimes.add("15:00");
        mTimes.add("16:00");
        mTimes.add("17:00");
        mTimes.add("18:00");
        mTimes.add("19:00");
        mTimes.add("20:00");
        mTimes.add("21:00");
        mTimes.add("22:00");
        mTimes.add("23:00");
    }

    @NonNull
    @Override
    public GroupBlockAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.group_event_item, parent, false);
        return new GroupBlockAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupBlockAdapter.ViewHolder holder, final int position) {
        holder.time.setText(mTimes.get(position));
        List<Event> events = mEvent.get(position);
        boolean noEvent = true;
        String display = "";

        if(!events.isEmpty()){
            for(int i = 0; i< events.size(); i++){
                if(events.get(i) != null ) {
                    if(events.get(i).getEventType().equals("MEETING")){
                        display = "";
                        display += "Meeting: " + events.get(i).getEventDescription();
                    }
                    else{
                        if(display == ""){
                            display += mUsers.get(i);
                        }
                        else {
                            display += ", " + mUsers.get(i);
                        }
                    }


                    switch(i) {
                        case 0 :
                            holder.background1.setVisibility(View.VISIBLE);
                            holder.background1.setBackgroundResource(R.drawable.background_meeting_block);
                            noEvent = false;
                            break;
                        case 1 :
                            holder.background2.setVisibility(View.VISIBLE);
                            holder.background2.setBackgroundResource(R.drawable.background_meeting_block1);
                            noEvent = false;
                            break;
                        case 2 :
                            holder.background3.setVisibility(View.VISIBLE);
                            holder.background3.setBackgroundResource(R.drawable.background_meeting_block2);
                            noEvent = false;
                            break;
                        case 3 :
                            holder.background4.setVisibility(View.VISIBLE);
                            holder.background4.setBackgroundResource(R.drawable.background_meeting_block3);
                            noEvent = false;
                            break;
                    }
                }
                else {
                    switch(i) {
                        case 0 : holder.background1.setVisibility(View.INVISIBLE);
                            holder.background2.setVisibility(View.INVISIBLE);
                            holder.background3.setVisibility(View.INVISIBLE);
                            holder.background4.setVisibility(View.INVISIBLE);
                            break;
                        case 1 : holder.background2.setVisibility(View.INVISIBLE);
                            holder.background3.setVisibility(View.INVISIBLE);
                            holder.background4.setVisibility(View.INVISIBLE);
                            break;
                        case 2 : holder.background3.setVisibility(View.INVISIBLE);
                            holder.background4.setVisibility(View.INVISIBLE);
                            break;
                        case 3 : holder.background4.setVisibility(View.INVISIBLE);
                            break;
                    }
                }
            }
            if(noEvent) {
                holder.message.setText("Schedule a meeting!");
            }
            else {
                holder.message.setText(display);
            }

        }





        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.scheduleMeetingRequest(position + 6);
            }
        });

    }



    @Override
    public int getItemCount() {
        return mTimes.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder{

        public TextView time;
        public TextView background1;
        public TextView background2;
        public TextView background3;
        public TextView background4;
        public TextView message;
        public RelativeLayout detail;


        public ViewHolder(View itemView){
            super(itemView);

            time = itemView.findViewById(R.id.time);
            detail = itemView.findViewById(R.id.detail);
            background1 = itemView.findViewById(R.id.cover1);
            background2 = itemView.findViewById(R.id.cover2);
            background3 = itemView.findViewById(R.id.cover3);
            background4 = itemView.findViewById(R.id.cover4);
            message = itemView.findViewById(R.id.message);
        }
    }

    public void setItems(List<List<Event>> mEvent) {
        this.mEvent = mEvent;
    }

}
