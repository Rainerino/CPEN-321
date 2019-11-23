package com.example.study_buddy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.LocationResult;

public class MyLocationService extends BroadcastReceiver {
    private static final String TAG = MyLocationService.class.getSimpleName();
    public static final String ACTION_PROCESS_UPDATE = "com.example.study_buddy.UPDATE_LOCATION";
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent != null) {
            final String action = intent.getAction();
            Log.e(TAG, action);
            if(ACTION_PROCESS_UPDATE.equals(action)) {
                LocationResult result = LocationResult.extractResult(intent);
                if (result != null) {
                    Location location = result.getLastLocation();
                    try {
                        Log.e(TAG, location.getLongitude() + " " + location.getLatitude());
                        Toast.makeText(context, location.toString(), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    }
}
