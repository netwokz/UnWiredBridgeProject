package com.netwokz.unwiredbridge.util;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import com.netwokz.unwiredbridge.R;
import com.netwokz.unwiredbridge.notif.NotifManager;

/**
 * Created by Steve on 9/10/13.
 */
public class ADBService extends Service {

    private static Context mContext;
    SharedPreferences mPrefs;
    boolean mEnableAtBoot = false;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mContext = getApplicationContext();
        mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        mEnableAtBoot = mPrefs.getBoolean(mContext.getResources().getString(R.string.pref_boot_up_auto_connect), mEnableAtBoot);

        if (mEnableAtBoot) {
            NotifManager.fromService = true;
            ADB.start(mContext);
        } else {
            NotifManager.fromService = true;
            NotifManager.show(getApplicationContext(), NotifManager.buildNotification(getApplicationContext(), false));
            Log.d("ADBService", "From BootReceiver...");
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
