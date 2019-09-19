package com.netwokz.unwiredbridge.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.text.format.Formatter;

/**
 * Created by Steve on 9/9/13.
 */
public class WiFi {

    private static final String PREF_WIFI_STATE = "wifi_state";
    private static final String IP_DISCONNECTED = "0.0.0.0";

    public static boolean isConnected(Context context) {
        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            if (!wifiManager.isWifiEnabled() || wifiInfo.getSSID() == null)
                return false;
            return !getIp(context).equals(IP_DISCONNECTED);
        } catch (Exception e) {
            return false;
        }
    }

    public static void setEnabled(Context context, boolean enable) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(enable);
    }

    public static String getIp(Context context) {

        WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();

        return Formatter.formatIpAddress(ip);


//        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//        int ip = wifiManager.getConnectionInfo().getIpAddress();
//        return (ip & 0xFF) + "." + ((ip >> 8) & 0xFF) + "." + ((ip >> 16) & 0xFF) + "." + ((ip >> 24) & 0xFF);
    }

    public static void saveInitialWifiState(Context context, boolean isWifiConnected) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putBoolean(PREF_WIFI_STATE, isWifiConnected).apply();
    }

    public static boolean getInitialState(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(PREF_WIFI_STATE, false);
    }

}
