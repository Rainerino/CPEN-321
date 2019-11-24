package com.example.study_buddy.fragments;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.study_buddy.R;
import com.example.study_buddy.adapter.BlockAdapter;
import com.example.study_buddy.adapter.SelectUserAdapter;
import com.example.study_buddy.model.Event;
import com.example.study_buddy.model.MyCalendar;
import com.example.study_buddy.model.User;
import com.example.study_buddy.network.GetDataService;
import com.example.study_buddy.network.RetrofitInstance;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.content.Context.MODE_PRIVATE;


public class CalendarFragment extends Fragment {
    final private GetDataService service =
            RetrofitInstance.getRetrofitInstance().create(GetDataService.class);

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
    private List<Event> mEvent;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setHasOptionsMenu(false);
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_calendar, container, false);
        display_date = view.findViewById(R.id.display_date);
        RelativeLayout day_calendar = view.findViewById(R.id.day_calendar);
        monthly_calendar = view.findViewById(R.id.monthly_calendar);
        monthly_calendar.setVisibility(View.INVISIBLE);
        mFragment = this;
        calendar_recyclerView = view.findViewById(R.id.calendar);
        calendar_recyclerView.setHasFixedSize(true);
        calendar_recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        cur_dayOfMonth = Calendar.getInstance().get(Calendar.DATE);
        cur_month = Calendar.getInstance().get(Calendar.MONTH);
        date = getDate(cur_month, cur_dayOfMonth);
        display_date.setText(date);

        /*****get event of the day, store to mEvent array*********/

        //get current user
        prefs = Objects.requireNonNull(getContext()).getSharedPreferences(
                "",MODE_PRIVATE);

        Gson gson = new Gson();
        String cur_user = prefs.getString("current_user", "");
        currentUser = gson.fromJson(cur_user, User.class);
        String json = prefs.getString("current_user_events", "");
        cur_userId = prefs.getString("current_user_id", "");
        if(json == ""){
            List<Event> emptyEvent = new ArrayList<>(Collections.nCopies(18, null));
            Log.e(TAG, "onCreateView: the event json is empty" );
            blockAdapter = new BlockAdapter(getContext(),this, emptyEvent);
            calendar_recyclerView.setAdapter(blockAdapter);
        }
        else {
            MyCalendar calendar = gson.fromJson(json, MyCalendar.class);
            mEvent = calendar.getmEvents();
            Log.e(TAG, "onCreateView: get event list" + json );
            blockAdapter = new BlockAdapter(getContext(),this, mEvent);
            calendar_recyclerView.setAdapter(blockAdapter);
        }

        // just use 1 calendar for now. TODO: change to the calendar picked.

        // group calendar button for now
        ImageButton next_btn = view.findViewById(R.id.next_button);
        ImageButton back_btn = view.findViewById(R.id.back_button);

        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNextDate();
            }
        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBeforeDate();
            }
        });

        display_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                day_calendar.setVisibility(View.INVISIBLE);
                monthly_calendar.setVisibility(View.VISIBLE);

                monthly_calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
                    @Override
                    public void onSelectedDayChange(CalendarView CalendarView, int year, int month, int dayOfMonth) {
                        cur_month = month;
                        cur_dayOfMonth = dayOfMonth;
                        String date = getDate(month, dayOfMonth);
                        display_date.setText(date);
                        day_calendar.setVisibility(View.VISIBLE);
                        monthly_calendar.setVisibility(View.INVISIBLE);

                    }
                });
            }
        });


        // create the popup window
        int width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        int height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        popupWindow = new PopupWindow();
        popupWindow.setWidth(width);
        popupWindow.setHeight(height);
        popupWindow.setFocusable(true);

        deletePopup = new PopupWindow();
        deletePopup.setWidth(width);
        deletePopup.setHeight(height);
        deletePopup.setOutsideTouchable(false);

        return view;
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


    private void showScheduleMeetingStartUp(View view) {
        LayoutInflater inflater = (LayoutInflater)
                view.getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.schedule_meeting_startup, null);
        popupWindow.setContentView(popupView);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        //EditText editText;
        ImageButton next_btn;
        mAvailableUsers = new ArrayList<>();
        mSelectedUsers = new ArrayList<>();


        //editText = popupView.findViewById(R.id.search_user);
        next_btn = popupView.findViewById(R.id.next_btn);
        recyclerView = popupView.findViewById(R.id.available_user_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        getAvailableUsers();


        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelectedUsers = selectUserAdapter.getSelectedUsers();
                popupWindow.dismiss();
                getMeetingDetails(v.getRootView());

            }
        });

    }

    private void getAvailableUsers(){
        GetDataService service = RetrofitInstance.getRetrofitInstance().create(GetDataService.class);
        /***use the getFriend request for now, will change to getAvailableFriend request when backend's ready***/
        Call<List<User>> call = service.getFriends(cur_userId);

        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                for(User user: response.body()){
                    mAvailableUsers.add(user);
                }
                selectUserAdapter = new SelectUserAdapter(getContext(), mAvailableUsers);
                recyclerView.setAdapter(selectUserAdapter);
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(getContext(), "Please check internet connection",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getSuggestedFriends() {

        /***use the getFriend request for now, will change to getAvailableFriend request when backend's ready***/
        Call<List<User>> call = service.getFriends(cur_userId);

        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                for(User user: response.body()){
                    mAvailableUsers.add(user);
                }
                selectUserAdapter = new SelectUserAdapter(getContext(), mAvailableUsers);
                recyclerView.setAdapter(selectUserAdapter);

            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(getContext(), "Please check internet connection",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getMeetingDetails(View view){
        LayoutInflater inflater = (LayoutInflater)
                view.getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.create_event_layout, null);

        popupWindow.setContentView(popupView);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        setUpView(popupView);

        frequency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!parent.getItemAtPosition(position).toString().equals("Never")){
                    s_frequency = parent.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //mSelectedUsers = new ArrayList<>();
            }
        });


        submit_btn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                tryCreateEvent();
            }
        });
    }

    private void setUpView(View popupView) {
        s_frequency = "";

        submit_btn = popupView.findViewById(R.id.submit_btn);
        frequency = popupView.findViewById(R.id.frequency);
        title = popupView.findViewById(R.id.edit_title);
        description = popupView.findViewById(R.id.edit_description);
        location = popupView.findViewById(R.id.edit_location);


        ArrayAdapter<CharSequence> spinner_adapter = ArrayAdapter.createFromResource(view.getContext(), R.array.frequency, android.R.layout.simple_spinner_item);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        frequency.setAdapter(spinner_adapter);
    }

    public void scheduleMeetingRequest(String time){
        hour = Integer.parseInt(time.split(":")[0]);
        getMeetingDetails(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void tryCreateEvent() {
        /**
         * 1. check if all the fields are filled
         * 2. if all field, create event object and store all the details
         * 3. call putEvent request
         * 4. send notifications to invited friends
         * */
        if(title.getText().toString().isEmpty() ||
                description.getText().toString().isEmpty() ||
                location.getText().toString().isEmpty()){
            Toast.makeText(getContext(), "Please fill in meeting information",
                    Toast.LENGTH_LONG).show();
            return;
        }
        Date startTime = new GregorianCalendar(2019, cur_month,cur_dayOfMonth, hour, 0).getTime();
        Date endTime = new GregorianCalendar(2019, cur_month,cur_dayOfMonth, hour + 1, 0).getTime();

        // create the event

        Call<Event> createEventCall = service.postNewEvent(
                title.getText().toString(),
                description.getText().toString(),
                startTime,
                endTime,
                cur_userId,
                s_frequency
        );
        createEventCall.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                if(response.isSuccessful()){
                    scheduledEvent = response.body();
                    mEvent.set(hour-6, scheduledEvent);

                    blockAdapter.notifyDataSetChanged();

                    Call putEventCall = service.putEvent2Calendar(
                            currentUser.getCalendarList().get(0),
                            scheduledEvent.getId()
                    );
                    putEventCall.enqueue(new Callback() {
                        @Override
                        public void onResponse(Call call, Response response) {
                            Toast.makeText(getContext(), "Event created",
                                    Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void onFailure(Call call, Throwable t) {

                        }
                    });
                }
                else {
                    Toast.makeText(getContext(), response.message(),
                            Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                Toast.makeText(getContext(), "Save meeting to server failed",
                        Toast.LENGTH_LONG).show();
            }
        });

        popupWindow.dismiss();

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void tryCreateMeeting() {
        /**
         * 1. check if all the fields are filled
         * 2. if all field, create event object and store all the details
         * 3. call putEvent request
         * 4. send notifications to invited friends
         * */
        if(title.getText().toString().isEmpty() ||
                description.getText().toString().isEmpty() ||
                location.getText().toString().isEmpty()){
            Toast.makeText(getContext(), "Please fill in meeting information",
                    Toast.LENGTH_LONG).show();
            return;
        }
        Date startTime = new GregorianCalendar(2019, cur_month,cur_dayOfMonth, hour, 0).getTime();
        Date endTime = new GregorianCalendar(2019, cur_month,cur_dayOfMonth, hour + 1, 0).getTime();

        // create the event

        ArrayList<String> friendIdList = new ArrayList<>();
//        for (User user : mSelectedUsers) {
//            friendIdList.add(user.getid());
//        }
        Call<Event> createEventCall = service.postNewMeeting(
                title.getText().toString(),
                description.getText().toString(),
                startTime,
                endTime,
                cur_userId,
                friendIdList,
                s_frequency
        );
        createEventCall.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                if(response.isSuccessful()){
                    scheduledEvent = response.body();
                    mEvent.set(hour-6, scheduledEvent);

                    blockAdapter.setItems(mEvent);
                    blockAdapter.notifyItemChanged(hour-6);
                }
                else {
                    Toast.makeText(getContext(), response.message(),
                            Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                Toast.makeText(getContext(), "Save meeting to server failed",
                        Toast.LENGTH_LONG).show();
            }
        });


        // TODO: notification

        /**********post event********/

        popupWindow.dismiss();

        Toast.makeText(getContext(), "Meeting created",
                Toast.LENGTH_LONG).show();

    }

    public void deleteEventRequest(int position) {
        int time = position + 6;
        Toast.makeText(getContext(), "Delete event at " + time + ":00" ,
                Toast.LENGTH_LONG).show();

        LayoutInflater inflater = (LayoutInflater)
                getContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.delete_confirm_popup, null);
        deletePopup.setContentView(popupView);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        deletePopup.showAtLocation(this.getView(), Gravity.CENTER, 0, -100);

        TextView message = popupView.findViewById(R.id.confirm_message);
        Button delete_btn = popupView.findViewById(R.id.delete_btn);
        Button cancel_btn = popupView.findViewById(R.id.cancel_btn);
        String confirm_message = "Delete event at " + time + ":00?";
        message.setText(confirm_message);

        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEvent.set(position, null);
                blockAdapter.notifyDataSetChanged();
                //TODO: delete request
                deletePopup.dismiss();
            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deletePopup.dismiss();
            }
        });
    }

}
