package com.netwokz.unwiredbridge.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.netwokz.unwiredbridge.R;
import com.netwokz.unwiredbridge.util.ADBService;

/**
 * Created by Steve on 9/10/13.
 */
public class ADBBootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            boolean autoRun = mPrefs.getBoolean(context.getResources().getString(R.string.pref_boot_up), false);

            if (autoRun) {
                Intent mIntent = new Intent("com.netwokz.unwiredbridge.util.ADBService");
                mIntent.setClass(context, ADBService.class);
                context.startService(mIntent);
            }
        }
    }
}
