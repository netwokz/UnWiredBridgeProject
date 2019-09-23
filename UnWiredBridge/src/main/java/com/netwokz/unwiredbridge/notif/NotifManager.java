package com.netwokz.unwiredbridge.notif;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.netwokz.unwiredbridge.R;
import com.netwokz.unwiredbridge.activity.MainActivity;
import com.netwokz.unwiredbridge.receiver.ADBReceiver;
import com.netwokz.unwiredbridge.util.WiFi;

/**
 * Created by Steve on 9/9/13.
 */
public class NotifManager {

    private static final int NOTIF_ID = 1;
    private static String STOP_ADB;
    private static String START_ADB;
    private static NotificationManager mNotificationManager = null;
    private static boolean persistNotif = false;
    public static boolean fromService = false;

    public static Notification buildNotification(Context ctx, boolean adbEnabled) {

        STOP_ADB = ctx.getResources().getString(R.string.stop_adb);
        START_ADB = ctx.getResources().getString(R.string.start_adb);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        persistNotif = prefs.getBoolean(ctx.getString(R.string.pref_notif_key), persistNotif);

        Notification.Builder mNotifBuilder;
        Notification mNotification;

        if (adbEnabled) {
            mNotifBuilder = new Notification.Builder(ctx, "ADB");
            mNotifBuilder.setSmallIcon(R.drawable.ic_launcher_logo);
            mNotifBuilder.setOngoing(true);
            mNotifBuilder.setAutoCancel(false);
            mNotifBuilder.setWhen(System.currentTimeMillis());
            mNotifBuilder.setContentTitle(ctx.getResources().getString(R.string.notif_title_on));
            mNotifBuilder.setContentText(WiFi.getIp(ctx));

            Intent notificationIntent = new Intent(ctx, MainActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0, notificationIntent, 0);
            mNotifBuilder.setContentIntent(contentIntent);

            Intent cancelIntent = new Intent(ctx, ADBReceiver.class);
            cancelIntent.setAction(ADBReceiver.ACTION_STOP);
            PendingIntent cancelPendingIntent = PendingIntent.getBroadcast(ctx, 0, cancelIntent, 0);
            mNotifBuilder.addAction(android.R.drawable.ic_media_play, STOP_ADB, cancelPendingIntent);
            mNotification = mNotifBuilder.build();
        } else {
            mNotifBuilder = new Notification.Builder(ctx, "ADB");
            mNotifBuilder.setSmallIcon(R.drawable.ic_launcher_logo);
            if (persistNotif) {
                mNotifBuilder.setOngoing(true);
                mNotifBuilder.setAutoCancel(false);
            }
            mNotifBuilder.setWhen(System.currentTimeMillis());
            mNotifBuilder.setContentTitle(ctx.getResources().getString(R.string.notif_title_off));

            Intent notificationIntent = new Intent(ctx, MainActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0, notificationIntent, 0);
            mNotifBuilder.setContentIntent(contentIntent);

            Intent startIntent = new Intent(ctx, ADBReceiver.class);
            if (fromService)
                startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startIntent.setAction(ADBReceiver.ACTION_START);
            PendingIntent startPendingIntent = PendingIntent.getBroadcast(ctx, 0, startIntent, 0);
            mNotifBuilder.addAction(android.R.drawable.ic_media_play, START_ADB, startPendingIntent);
            mNotification = mNotifBuilder.build();
        }
        return mNotification;
    }

    public static void show(Context ctx, Notification notification) {
        if (mNotificationManager == null) {
            mNotificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        } else {
            mNotificationManager.cancelAll();
        }
        mNotificationManager.notify(NOTIF_ID, notification);
        if (!fromService) {
            MainActivity.updateUI();
        }
    }

    public static void cancel(Context ctx) {
        mNotificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(NOTIF_ID);
    }
}
