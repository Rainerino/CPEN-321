package com.example.study_buddy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.security.AccessController.getContext;

public class LoadingActivity extends AppCompatActivity {
    private List<Event> mEvent = new ArrayList<>(Collections.nCopies(18, null));
    private SharedPreferences sharedPref;
    private static final String TAG = LoadingActivity.class.getSimpleName();
    private SharedPreferences.Editor editor;
    private Button button;
    private boolean calendarLoaded;
    private SharedPreferences data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        // check if current user already exist. If so, ship login.
        sharedPref = getSharedPreferences("", Context.MODE_PRIVATE);
        editor  = sharedPref.edit();
        String user = sharedPref.getString("current_user", "");
        Gson gson = new Gson();
        User currentUser = gson.fromJson(user, User.class);

        if (currentUser != null && currentUser.getid() != null && !currentUser.getid().isEmpty()  ){
            Log.e("Loading activity", "userid is " + currentUser.getid() );
            String json = sharedPref.getString("current_user_events", "");
            if(json.equals("")){
                // check if the event list are filled
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
                Log.e("Loading activity", "User Id is ready" );

                //check if the userId is still valid. If it's not valid, go to login,
                // if it is, refresh the user data.

                GetDataService service = RetrofitInstance.getRetrofitInstance().create(GetDataService.class);

                Call<User> call = service.getCurrentUser(currentUser.getid());

                call.enqueue(new Callback<User>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        Log.d(TAG, "get response" + response.raw());

                        if (response.body() != null) {
                            User user = response.body();
                            /*Save the current user id*/
                            data = Objects.requireNonNull(getSharedPreferences(
                                    "", Context.MODE_PRIVATE));
                            editor = data.edit();
                            Gson gson = new Gson();
                            String json = gson.toJson(user); // myObject - instance of MyObject
                            Log.e(TAG, "onResponse: " + json);
                            editor.putString("current_user", json);
                            editor.putString("current_user_id", user.getid());
                            editor.apply();

                            Log.d(TAG, user.getid());
                            /* Go to the main activity. Upon success
                             */
                            Intent intent = new Intent(
                                    LoadingActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            Log.e(TAG, "User doesn't exist");
                            editor.clear();
                            editor.commit();
                            Intent intent = new Intent(
                                    LoadingActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    }
                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Log.e(TAG, t.toString());
                        editor.clear();
                        editor.commit();
                        Intent intent = new Intent(
                                LoadingActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }

                });
            }
        } else {
            Log.e("Loading activity", "No userid found, go to login activity" );
            Intent intent = new Intent(
                    LoadingActivity.this, LoginActivity.class);
            startActivity(intent);
        }

    }

    public class EventRequest extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            SharedPreferences pref = getSharedPreferences("", Context.MODE_PRIVATE);
            String user = pref.getString("current_user", "");
            Gson gson = new Gson();
            User currentUser = gson.fromJson(user, User.class);
            GetDataService service = RetrofitInstance.getRetrofitInstance().create(GetDataService.class);

            List<String> calendarList = currentUser.getCalendarList();
            List<Event> mEvent = new ArrayList<>(Collections.nCopies(18, null));

            Call<List<Event>> eventCall = service.getAllEvents(calendarList.get(0));

            try {
                for(Event event : eventCall.execute().body()){
                    if(event.getStartTime().getHours()-6>0){
                        mEvent.set(event.getStartTime().getHours()-6, event);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            MyCalendar mCalendar = new MyCalendar(mEvent);
            SharedPreferences.Editor editor = pref.edit();
            String events = gson.toJson(mCalendar); // myObject - instance of MyObject
            Log.e("Loading activity", "onCreate: get event list" + events);
            editor.putString("current_user_events", events);
            editor.apply();
            calendarLoaded = true;

            return null;
        }
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
                    if(event.getStartTime().getHours()-6>0){
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





