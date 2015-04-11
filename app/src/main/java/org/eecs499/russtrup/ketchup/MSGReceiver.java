package org.eecs499.russtrup.ketchup;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.WakefulBroadcastReceiver;

public class MSGReceiver extends WakefulBroadcastReceiver {
    public MSGReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();

        Intent msgrcv = new Intent("Msg");
        msgrcv.putExtra("msg", extras.getString("msg"));
        msgrcv.putExtra("title", extras.getString("title"));
        msgrcv.putExtra("season", extras.getString("season"));
        msgrcv.putExtra("episode_number", extras.getString("episode_number"));

        LocalBroadcastManager.getInstance(context).sendBroadcast(msgrcv);
        ComponentName comp = new ComponentName(context.getPackageName(), MSGService.class.getName());
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }
}
