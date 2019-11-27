package com.example.study_buddy;

import android.util.Log;

import com.example.study_buddy.notification.ScheduleMeeting;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessage extends FirebaseMessagingService {
    private static final String TAG = FirebaseMessage.class.getSimpleName();

    private static final int MEETING_INVITATION_RESPONSE = 1;
    private static final int MEETING_INVITATION_REQUEST_ACCEPT = 2;
    private static final int MEETING_INVITATION_REQUEST_REJECT = 3;
    private static final int FRIEND_ADD_REQUEST = 4;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "received message");
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        int notificationMode = Integer.parseInt(remoteMessage.getData().get("type"));
        switch (notificationMode) {
            case MEETING_INVITATION_RESPONSE:
                ScheduleMeeting.receiveMeetingNotification(remoteMessage);
                Log.e(TAG, "TYPE 1");
                break;
            case MEETING_INVITATION_REQUEST_ACCEPT:
                // notification for owner on accept
                ScheduleMeeting.receiveMeetingResultAccept(remoteMessage);
                Log.e(TAG, "TYPE 2");
                break;
            case MEETING_INVITATION_REQUEST_REJECT:
                // notification for owner on reject
                ScheduleMeeting.receiveMeetingResultReject(remoteMessage);
                Log.e(TAG, "TYPE 3");
                break;
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.e(TAG, "Refreshed token: " + token);
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }
    private void sendRegistrationToServer(String token) {
        Log.e(TAG, "refreshed token" + token);
    }
}