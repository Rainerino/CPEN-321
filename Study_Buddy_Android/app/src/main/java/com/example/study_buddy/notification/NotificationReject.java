package com.example.study_buddy.notification;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.study_buddy.MainActivity;
import com.example.study_buddy.model.Event;
import com.example.study_buddy.model.User;
import com.example.study_buddy.network.GetDataService;
import com.example.study_buddy.network.RetrofitInstance;
import com.google.gson.Gson;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationReject extends AppCompatActivity {
    private static final String TAG = NotificationReject.class.getSimpleName();
    private SharedPreferences prefs;

    GetDataService service = RetrofitInstance.getRetrofitInstance().create(GetDataService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String userId = intent.getStringExtra("userId");
        String eventId = intent.getStringExtra("eventId");

        rejectNotification(userId, eventId);
        rejectEvent(userId, eventId);
        Log.e(TAG, "rejection clicked");
        Intent main = new Intent(
                NotificationReject.this, MainActivity.class);
        startActivity(main);

    }
    private void rejectEvent(String userId, String eventId) {
        // send with current userId and the eventId
        Call<User> deleteEvent = service.deleteUserFromEvent(
                userId,
                eventId
        );
        deleteEvent.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "rejection sent");
                } else {
                    Log.d(TAG, "rejection response error");
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, "rejection sending failed!");
                Toast.makeText(getApplicationContext(), "rejection sending failed!",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     *
     * @param userId
     * @param eventId
     */
    private void rejectNotification (String userId, String eventId) {

        //get current user
        prefs = Objects.requireNonNull(getApplicationContext().getSharedPreferences(
                "",MODE_PRIVATE));

        Gson gson = new Gson();
        String cur_user = prefs.getString("current_user", "");
        User currentUser = gson.fromJson(cur_user, User.class);
        // send with currrent userId and the eventId
        Call<Event> rejectCall = service.notifyRejectMeeting(
                currentUser.getJwt(),
                userId,
                eventId
        );
        rejectCall.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                // dont care lol
                if (response.isSuccessful()) {
                    Log.e(TAG, "rejection sent");
                } else {
                    Log.e(TAG, "rejection response error");
                }
            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                Log.e(TAG, "rejection sending failed!");
                Toast.makeText(getApplicationContext(), "rejection sending failed!",
                        Toast.LENGTH_LONG).show();

            }
        });

    }

}