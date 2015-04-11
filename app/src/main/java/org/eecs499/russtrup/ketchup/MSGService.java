package org.eecs499.russtrup.ketchup;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * helper methods.
 */
public class MSGService extends IntentService {

    NotificationCompat.Builder notification;
    NotificationManager manager;

    public MSGService() {
        super("MSGService");
    }

    private class DummyCallback implements KetchupAPI.HTTPCallback {

        int _notificationId;

        public DummyCallback(int notificationId) {
            _notificationId = notificationId;
        }

        @Override
        public void invokeCallback(JSONObject response) throws JSONException {
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(_notificationId);
        }

        @Override
        public void onFail() {
            Log.i("DUMMY", "Failure");
        }
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

        String messageType = gcm.getMessageType(intent);

        String action = intent.getAction();
        Log.i("ACTION", action);
        if (action.equals("MSGService.ACTION_ADD")) {
            ((NotificationManager) (getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE))).cancel(extras.getInt("NOTIFICATION_ID"));
            // Close the notification
        } else if (action.equals("MSGService.ACTION_WATCHED")) {
            KetchupAPI.updateEpisode(extras.getString("SLUG"), extras.getInt("SEASON"), extras.getInt("NUMBER"), true, new DummyCallback(extras.getInt("NOTIFICATION_ID")));
        } else {
            if (messageType != null && extras != null && !extras.isEmpty()) {
                switch (messageType) {
                    case GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR:
                        Log.e("LC2", "Error");
                        break;
                    case GoogleCloudMessaging.MESSAGE_TYPE_DELETED:
                        Log.e("L2C", "Error");
                        break;
                    case GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE:
                        try {
                            Log.i("MSGSVC", extras.getString("msg"));
                        } catch (NullPointerException npe) {
                            Log.i("MSGSVC", "msg is null :(");
                        }

                        sendNotification(extras.getString("title"), extras.getString("msg"), extras.getString("slug"), Integer.valueOf(extras.getString("season")), Integer.valueOf(extras.getString("episode_number")));
                        Log.i("MSGSERIVCE", "Received: " + extras.getString("title") + " - " + extras.getString("msg"));

                        break;
                }
            }
            MSGReceiver.completeWakefulIntent(intent);
        }
    }

    private void sendNotification(String title, String msg, String slug, int season, int number) {

        long time = new Date().getTime();
        String tmpStr = String.valueOf(time);
        String last4Str = tmpStr.substring(tmpStr.length() - 5);
        int notificationId = Integer.valueOf(last4Str);

        Intent addShowToQueue = new Intent(this, MSGService.class);
        addShowToQueue.setAction("MSGService.ACTION_ADD");
        addShowToQueue.putExtra("NOTIFICATION_ID", notificationId);
        Intent markShowWatched = new Intent(this, MSGService.class);
        markShowWatched.setAction("MSGService.ACTION_WATCHED");
        markShowWatched.putExtra("SLUG", slug);
        markShowWatched.putExtra("SEASON", season);
        markShowWatched.putExtra("NUMBER", number);
        markShowWatched.putExtra("NOTIFICATION_ID", notificationId);

        PendingIntent pAddShowToQueue = PendingIntent.getService(this, 1005, addShowToQueue, PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent pMarkShowWatched = PendingIntent.getService(this, 1005, markShowWatched, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent myShows = new Intent(this, MainActivity.class);
        myShows.putExtra("SLUG", slug);
        myShows.putExtra("SEASON", season);
        myShows.putExtra("NUMBER", number);
        myShows.putExtra("NOTIFICATION_ID", notificationId);
        myShows.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent myShowsPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        myShows,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        notification = new NotificationCompat.Builder(this);
        notification.setContentTitle(title)
                    .setContentText(msg)
                    .setTicker(title)
                    .setSmallIcon(R.drawable.ic_notificication)
                    .addAction(R.drawable.ic_action_new, "Add To Queue", pAddShowToQueue)
                    .addAction(R.drawable.ic_action_accept, "I'm Watching", pMarkShowWatched)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                    .setContentIntent(myShowsPendingIntent)
                    .setAutoCancel(true);

        manager =(NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        // Play default notification sound
        Notification notif = notification.build();
        notif.defaults |= Notification.DEFAULT_SOUND;
        // Vibrate if vibrate is enabled
        notif.defaults |= Notification.DEFAULT_VIBRATE;



        manager.notify(notificationId, notif);
    }
}
