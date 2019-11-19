package com.example.study_buddy;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.study_buddy.model.Event;
import com.example.study_buddy.model.MyCalendar;
import com.example.study_buddy.model.User;
import com.example.study_buddy.network.GetDataService;
import com.example.study_buddy.network.RetrofitInstance;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoadingActivity extends AppCompatActivity {
    private List<Event> mEvent = new ArrayList<>(Collections.nCopies(18, null));
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;
    private Button button;
    private boolean calendarLoaded;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        button = findViewById(R.id.nextbtn);


        // check if current user already exist. If so, ship login.
        sharedPref = getSharedPreferences("", Context.MODE_PRIVATE);
        editor  = sharedPref.edit();
        String user = sharedPref.getString("current_user", "");
        Gson gson = new Gson();
        User currentUser = gson.fromJson(user, User.class);

        if (!"".equals(user)){
            String json = sharedPref.getString("current_user_events", "");
            if(json == ""){
                Log.e("Loading activity", "onCreate: event list is empty" );
                calendarLoaded = false;
                getEvent(currentUser);
                //new EventRequest().execute();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(
                                LoadingActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }, 5000);

            }
            else {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(
                                LoadingActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }, 2000);
            }

        } else {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(
                            LoadingActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }, 2000);
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        v.getContext(), MainActivity.class);
                startActivity(intent);
            }
        });


    }


    private void getEvent(User currentUser){
        List<String> calendarList = currentUser.getCalendarList();
        List<Event> mEvent = new ArrayList<>(Collections.nCopies(18, null));

        GetDataService service = RetrofitInstance.getRetrofitInstance().create(GetDataService.class);
        Call<List<Event>> eventCall = service.getAllEvents(calendarList.get(0));

        Log.e("test", calendarList.toString());

        eventCall.enqueue(new Callback<List<Event>>() {
            @Override
            public void onResponse(Call<List<Event>> call, Response<List<Event>> response) {
                Log.e("test", response.body().toString());
                Log.e("!!!!!!!!!", "onResponse: " + response.body() );
                for(Event event : response.body()){
                    if(event.getStartTime().getHours()-6>=0){
                        mEvent.set(event.getStartTime().getHours()-6, event);
                    }
                }
                MyCalendar mCalendar = new MyCalendar(mEvent);
                SharedPreferences pref = Objects.requireNonNull(getSharedPreferences(
                        "", Context.MODE_PRIVATE));
                SharedPreferences.Editor editor = pref.edit();
                Gson gson = new Gson();
                String events = gson.toJson(mCalendar); // myObject - instance of MyObject
                Log.e("Loading activity", "onCreate: get event list" + events);
                editor.putString("current_user_events", events);
                editor.apply();
                calendarLoaded = true;
            }
            @Override
            public void onFailure(Call<List<Event>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Please check event list in the calendar!",
                        Toast.LENGTH_LONG).show();
            }
        });
    }
}





