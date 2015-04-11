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

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.Date;

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

        if (!extras.isEmpty()) {
            switch (messageType) {
                case GoogleCloudMessaging.
                        MESSAGE_TYPE_SEND_ERROR:
                    Log.e("LC2", "Error");
                    break;
                case GoogleCloudMessaging.
                        MESSAGE_TYPE_DELETED:
                    Log.e("L2C", "Error");
                    break;
                case GoogleCloudMessaging.
                        MESSAGE_TYPE_MESSAGE:
                    try {
                        Log.i("MSGSVC", extras.getString("msg"));
                    } catch (NullPointerException npe) {
                        Log.i("MSGSVC", "msg is null :(");
                    }
//                    sendNotif(extras.getString("title"), extras.getString("msg"));
                    sendNotification(extras.getString("title"), extras.getString("msg"));
                    Log.i("MSGSERIVCE", "Received: " + extras.getString("title") + " - " + extras.getString("msg"));
                    break;
            }
        }

        MSGReceiver.completeWakefulIntent(intent);
    }

    private void sendNotif(String title, String msg){//}, int season, int episode_number) {

        Log.i("NOTIF", "Notifying");

        Intent myshowsIntent = new Intent(this, MainActivity.class);

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                    this,
                    0,
                    myshowsIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                );

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_notificication)
                        .setContentTitle(title)
                        .setContentText(msg);

        mBuilder.setContentIntent(resultPendingIntent);

        // Sets an ID for the notification
        int mNotificationId = 001;
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }

    private void sendNotification(String title, String msg) {
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("msg", msg);

        Bundle args2 = new Bundle();
        args2.putString("title", "ADD TO QUEUE");
        args2.putString("msg", msg);

        Bundle args3 = new Bundle();
        args3.putString("title", "MARKED WATCHED");
        args3.putString("msg", msg);


        Intent myShows = new Intent(this,MainActivity.class);
        myShows.putExtra("INFO", args);

        Intent addShowToQueue = new Intent(this,MainActivity.class);
        addShowToQueue.putExtra("INFO", args2);

        Intent markShowWatched = new Intent(this,MainActivity.class);
        markShowWatched.putExtra("INFO", args3);


        PendingIntent pMyShows = PendingIntent.getActivity(this, 1000, myShows, 0);
        PendingIntent pAddShowToQueue = PendingIntent.getActivity(this, 1000, addShowToQueue, 0);
        PendingIntent pMarkShowWatched = PendingIntent.getActivity(this, 1000, markShowWatched, 0);

        notification = new NotificationCompat.Builder(this);
        notification.setContentTitle(title)
                    .setContentText(msg)
                    .setTicker(title)
                    .setSmallIcon(R.drawable.ic_notificication)
                    .addAction(R.drawable.ic_action_new, "Add To Queue", pAddShowToQueue)
                    .addAction(R.drawable.ic_action_accept, "I'm Watching", pMarkShowWatched)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                    .setContentIntent(pMyShows)
                    .setAutoCancel(true);

        manager =(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Play default notification sound
        Notification notif = notification.build();
        notif.defaults |= Notification.DEFAULT_SOUND;
        // Vibrate if vibrate is enabled
        notif.defaults |= Notification.DEFAULT_VIBRATE;

        long time = new Date().getTime();
        String tmpStr = String.valueOf(time);
        String last4Str = tmpStr.substring(tmpStr.length() - 5);
        int notificationId = Integer.valueOf(last4Str);

        manager.notify(notificationId, notif);
    }
}
