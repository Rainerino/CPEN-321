package com.example.study_buddy;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.study_buddy.model.Event;
import com.example.study_buddy.model.MyCalendar;
import com.example.study_buddy.model.User;
import com.example.study_buddy.network.GetDataService;
import com.example.study_buddy.network.RetrofitInstance;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoadingActivity extends AppCompatActivity {
    private List<Event> mEvent = new ArrayList<>(Collections.nCopies(18, null));
    private SharedPreferences sharedPref;
    private static final String TAG = LoadingActivity.class.getSimpleName();
    private SharedPreferences.Editor editor;
    private Button button;
    private boolean calendarLoaded;
    private SharedPreferences data;
    private FusedLocationProviderClient fusedLocationClient;
    private GetDataService service = RetrofitInstance.getRetrofitInstance().create(GetDataService.class);
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean permissionAccessCoarseLocationApproved =
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED;

        if (permissionAccessCoarseLocationApproved) {
            boolean backgroundLocationPermissionApproved =
                    ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                            == PackageManager.PERMISSION_GRANTED;

            if (backgroundLocationPermissionApproved) {
                // App can access location both in the foreground and in the background.
                // Start your service that doesn't have a foreground service type
                // defined.
            } else {
                // App can only access location in the foreground. Display a dialog
                // warning the user that your app must have all-the-time access to
                // location in order to function properly. Then, request background
                // location.
                ActivityCompat.requestPermissions(this, new String[] {
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                        1);
            }
        } else {
            // App doesn't have access to the device's location at all. Make full request
            // for permission.
            ActivityCompat.requestPermissions(this, new String[] {
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    },
                    1);
        }

        setContentView(R.layout.activity_loading);

        // check if current user already exist. If so, ship login.
        sharedPref = getSharedPreferences("", Context.MODE_PRIVATE);
        editor  = sharedPref.edit();
        String user = sharedPref.getString("current_user", "");
        Gson gson = new Gson();
        currentUser = gson.fromJson(user, User.class);

        // check if user is saved locally.
        if (currentUser != null && currentUser.getid() != null && !currentUser.getid().isEmpty() ){
            Log.e(TAG, "userid is " + currentUser.getid() );

            // if the current userId are saved,
            Log.e(TAG, "User Id is ready" );

            sendCurrentLocation(currentUser);

            sendRegistrationToken(currentUser);

            //check if the userId is still valid. If it's not valid, go to login,
            // if it is, refresh the user data.
            updateCurrentUser(currentUser);

            // all data loaded, go to ain activity
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(
                            LoadingActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }, 1000);
        } else {

            // if no local user is found, go to login activity.
            Log.e("Loading activity", "No userid found, go to login activity" );
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(
                            LoadingActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }, 1000);
        }

    }

    private void sendRegistrationToken(User currentUser) {
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.e("FirebaseIDService", "Registration Token: = " + token);
        Call<User> tokenCall = service.putDeviceToken(currentUser.getJwt(),
                currentUser.getid(),
                FirebaseInstanceId.getInstance().getToken()
        );
        tokenCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                // since we will load the user data next, there is no point in loading it here.
                if (response.body() != null) {
                    Log.d(TAG, "token sent");
                    Log.d("JWTJWTJWTfromREGTOKEN", "IT WOOORKKKKSSS");
                } else {
                    Log.e(TAG, "token sending failed, err code " + response);
                }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.e(TAG, "token sending failed!");
                Toast.makeText(getApplicationContext(), "No token is sent!",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private void sendCurrentLocation(User currentUser) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        // FIXME: sometimes location is null!
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            Log.e(TAG, location.getLongitude() + " " + location.getLatitude());

                            Call<User> locationCall = service.putUserLocation(currentUser.getJwt(),
                                    currentUser.getid(),
                                    location.getLongitude(),
                                    location.getLatitude()
                            );
                            locationCall.enqueue(new Callback<User>() {
                                @Override
                                public void onResponse(Call<User> call, Response<User> response) {

                                    // since we will load the user data next, there is no point in loading it here.
                                    if (response.body()!= null) {
                                        Toast.makeText(getApplicationContext(), "Location saved",
                                                Toast.LENGTH_LONG).show();
                                    } else {
                                        Log.e(TAG, "err code " + response);
                                    }
                                }
                                @Override
                                public void onFailure(Call<User> call, Throwable t) {
                                    Log.e(TAG, "location saving failed!");
                                    Toast.makeText(getApplicationContext(), "No location is saved!",
                                            Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            Toast.makeText(App.getContext(), "No last GPS location data!", Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    /**
     *
     * @param currentUser
     */
    private void updateCurrentUser(User currentUser) {

        Call<User> call = service.getCurrentUser(currentUser.getJwt(), currentUser.getid());

        call.enqueue(new Callback<User>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.d(TAG, "get response" + response.raw());

                if (response.body() != null) {
                    User user = response.body();
                    user.setJwt(currentUser.getJwt());
                    /*Save the current user id*/
                    data = Objects.requireNonNull(getSharedPreferences(
                            "", Context.MODE_PRIVATE));
                    editor = data.edit();
                    Gson gson = new Gson();
                    String json = gson.toJson(user); // myObject - instance of MyObject
//                    Log.e(TAG, "onResponse: " + json);
                    editor.putString("current_user", json);
                    editor.putString("current_user_id", user.getid());
                    editor.apply();

                    Log.e(TAG, user.getid());
                } else {
                    Log.e(TAG, "User doesn't exist");
                    // to avoid database gets flushed and userId are invalid, clean up the local user.
                    editor.clear();
                    editor.commit();
                    Intent intent = new Intent(
                            LoadingActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
            @Override
            public void onFailure(Call<User> call, Throwable t) {
                // TODO: check error code!
                Log.e(TAG, t.toString());
                editor.clear();
                editor.commit();
                Intent intent = new Intent(
                        LoadingActivity.this, LoginActivity.class);
                startActivity(intent);
            }

        });
    }
}





