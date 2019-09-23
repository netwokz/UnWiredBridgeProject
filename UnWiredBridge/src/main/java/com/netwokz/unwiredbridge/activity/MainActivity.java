package com.netwokz.unwiredbridge.activity;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.netwokz.unwiredbridge.R;
import com.netwokz.unwiredbridge.notif.NotifManager;
import com.netwokz.unwiredbridge.receiver.ADBReceiver;
import com.netwokz.unwiredbridge.util.ADB;
import com.netwokz.unwiredbridge.util.Dialog;
import com.netwokz.unwiredbridge.util.Root;
import com.netwokz.unwiredbridge.util.WiFi;

import java.lang.ref.WeakReference;
import java.util.Locale;


public class MainActivity extends Activity implements SharedPreferences.OnSharedPreferenceChangeListener {

    SharedPreferences prefs;
    private static String PREF_PERSIST_NOTIF;
    private static Context mContext;

    private boolean prefWifiOn = true;
    private boolean prefPersist = false;
    private boolean isWorking = false;

    private static ImageView image;
    private static View ipContainer;
    private static TextView ipText;

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        createNotificationChannel();

        mContext = this;
        PREF_PERSIST_NOTIF = getResources().getString(R.string.pref_notif_key);

        new RootCheckTask(this).execute();
        loadData();
        setViews();

        boolean isWifiConnected = WiFi.isConnected(this);
        if (!isWifiConnected && prefWifiOn) {
            WiFi.saveInitialWifiState(this, isWifiConnected);
            WiFi.setEnabled(this, true);
        }
        updateUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (prefs == null)
            prefs.registerOnSharedPreferenceChangeListener(this);
        loadData();
        updateUI();
        sendBroadcast();
    }

    private void setViews() {
        image = findViewById(R.id.main_image);
        final String result = isWorking ? "Running" : "Not Running";
        image.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!isWorking) {
                    Toast.makeText(MainActivity.mContext, "ADB is " + result, Toast.LENGTH_LONG).show();
                    toggleAdb();
                }
            }
        });
//        hint = (TextView) findViewById(R.id.main_hint);
        ipContainer = findViewById(R.id.main_ipContainer);
        ipText = findViewById(R.id.ip_address_view);
        mToolbar = findViewById(R.id.tool_bar);
        setActionBar(mToolbar);
    }

    private void loadData() {
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefWifiOn = prefs.getBoolean(getString(R.string.pref_wifi_on_key), prefWifiOn);
        prefPersist = prefs.getBoolean(getString(R.string.pref_notif_key), prefPersist);
    }

    public static void updateUI() {
        if (ADB.isEnabled(mContext)) {
            image.setImageResource(R.drawable.wifi_adb_on);
//            hint.setText(R.string.main_hintOff);
            ipContainer.setVisibility(View.VISIBLE);
            ipText.setText(getIpAddress());
        } else {
            image.setImageResource(R.drawable.wifi_adb_off);
//            hint.setText(R.string.main_hintOn);
            ipContainer.setVisibility(View.INVISIBLE);
        }
    }

    public static String getIpAddress() {
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String ip = null;
        if (wifiInfo != null) {
            int ipAddress = wifiInfo.getIpAddress();
            ip = String.format(Locale.getDefault(), "%d.%d.%d.%d",
                    (ipAddress & 0xff),
                    (ipAddress >> 8 & 0xff),
                    (ipAddress >> 16 & 0xff),
                    (ipAddress >> 24 & 0xff));
        } else {
            ip = "0.0.0.0";
        }
        return "IP: " + ip;
    }

    private void sendBroadcast() {
        sendBroadcast(new Intent(this, ADBReceiver.class));
    }

    private void toggleAdb() {
        if (!WiFi.isConnected(this))
            wifiOffDialog();
        else {
            boolean isADBEnabled = ADB.isEnabled(this);
            image.setImageResource(isADBEnabled ? R.drawable.anim_adb_off : R.drawable.anim_adb_on);
            ((AnimationDrawable) image.getDrawable()).start();
            new ADBTask().execute();
        }
    }

    private void wifiOffDialog() {
        Dialog.question(this, R.string.main_wifiQuestion,
                R.string.main_connect, new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        WiFi.setEnabled(MainActivity.this, true);
                        dialog.dismiss();
                    }
                },
                R.string.main_quit, new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.main_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(PREF_PERSIST_NOTIF)) {
            new ADBTask().execute();
            new ADBTask().execute();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (prefs != null)
            prefs.unregisterOnSharedPreferenceChangeListener(this);

        if (!prefPersist && !ADB.isEnabled(MainActivity.this))
            NotifManager.cancel(MainActivity.this);
    }

    private class ADBTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            isWorking = true;
            ADB.toggle(MainActivity.this);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            isWorking = false;
            updateUI();
        }
    }

    public void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "ADB";
            String description = "ADB Service Notification";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("ADB", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private static class RootCheckTask extends AsyncTask<Void, Void, Boolean> {

        // Weak references will still allow the Activity to be garbage-collected
        private final WeakReference<Activity> weakActivity;

        RootCheckTask(Activity myActivity) {
            this.weakActivity = new WeakReference<>(myActivity);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            return Root.hasRootPermission();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (!result)
                Dialog.error(weakActivity.get(), R.string.main_noRoot);
        }
    }
}