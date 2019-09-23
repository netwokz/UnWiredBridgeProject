package com.netwokz.unwiredbridge.activity;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.netwokz.unwiredbridge.R;

/**
 * Created by Steve on 9/9/13.
 */
public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);

        Context mContext = this.getActivity();
        mContext.setTheme(R.style.PreferenceScreen);
    }
}
