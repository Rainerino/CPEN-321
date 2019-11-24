package com.example.study_buddy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.study_buddy.adapter.BlockAdapter;
import com.example.study_buddy.adapter.GroupBlockAdapter;
import com.example.study_buddy.adapter.SelectUserAdapter;
import com.example.study_buddy.fragments.CalendarFragment;
import com.example.study_buddy.model.Event;
import com.example.study_buddy.model.Group;
import com.example.study_buddy.model.User;
import com.example.study_buddy.network.GetDataService;
import com.example.study_buddy.network.RetrofitInstance;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupCalendarActivity extends AppCompatActivity {

    private PopupWindow popupWindow;
    private PopupWindow deletePopup;
    private RecyclerView recyclerView;
    private SelectUserAdapter selectUserAdapter;
    private List<User> mAvailableUsers;
    private List<User> mSelectedUsers;
    private String cur_userId;
    private User currentUser;
    private int hour;
    private View view;
    private String s_frequency;
    private BlockAdapter blockAdapter;
    private List<List<Event>> mEvent;
    private EditText title;
    private EditText description;
    private EditText location;
    private ImageButton back_btn;
    private TextView meeting_member;
    private Button submit_btn;
    private Spinner frequency;
    private Event scheduledEvent;
    private SharedPreferences prefs;
    private CalendarFragment mFragment;
    private RecyclerView calendar_recyclerView;
    private CalendarView monthly_calendar;
    private TextView display_date;
    private String date;
    private int cur_dayOfMonth;
    private int cur_month;
    private Group cur_group;
    private List<String> mMembers;
    private GroupBlockAdapter groupBlockAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_calendar);

        /** Setting up toolbar **/
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupCalendarActivity.this,GroupActivity.class);
                Gson gson = new Gson();
                String group_info = gson.toJson(cur_group);
                intent.putExtra("group_into", group_info);
                startActivity(intent);
            }
        });

        /** Setting up view**/
        display_date = findViewById(R.id.display_date);
        RelativeLayout day_calendar = findViewById(R.id.day_calendar);
        monthly_calendar = findViewById(R.id.monthly_calendar);
        monthly_calendar.setVisibility(View.INVISIBLE);
        calendar_recyclerView = findViewById(R.id.calendar);
        calendar_recyclerView.setHasFixedSize(true);
        calendar_recyclerView.setLayoutManager(new LinearLayoutManager(this));

        cur_dayOfMonth = Calendar.getInstance().get(Calendar.DATE);
        cur_month = Calendar.getInstance().get(Calendar.MONTH);
        date = getDate(cur_month, cur_dayOfMonth);
        display_date.setText(date);

        /** Init group calendar **/
        mMembers = new ArrayList<>();
        mEvent = new ArrayList<>();
        for(int i = 0; i < 18; i++){
            List<Event> events = new ArrayList<>();
            mEvent.add(events);
        }
        groupBlockAdapter =
                new GroupBlockAdapter(this, mFragment, mEvent, mMembers);
        calendar_recyclerView.setAdapter(groupBlockAdapter);


        /** Get the group event**/
        Intent intent = getIntent();
        final String receiving_group = intent.getStringExtra("group_into");
        Gson gson = new Gson();
        cur_group = gson.fromJson(receiving_group, Group.class);
        List<String> friend_list = cur_group.getUserList();
        GetDataService service = RetrofitInstance.getRetrofitInstance().create(GetDataService.class);
        for(String userId: friend_list) {

            Call<User> call = service.getCurrentUser(userId);
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    /** Get the events of one member**/
                    mMembers.add(response.body().getFirstName());
                    Call<List<Event>> eventCall = service.getAllEvents(response.body().getCalendarList().get(0));
                    eventCall.enqueue(new Callback<List<Event>>() {
                        @Override
                        public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                            List<Event> userEvent = new ArrayList<>(Collections.nCopies(18, null));
                            for(Event event : response.body()){
                                if(event.getStartTime().getHours()-6>=0){
                                    userEvent.set(event.getStartTime().getHours()-6, event);
                                }
                            }
                            for(int i = 0; i < 18; i++){
                                mEvent.get(i).add(userEvent.get(i));
                            }
                            /** Notify the adapter **/
                            groupBlockAdapter.notifyDataSetChanged();

                        }

                        @Override
                        public void onFailure(Call<List<Event>> call, Throwable t) {

                        }
                    });
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {

                }
            });
        }
    }

    private void setNextDate() {
        if(cur_dayOfMonth < 30){
            cur_dayOfMonth++;
        }
        else if(cur_dayOfMonth == 30){
            if(cur_month == 0 || cur_month == 2 || cur_month == 4 || cur_month == 6 || cur_month == 7 || cur_month == 9 || cur_month == 11){
                cur_dayOfMonth++;
            }
            else {
                cur_dayOfMonth = 1;
                cur_month++;
            }
        }
        else {
            cur_dayOfMonth = 1;
            cur_month++;
        }

        String date = getDate(cur_month, cur_dayOfMonth);

        //TODO: update event
        display_date.setText(date);
    }

    private void setBeforeDate() {
        if(cur_dayOfMonth != 1){
            cur_dayOfMonth--;
        }
        else {
            cur_month--;
            if(cur_month == 0 || cur_month == 2 || cur_month == 4 || cur_month == 6 || cur_month == 7 || cur_month == 9 || cur_month == 11){
                cur_dayOfMonth = 31;
            }
            else {
                cur_dayOfMonth = 30;
            }
        }

        String date = getDate(cur_month, cur_dayOfMonth);

        //TODO: update event
        display_date.setText(date);
    }

    private String getDate(int month, int dayOfMonth) {
        String date = "";
        switch (month) {
            case 0 : date = "JAN "+ dayOfMonth; break;
            case 1 : date = "FEB "+ dayOfMonth; break;
            case 2 : date = "MAR "+ dayOfMonth; break;
            case 3 : date = "APR "+ dayOfMonth; break;
            case 4 : date = "MAY "+ dayOfMonth; break;
            case 5 : date = "JUN "+ dayOfMonth; break;
            case 6 : date = "JUL "+ dayOfMonth; break;
            case 7 : date = "AUG "+ dayOfMonth; break;
            case 8 : date = "SEP "+ dayOfMonth; break;
            case 9 : date = "OCT "+ dayOfMonth; break;
            case 10 : date = "NOV "+ dayOfMonth; break;
            case 11 : date = "DEC "+ dayOfMonth; break;
        }
        return date;
    }

    private void getEvents(){

    }
}
