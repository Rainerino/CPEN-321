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
import com.example.study_buddy.LoadingActivity;
import com.example.study_buddy.MainActivity;
import com.example.study_buddy.R;
import com.google.firebase.messaging.RemoteMessage;

public class ScheduleMeeting {
    private static final String TAG = ScheduleMeeting.class.getSimpleName();
    public static final String CHANNEL_ID_1 = "hat is this";
    public static void sendScheduleMeetingNotification(){

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void receiveMeetingNotification(RemoteMessage remoteMessage) {

        Intent mainCalendar = new Intent(App.getContext(), LoadingActivity.class);
        PendingIntent pendingMainCalendar =
                PendingIntent.getBroadcast(App.getContext(), 0, mainCalendar, 0);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(App.getContext(), MainActivity.CHANNEL_ID_1)
                .setSmallIcon(R.drawable.ic_forum_black_24dp)
                .setContentTitle(remoteMessage.getNotification().getTitle())
                .setContentText(remoteMessage.getNotification().getBody())
//                .setContentIntent(notificationPendingIntent)
                .setDefaults(Notification.DEFAULT_ALL)
//                .setStyle(new NotificationCompat.BigTextStyle()
//                        .bigText("Much longer text that cannot fit one line..."))
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setCategory(NotificationCompat.CATEGORY_EVENT)
                .addAction(R.drawable.ic_check_black_24dp, App.getContext().getResources().getString(R.string.accept),
                        pendingMainCalendar)
                .addAction(R.drawable.ic_close_black_24dp, App.getContext().getResources().getString(R.string.refuse),
                        pendingMainCalendar);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(App.getContext());

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, builder.build());
    }
}
