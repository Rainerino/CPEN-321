package com.example.study_buddy.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.study_buddy.App;
import com.example.study_buddy.MainActivity;
import com.example.study_buddy.R;
import com.google.firebase.messaging.RemoteMessage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ScheduleMeeting {
    private static final String TAG = ScheduleMeeting.class.getSimpleName();
    public static final String CHANNEL_ID_1 = "hat is this";
    public static void sendScheduleMeetingNotification(){

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void receiveMeetingNotification(RemoteMessage remoteMessage) {
        PendingIntent pendingMainCalendar =
                PendingIntent.getActivity(App.getContext(), 0,
                        new Intent(App.getContext(), MainActivity.class),PendingIntent.FLAG_UPDATE_CURRENT );

        String body = "";
        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sssX", Locale.CANADA);
        try {
            Date startTime = date.parse(remoteMessage.getData().get("startTime"));
            Date endTime = date.parse(remoteMessage.getData().get("endTime"));
            body = "Time: From " + startTime.getHours() + " to " + endTime.getHours();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        body = body.concat("\nDescription: " +remoteMessage.getNotification().getBody());
        NotificationCompat.Builder builder = new NotificationCompat.Builder(App.getContext(), MainActivity.CHANNEL_ID_1)
                .setSmallIcon(R.drawable.ic_event_note_black_24dp)
                .setContentTitle(remoteMessage.getNotification().getTitle())
//                .setContentIntent(notificationPendingIntent)
                .setDefaults(Notification.DEFAULT_ALL)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(body))
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setCategory(NotificationCompat.CATEGORY_EVENT)
                .setAutoCancel(true)
                .addAction(R.drawable.ic_check_black_24dp, App.getContext().getResources().getString(R.string.accept),
                        pendingMainCalendar)
                .addAction(R.drawable.ic_close_black_24dp, App.getContext().getResources().getString(R.string.refuse),
                        pendingMainCalendar);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(App.getContext());

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, builder.build());
    }


}
