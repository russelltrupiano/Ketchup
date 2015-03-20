package org.eecs499.russtrup.ketchup;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MSGService extends IntentService {

    NotificationCompat.Builder notification;
    NotificationManager manager;

    public MSGService() {
        super("MSGService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        String messageType = gcm.getMessageType(intent);
//        prefs = getSharedPreferences("")

        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                Log.e("LC2", "Error");
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType)) {
                Log.e("L2C","Error");
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                sendNotification(extras.getString("msg"));
                Log.i("MSGSERIVCE", "Received: " + extras.getString("msg"));
            }
        }

        MSGReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(String msg) {
        Bundle args = new Bundle();
        args.putString("msg", msg);
        Intent myShows = new Intent(this,MainActivity.class);
        myShows.putExtra("INFO", args);
        notification = new NotificationCompat.Builder(this);
        notification.setContentTitle("Show is Airing!");
        notification.setContentText(msg);
        notification.setTicker("A Show is Airing!");
        notification.setSmallIcon(R.drawable.ic_launcher);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 1000,
                myShows, PendingIntent.FLAG_CANCEL_CURRENT);
        notification.setContentIntent(contentIntent);
        notification.setAutoCancel(true);
        manager =(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, notification.build());
    }
}
