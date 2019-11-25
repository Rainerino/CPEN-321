package com.example.study_buddy.fragments;

import android.content.Context;
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

import com.example.study_buddy.App;
import com.example.study_buddy.R;
import com.example.study_buddy.adapter.BlockAdapter;
import com.example.study_buddy.adapter.SelectUserAdapter;
import com.example.study_buddy.model.Event;
import com.example.study_buddy.model.MyCalendar;
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
    private final int YEAR_START = 1900;
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
    private SimpleDateFormat df;
    private int cur_dayOfMonth;
    private int cur_month;
    private int cur_year;

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

        mEvent = new ArrayList<>(Collections.nCopies(18, null));
        blockAdapter = new BlockAdapter(getContext(), this, mEvent);
        calendar_recyclerView = view.findViewById(R.id.calendar);
        calendar_recyclerView.setHasFixedSize(true);
        calendar_recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        calendar_recyclerView.setAdapter(blockAdapter);

        df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sssX", Locale.CANADA);
        cur_dayOfMonth = Calendar.getInstance().get(Calendar.DATE);
        cur_month = Calendar.getInstance().get(Calendar.MONTH);
        cur_year = Calendar.getInstance().get(Calendar.YEAR) - YEAR_START;
        date = getDate(cur_month, cur_dayOfMonth);
        display_date.setText(date);

        /*****get event of the day, store to mEvent array*********/

        //get current user
        prefs = Objects.requireNonNull(getContext()).getSharedPreferences(
                "",MODE_PRIVATE);

        Gson gson = new Gson();
        String cur_user = prefs.getString("current_user", "");
        currentUser = gson.fromJson(cur_user, User.class);
        cur_userId = prefs.getString("current_user_id", "");

        getEvent();

        // just use 1 calendar for now. TODO: change to the calendar picked.

        // group calendar button for now
        ImageButton next_btn = view.findViewById(R.id.next_button);
        ImageButton back_btn = view.findViewById(R.id.back_button);

        next_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNextDate();
                getEvent();
            }
        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setBeforeDate();
                getEvent();
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
                        // where the calendar select the date
                        cur_month = month;
                        cur_dayOfMonth = dayOfMonth;
                        cur_year = year - YEAR_START;
                        String date = getDate(month, dayOfMonth);
                        display_date.setText(date);
                        day_calendar.setVisibility(View.VISIBLE);
                        monthly_calendar.setVisibility(View.INVISIBLE);
                        getEvent();
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

    /**
     * Make the request to get the event of the day from the user.
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void getEvent(){

        mEvent.stream().map(event -> null);

        List<String> calendarList = currentUser.getCalendarList();
        Date cur_date = Calendar.getInstance().getTime();
        cur_date.setDate(cur_dayOfMonth);
        cur_date.setMonth(cur_month);
        cur_date.setYear(cur_year);

        String currentDate = df.format(cur_date);

        Log.e(TAG, cur_date.toString());
        Log.e(TAG, currentDate);

        GetDataService service = RetrofitInstance.getRetrofitInstance().create(GetDataService.class);
        Call<List<Event>> eventCall = service.getUserEvents(currentUser.getid(), cur_date);

        Log.e(TAG, calendarList.toString());

        eventCall.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                Log.e(TAG, "onResponse: " + response.body() );
                for(Event event : response.body()){

                    if(event.getStartTime().getHours()-6>=0){
                        mEvent.set(event.getStartTime().getHours()-6, event);
                    }
                }
                blockAdapter.notifyDataSetChanged();
            }
            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                Toast.makeText(App.getContext(), "Can't get calendar. Please check event list in the calendar!",
                        Toast.LENGTH_LONG).show();
                Log.e(TAG, "onFailure: " + t.toString() );
            }
        });
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

    private void getEventDetails(View view){
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
        getEventDetails(view);
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
