package com.example.study_buddy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.study_buddy.adapter.GroupBlockAdapter;
import com.example.study_buddy.adapter.SelectUserAdapter;
import com.example.study_buddy.model.Event;
import com.example.study_buddy.model.Group;
import com.example.study_buddy.model.User;
import com.example.study_buddy.network.GetDataService;
import com.example.study_buddy.network.RetrofitInstance;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.study_buddy.App.getContext;

public class GroupCalendarActivity extends AppCompatActivity {

    final private GetDataService service =
            RetrofitInstance.getRetrofitInstance().create(GetDataService.class);
    private final int YEAR_START = 1900;
    private PopupWindow popupWindow;
    private RecyclerView recyclerView;
    private SelectUserAdapter selectUserAdapter;
    private List<User> mSelectedUsers;
    private User currentUser;
    private int hour;
    private String s_frequency;
    private List<List<Event>> mEvent;
    private EditText title;
    private EditText description;
    private EditText location;
    private TextView meeting_member;
    private Button submit_btn;
    private Spinner frequency;
    private SharedPreferences prefs;
    private RecyclerView calendar_recyclerView;
    private CalendarView monthly_calendar;
    private TextView display_date;
    private String date;
    private int cur_dayOfMonth;
    private int cur_month;
    private int cur_year;
    private Group cur_group;
    private List<String> mMembers;
    private GroupBlockAdapter groupBlockAdapter;
    private PopupWindow addMemberpopupWindow;
    private List<User> mFriends;
    private List<User> filteredUsers;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_calendar);

        Intent intent = getIntent();
        Gson gson = new Gson();
        final String receiving_group = intent.getStringExtra("group_into");
        cur_group = gson.fromJson(receiving_group, Group.class);

        /** Setting up toolbar **/
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(cur_group.getGroupName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupCalendarActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        /** Get currentUser **/
        prefs = getSharedPreferences("",
                MODE_PRIVATE);

        String json = prefs.getString("current_user", "");
        currentUser = gson.fromJson(json, User.class);

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
        cur_year = Calendar.getInstance().get(Calendar.YEAR) - YEAR_START;
        date = getDate(cur_month, cur_dayOfMonth);
        display_date.setText(date);


        /** Create the popup window **/
        int width = RelativeLayout.LayoutParams.WRAP_CONTENT;
        int height = RelativeLayout.LayoutParams.WRAP_CONTENT;
        popupWindow = new PopupWindow();
        popupWindow.setWidth(width);
        popupWindow.setHeight(height);
        popupWindow.setFocusable(true);

        addMemberpopupWindow = new PopupWindow();
        addMemberpopupWindow.setWidth(width);
        addMemberpopupWindow.setHeight(height);
        addMemberpopupWindow.setFocusable(true);



        /** Init group calendar **/
        mMembers = new ArrayList<>();
        mEvent = new ArrayList<>();
        for(int i = 0; i < 18; i++){
            List<Event> events = new ArrayList<>();
            for(int j = 0; j < cur_group.getUserList().size(); j++){
                events.add(null);
            }
            mEvent.add(events);
        }
        groupBlockAdapter =
                new GroupBlockAdapter(this, this, mEvent, mMembers);
        calendar_recyclerView.setAdapter(groupBlockAdapter);


        /** Get the group event**/

        getGroupEvent();

        /** Calendar Actions **/
        ImageButton next_btn = findViewById(R.id.next_button);
        ImageButton back_btn = findViewById(R.id.back_button);

        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNextDate();
                getGroupEvent();
            }
        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBeforeDate();
                getGroupEvent();
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
                        getGroupEvent();
                    }
                });
            }
        });
    }

    private void getGroupEvent() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sssX", Locale.CANADA);

        for(List<Event> eventList : mEvent) {
            if(!eventList.isEmpty()) {
                for (Event event : eventList){
                    if(event != null) {
                        eventList.set(eventList.indexOf(event), null);
                    }
                }
            }
        }

        Date cur_date = Calendar.getInstance().getTime();
        cur_date.setDate(cur_dayOfMonth);
        cur_date.setMonth(cur_month);
        cur_date.setYear(cur_year);


        List<String> friend_list = cur_group.getUserList();
        GetDataService service = RetrofitInstance.getRetrofitInstance().create(GetDataService.class);
        for(String userId: friend_list) {

            Call<User> call = service.getCurrentUser(currentUser.getJwt(), userId);
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    /** Get the events of one member**/
                    if(mMembers.size() < cur_group.getUserList().size()){
                        mMembers.add(response.body().getFirstName());
                    }
                    String cur_id = response.body().getid();
                    Call<List<Event>> eventCall = service.getUserEvents(currentUser.getJwt(), cur_id, cur_date);
                    eventCall.enqueue(new Callback<List<Event>>() {
                        @Override
                        public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                            Log.e("Getting event: ", "onResponse: " + response.body() );
                            List<Event> userEvent = new ArrayList<>(Collections.nCopies(18, null));
                            if(!response.body().isEmpty()) {
                                for(Event event : response.body()){
                                    if(event.getStartTime().getHours()-6>=0){
                                        userEvent.set(event.getStartTime().getHours()-6, event);
                                    }
                                }
                                for(int i = 0; i < 18; i++){
                                    mEvent.get(i).set(cur_group.getUserList().indexOf(cur_id), userEvent.get(i));
                                }
                                /** Notify the adapter **/
                                groupBlockAdapter.notifyDataSetChanged();
                            }
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
            if(cur_month == 11){
                cur_year++;
                cur_month = 0;
            }
            else {
                cur_month++;
            }
            cur_dayOfMonth = 1;
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

    private void getMeetingDetails(View view){
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.schedule_meeting_details, null);


//        // show the popup window
//        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.setContentView(popupView);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        setUpView(popupView);


        String members = "";
        for(String name : mMembers) {
            if(members == "") {
                members += name;
            }
            else{
                members += ", " + name;
            }
        }

        meeting_member.setText(members);

        frequency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(!parent.getItemAtPosition(position).toString().equals("Never")){
                    s_frequency = parent.getItemAtPosition(position).toString();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mSelectedUsers = new ArrayList<>();
            }
        });

        submit_btn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                tryCreateMeeting();
            }
        });
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

        Call<Event> createEventCall = service.postNewMeeting(
                currentUser.getJwt(),
                title.getText().toString(),
                description.getText().toString(),
                startTime,
                endTime,
                currentUser.getid(),
                mMembers,
                s_frequency
        );
        createEventCall.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                if(response.isSuccessful()){
                    Event scheduledEvent = response.body();
                    /** Add meetings to every member's list and notify the adapter**/

                    // Send the notification to everyone
                    Call<Event> notifyCall = service.notifyNewMeeting(currentUser.getJwt(), currentUser.getid(), scheduledEvent.getId());
                    notifyCall.enqueue(new Callback<Event>() {
                        @Override
                        public void onResponse(Call<Event> call, Response<Event> response) {

                        }

                        @Override
                        public void onFailure(Call<Event> call, Throwable t) {

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

        Toast.makeText(getContext(), "Meeting created",
                Toast.LENGTH_LONG).show();

    }

    private void setUpView(View popupView) {
        s_frequency = "";

        submit_btn = popupView.findViewById(R.id.submit_btn);
        meeting_member = popupView.findViewById(R.id.member_names);
        frequency = popupView.findViewById(R.id.frequency);
        title = popupView.findViewById(R.id.edit_title);
        description = popupView.findViewById(R.id.edit_description);
        location = popupView.findViewById(R.id.edit_location);


        ArrayAdapter<CharSequence> spinner_adapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.frequency, android.R.layout.simple_spinner_item);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        frequency.setAdapter(spinner_adapter);
    }

    public void scheduleMeetingRequest(int time){
        hour = time;
        getMeetingDetails(findViewById(R.id.group_calendar_view));
    }

    private void addMemberPopup(View view) {
        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.add_member_popup, null);
        addMemberpopupWindow.setContentView(popupView);

        // show the popup window
        // which view you pass in doesn't matter, it is only used for the window tolken
        addMemberpopupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        EditText search_bar = popupView.findViewById(R.id.search_user);
        Button create_btn;
        mFriends = new ArrayList<>();
        readUsers();
        mSelectedUsers = new ArrayList<>();
        filteredUsers = new ArrayList<>();


        //editText = popupView.findViewById(R.id.search_user);
        create_btn = popupView.findViewById(R.id.next_btn);
        recyclerView = popupView.findViewById(R.id.available_user_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        selectUserAdapter = new SelectUserAdapter(getContext(), filteredUsers);
        recyclerView.setAdapter(selectUserAdapter);




        create_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSelectedUsers = selectUserAdapter.getSelectedUsers();
                if(mSelectedUsers.isEmpty()) {
                    Toast.makeText(getContext(), "Please select at least one member to add",
                            Toast.LENGTH_LONG).show();
                }
                else {
                    addMemberpopupWindow.dismiss();
                    addMember();
                }
            }
        });

        search_bar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().equals("")){
                    InitUser();
                }
                else {
                    searchUser(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private  void readUsers() {

        Call<List<User>> call = service.getFriends(currentUser.getJwt(),currentUser.getid());

        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                mFriends = response.body();
                if(mFriends != null) {
                    for(User user: mFriends){
                        filteredUsers.add(user);
                    }
                    selectUserAdapter = new SelectUserAdapter(getContext(), filteredUsers);
                    recyclerView.setAdapter(selectUserAdapter);
                }

            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(getContext(), "Can't get friends. Please check internet connection",
                        Toast.LENGTH_LONG).show();
                Log.e("Get Friend List", "onFailure: " + t.toString() );
            }
        });

    }

    private void InitUser() {
        filteredUsers.clear();
        if(!mFriends.isEmpty()){
            for(User user : mFriends){
                filteredUsers.add(user);
            }
        }
        selectUserAdapter.notifyDataSetChanged();
    }

    private void searchUser(String s) {
        filteredUsers.clear();
        for(User user : mFriends) {
            if(user.getFirstName().toLowerCase().contains(s.toLowerCase())){
                filteredUsers.add(user);
            }
        }

        selectUserAdapter.notifyDataSetChanged();
    }

    private void addMember() {
        for(User user : mSelectedUsers) {
            Call<Group> addCall = service.addGroup(currentUser.getJwt(), user.getid(), cur_group.getId());
            addCall.enqueue(new Callback<Group>() {
                @Override
                public void onResponse(Call<Group> call, Response<Group> response) {
                    if(!response.isSuccessful()){
                        Toast.makeText(getContext(), response.message(),
                                Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(getContext(), "Invitation sent",
                                Toast.LENGTH_LONG).show();
                        //TODO: NOTIFY MEMBER
                    }
                }

                @Override
                public void onFailure(Call<Group> call, Throwable t) {
                    Toast.makeText(getContext(), t.toString(),
                            Toast.LENGTH_LONG).show();
                }
            });
        }
    }
    private void tryDelete() {
        SharedPreferences prefs;

        //get current user
        prefs = getSharedPreferences("",
                MODE_PRIVATE);

        String user_id = prefs.getString("current_user_id", "");
        GetDataService service = RetrofitInstance.getRetrofitInstance().create(GetDataService.class);
        Call<Group> call = service.deleteGroup(currentUser.getJwt(), user_id, cur_group.getId());
        call.enqueue(new Callback<Group>() {
            @Override
            public void onResponse(Call<Group> call, Response<Group> response) {
                if(response.isSuccessful()){
                    goBackToMain();
                }
                else {
                    Toast.makeText(getApplicationContext(), response.message(),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<Group> call, Throwable t) {
                Toast.makeText(getApplicationContext(), t.toString(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void goBackToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.group_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.leave_group :
                tryDelete();
                return true;
            case R.id.add_member :
                addMemberPopup(findViewById(R.id.group_calendar_view));
                return true;
        }
        return true;
    }

}
