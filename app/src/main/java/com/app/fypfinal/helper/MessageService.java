package com.app.fypfinal.helper;

import android.util.Log;

import androidx.annotation.NonNull;

import com.app.fypfinal.activities.PostmanDashboard;
import com.app.fypfinal.activities.UserDashboard;
import com.app.fypfinal.utils.Utils;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.pubnub.api.callbacks.PNCallback;
import com.pubnub.api.enums.PNPushType;
import com.pubnub.api.models.consumer.PNStatus;
import com.pubnub.api.models.consumer.push.PNPushAddChannelResult;

import java.util.Arrays;

public class MessageService extends FirebaseMessagingService {
    private static final String TAG = "FirebaseService";

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "NEW_TOKEN: " + token);
        sendRegistrationToPubNub(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        // If the application is in the foreground handle or display both data and notification FCM messages here.
        // Here is where you can display your own notifications built from a received FCM message.
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
        }
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
        // displayNotification(remoteMessage.getNotification().getBody());
    }

    private void sendRegistrationToPubNub(String token) {
        // Configure PubNub push notifications.
        if (Utils.profilePojo.isCustomer())
            UserDashboard.pubNub.addPushNotificationsOnChannels()
                    .pushType(PNPushType.GCM)
                    .channels(Arrays.asList("HelloPush", "TestPushChannel"))
                    .deviceId(token)
                    .async(new PNCallback<PNPushAddChannelResult>() {
                        @Override
                        public void onResponse(PNPushAddChannelResult result, PNStatus status) {
                            Log.d("PUBNUB", "-->PNStatus.getStatusCode = " + status.getStatusCode());
                        }
                    });
        else
            PostmanDashboard.pubNub.addPushNotificationsOnChannels()
                    .pushType(PNPushType.GCM)
                    .channels(Arrays.asList("HelloPush", "TestPushChannel"))
                    .deviceId(token)
                    .async(new PNCallback<PNPushAddChannelResult>() {
                        @Override
                        public void onResponse(PNPushAddChannelResult result, PNStatus status) {
                            Log.d("PUBNUB", "-->PNStatus.getStatusCode = " + status.getStatusCode());
                        }
                    });
    }
}
