package com.netwokz.unwiredbridge.receiver;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.netwokz.unwiredbridge.notif.NotifManager;
import com.netwokz.unwiredbridge.util.ADB;
import com.netwokz.unwiredbridge.util.ScreenLock;

/**
 * Created by Steve on 9/9/13.
 */
public class ADBReceiver extends BroadcastReceiver {

    public static final String ACTION_STOP = "action_stop";
    public static final String ACTION_START = "action_start";

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction() != null && intent.getAction().equals(ACTION_STOP)) {
            if (ADB.isEnabled(context))
                ADB.stop(context);
        } else if (intent.getAction() != null && intent.getAction().equals(ACTION_START)) {
            if (!ADB.isEnabled(context))
                ADB.start(context);
        } else {
            if (ADB.isEnabled(context))
                start(context);
            else
                stop(context);
        }
    }

    public static void start(Context context) {
        Notification mNotif = NotifManager.buildNotification(context, true);
        NotifManager.show(context, mNotif);
        ScreenLock.start(context);
    }

    public static void stop(Context context) {
        Notification mNotif = NotifManager.buildNotification(context, false);
        NotifManager.show(context, mNotif);
        ScreenLock.stop(context);
    }
}
