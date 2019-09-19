package com.netwokz.unwiredbridge.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.View;
import android.widget.Toolbar;

import com.netwokz.unwiredbridge.R;

/**
 * Created by Steve on 4/26/2015.
 */
public class SettingsActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_fragment);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar_settings);
        setActionBar(toolbar);

        if (null != toolbar) {
            toolbar.setNavigationIcon(R.drawable.abc_ic_ab_back_mtrl_am_alpha);

            getActionBar().setTitle(R.string.settings);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NavUtils.navigateUpFromSameTask(SettingsActivity.this);
                }
            });

            // Inflate a menu to be displayed in the toolbar
//            actionbar.inflateMenu(R.menu.settings);
        }

        getFragmentManager().beginTransaction().replace(R.id.content_frame, new SettingsFragment()).commit();
    }

}
