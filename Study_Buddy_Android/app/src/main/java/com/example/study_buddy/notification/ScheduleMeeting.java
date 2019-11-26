package com.example.study_buddy.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.study_buddy.App;
import com.example.study_buddy.MainActivity;
import com.example.study_buddy.R;
import com.example.study_buddy.model.User;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ScheduleMeeting {
    private static final String TAG = ScheduleMeeting.class.getSimpleName();
    public static final String CHANNEL_ID_1 = "what is this";
    private static SharedPreferences.Editor editor;
    private static SharedPreferences sharedPref;

    public static void receiveMeetingResultAccept(RemoteMessage remoteMessage){
        PendingIntent pendingMain =
                PendingIntent.getActivity(App.getContext(), 0,
                        new Intent(App.getContext().getApplicationContext(), MainActivity.class),
                        PendingIntent.FLAG_UPDATE_CURRENT );
        String body = "";
        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sssZ", Locale.CANADA);
        try {
            Date startTime = date.parse(remoteMessage.getData().get("startTime"));
            Date endTime = date.parse(remoteMessage.getData().get("endTime"));
            body = "Time: From " + startTime.getHours() + " to " + endTime.getHours();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        body = body.concat("\nDescription: " +remoteMessage.getNotification().getBody());

        NotificationCompat.Builder builder = new NotificationCompat.Builder(App.getContext(), MainActivity.CHANNEL_ID_1)
                .setSmallIcon(R.drawable.ic_check_black_24dp)
                .setContentTitle(remoteMessage.getNotification().getTitle())
//                .setContentIntent(notificationPendingIntent)
                .setDefaults(Notification.DEFAULT_ALL)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(body))
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setCategory(NotificationCompat.CATEGORY_EVENT)
                .setAutoCancel(true)
                .setContentIntent(pendingMain);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(App.getContext());

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(3, builder.build());

    }
    public static void receiveMeetingResultReject(RemoteMessage remoteMessage){
        String body = "";
        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.CANADA);
        try {
            Date startTime = date.parse(remoteMessage.getData().get("startTime"));
            Date endTime = date.parse(remoteMessage.getData().get("endTime"));
            body = "Time: From " + startTime.getHours() + " to " + endTime.getHours();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        body = body.concat("\nDescription: " +remoteMessage.getNotification().getBody());

        NotificationCompat.Builder builder = new NotificationCompat.Builder(App.getContext(), MainActivity.CHANNEL_ID_1)
                .setSmallIcon(R.drawable.ic_close_black_24dp)
                .setContentTitle(remoteMessage.getNotification().getTitle())
//                .setContentIntent(notificationPendingIntent)
                .setDefaults(Notification.DEFAULT_ALL)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(body))
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setCategory(NotificationCompat.CATEGORY_EVENT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(App.getContext());

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(2, builder.build());

    }


    /**
     * The receivers from owner of the event.
     * @param remoteMessage
     */
    public static void receiveMeetingNotification(RemoteMessage remoteMessage) {
        // check if current user already exist. If so, ship login.
        sharedPref = App.getContext().getSharedPreferences("", Context.MODE_PRIVATE);
        editor  = sharedPref.edit();
        String user = sharedPref.getString("current_user", "");
        Gson gson = new Gson();
        User currentUser = gson.fromJson(user, User.class);

        Intent accept = new Intent(App.getContext().getApplicationContext(), NotificationAccept.class);
        Intent reject = new Intent(App.getContext().getApplicationContext(), NotificationReject.class);


        accept.putExtra("userId", currentUser.getid());
        accept.putExtra("eventId", remoteMessage.getData().get("eventId"));

        reject.putExtra("userId", currentUser.getid());
        reject.putExtra("eventId", remoteMessage.getData().get("eventId"));

        PendingIntent pendingAccept =
                PendingIntent.getActivity(App.getContext(), 0,
                        accept, PendingIntent.FLAG_UPDATE_CURRENT );
        PendingIntent pendingReject =
                PendingIntent.getActivity(App.getContext(), 0,
                        reject, PendingIntent.FLAG_UPDATE_CURRENT );


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
                        pendingAccept)
                .addAction(R.drawable.ic_close_black_24dp, App.getContext().getResources().getString(R.string.refuse),
                        pendingReject);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(App.getContext());

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, builder.build());
    }


}
