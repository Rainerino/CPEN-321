package com.example.study_buddy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.study_buddy.R;
import com.example.study_buddy.fragments.CalendarFragment;
import com.example.study_buddy.model.Event;

import java.util.ArrayList;
import java.util.List;

public class BlockAdapter extends RecyclerView.Adapter<BlockAdapter.ViewHolder> {
    private Context mContext;
    private List<String> mTimes;
    private List<Event> mEvent;
    private CalendarFragment mFragment;
    private int hour;
    private boolean getEvent;

    public BlockAdapter(Context mContext, CalendarFragment mFragment, List<Event> mEvent) {
        this.mContext = mContext;
        this.mFragment = mFragment;
        this.mEvent = mEvent;
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
    public BlockAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.event_item, parent, false);
        return new BlockAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlockAdapter.ViewHolder holder, final int position) {
        holder.time.setText(mTimes.get(position));


        if(mEvent.get(position) != null) {
            holder.event_title.setText(mEvent.get(position).getEventName());
            holder.event_location.setText(mEvent.get(position).getEventDescription());
            holder.background.setBackgroundResource(R.drawable.background_meeting_block);
        }
        else {
            holder.event_title.setText("");
            holder.event_location.setText("");
            holder.background.setBackgroundResource(R.color.trans);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFragment.scheduleMeetingRequest(mTimes.get(position));
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(mEvent.get(position) != null) {
                    mFragment.deleteEventRequest(position, mEvent.get(position).getId());
                    return true;
                }
                else {
                    return false;
                }
            }
        });

    }



    @Override
    public int getItemCount() {
        return mTimes.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder{

        public TextView time;
        public TextView event_title;
        public TextView event_location;
        public TextView background;
        public RelativeLayout detail;

        public ViewHolder(View itemView){
            super(itemView);

            time = itemView.findViewById(R.id.time);
            event_title = itemView.findViewById(R.id.event_title);
            event_location = itemView.findViewById(R.id.event_location);
            detail = itemView.findViewById(R.id.detail);
            background = itemView.findViewById(R.id.cover);
        }
    }

    public void setItems(List<Event> mEvent) {
        this.mEvent = mEvent;
    }

}
