package com.example.study_buddy.notification;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.study_buddy.MainActivity;
import com.example.study_buddy.model.Event;
import com.example.study_buddy.network.GetDataService;
import com.example.study_buddy.network.RetrofitInstance;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationAccept extends AppCompatActivity {
    private static final String TAG = NotificationAccept.class.getSimpleName();
    GetDataService service = RetrofitInstance.getRetrofitInstance().create(GetDataService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String userId = intent.getStringExtra("userId");
        String eventId = intent.getStringExtra("eventId");
        Log.e(TAG, "userid is: " + userId + ", " + "eventId is " + eventId);
        Log.e(TAG, "accept clicked");
        acceptNotfication(userId, eventId);

        Intent main = new Intent(
                NotificationAccept.this, MainActivity.class);
        startActivity(main);
    }

    private void acceptNotfication(String userId, String eventId) {
        // call the notification with ownerId and eventId
        // send with currrent userId and the eventId
        Call<Event> acceptCall = service.notifyAcceptMeeting(
                userId,
                eventId
        );
        acceptCall.enqueue(new Callback<Event>() {
            @Override
            public void onResponse(Call<Event> call, Response<Event> response) {
                // dont care lol
                if (response.isSuccessful()) {
                    Log.e(TAG, "accept sent");
                } else {
                    Log.e(TAG, "accept response error");
                }
            }

            @Override
            public void onFailure(Call<Event> call, Throwable t) {
                Log.e(TAG, "accept sending failed!");
                Toast.makeText(getApplicationContext(), "accept sending failed!",
                        Toast.LENGTH_LONG).show();

            }
        });
    }
}