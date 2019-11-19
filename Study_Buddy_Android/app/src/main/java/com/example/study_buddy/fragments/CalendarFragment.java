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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.study_buddy.R;
import com.example.study_buddy.adapter.BlockAdapter;
import com.example.study_buddy.adapter.GroupBlockAdapter;
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
    private int test;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_calendar, container, false);
        mFragment = this;
        calendar_recyclerView = view.findViewById(R.id.calendar);
        calendar_recyclerView.setHasFixedSize(true);
        calendar_recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        /*****get event of the day, store to mEvent array*********/

        //get current user
        prefs = Objects.requireNonNull(getContext()).getSharedPreferences(
                "",MODE_PRIVATE);
        Gson gson = new Gson();

        test = prefs.getInt("test", 0);

        if(test == 1){
            String json = prefs.getString("test_user_calendar", "");
            MyCalendar calendar = gson.fromJson(json, MyCalendar.class);
            mEvent = calendar.getmEvents();
            Log.e(TAG, "onCreateView: get event list" + json );
            blockAdapter = new BlockAdapter(getContext(),this, mEvent);
            calendar_recyclerView.setAdapter(blockAdapter);
        }
        else {
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
        }

        // just use 1 calendar for now. TODO: change to the calendar picked.

        // group calendar button for now
        Button groupbtn = view.findViewById(R.id.group_btn);
        Button myCalendarbtn = view.findViewById(R.id.Personal);

        groupbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getGroupCalendar();
            }
        });

        myCalendarbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blockAdapter = new BlockAdapter(getContext(),mFragment, mEvent);
                calendar_recyclerView.setAdapter(blockAdapter);
            }
        });


        // create the popup window
        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        popupWindow = new PopupWindow();
        popupWindow.setWidth(width);
        popupWindow.setHeight(height);
        popupWindow.setFocusable(true);

        return view;
    }

    private void getGroupCalendar(){
        /**
         *  1. Get request for the group list
         *  2. Merge them into the groupCalendar
         *  3. Get the owner of each calendar
         *  4. Pass the groupCalendar and the owner list to the adapter
         *  5. Set adapter
         * */

        List<List<Event>> groupCalendar = new ArrayList<>();
        List<String> users = new ArrayList<>();
        if(test == 1){
            users.add("TestUser");
        }
        else {
            users.add(currentUser.getFirstName());
        }
        users.add("TestUser 2");

        List<Event> testList1 = new ArrayList<>(Collections.nCopies(18, null));
        if(mEvent.get(4) != null){
            testList1.set(0, mEvent.get(4));
            testList1.set(3, mEvent.get(4));
            testList1.set(4, mEvent.get(4));
            testList1.set(6, mEvent.get(4));
            testList1.set(10, mEvent.get(4));
            testList1.set(13, mEvent.get(4));
        }
        else {
            Log.e("testList", "mEvent at 3 is null");
        }

        for(int i = 0; i < 18; i++){
            List<Event> events = new ArrayList<>();
            events.add(mEvent.get(i));
            events.add(testList1.get(i));
            groupCalendar.add(events);
        }

        GroupBlockAdapter groupBlockAdapter =
                new GroupBlockAdapter(getContext(), mFragment, groupCalendar, users);
        calendar_recyclerView.setAdapter(groupBlockAdapter);
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

                recyclerView.setVisibility(View.INVISIBLE);
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
        View popupView = inflater.inflate(R.layout.schedule_meeting_details, null);


//        // show the popup window
//        // which view you pass in doesn't matter, it is only used for the window tolken
        popupWindow.setContentView(popupView);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
        setUpView(popupView);

        String members = "";
        if(!mSelectedUsers.isEmpty()){
            for(User user : mSelectedUsers) {
                members += user.getFirstName() + ",  ";
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

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                showScheduleMeetingStartUp(v);
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

    private void setUpView(View popupView) {
        s_frequency = "";


        back_btn = popupView.findViewById(R.id.back_btn);
        meeting_member = popupView.findViewById(R.id.member_names);
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
        showScheduleMeetingStartUp(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void tryCreateMeeting() {
        /*
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
        Date startTime = new GregorianCalendar(2019, Calendar.NOVEMBER,1, hour, 0).getTime();
        Date endTime = new GregorianCalendar(2019, Calendar.NOVEMBER,1, hour + 1, 0).getTime();

        // create the event
//        GetDataService service = RetrofitInstance.getRetrofitInstance().create(GetDataService.class);

        ArrayList<String> friendIdList = new ArrayList<>();
        for (User user : mSelectedUsers) {
            friendIdList.add(user.getid());
        }
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

                    /**
                     *  For test purpose only
                     * */

                    Event event = new Event(title.getText().toString(),
                            description.getText().toString(),
                            startTime,
                            endTime);

                    mEvent.set(hour-6,event);
                    blockAdapter.setItems(mEvent);
                    blockAdapter.notifyItemChanged(hour-6);
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


    }



//    private  static final String TAG = "CalendarActivity";
//    private CalendarView mCalendarView;
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.calendar_layout);
//        mCalendarView = (CalendarView) findViewById(R.id.calendarView);
//        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
//            @Override
//            public void onSelectedDayChange(CalendarView CalendarView, int year, int month, int dayOfMonth) {
//                String date = year + "/" + month + "/"+ dayOfMonth ;
//                Log.d(TAG, "onSelectedDayChange: yyyy/mm/dd:" + date);
//                Intent intent = new Intent(CalendarActivity.this, MainActivity.class);
//                intent.putExtra("date",date);
//                startActivity(intent);
//
//            }
//        });

    }
