package com.vkuzub.taximeter.main;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import com.vkuzub.taximeter.R;

/**
 * Created by Vyacheslav on 28.10.2014.
 */
public class SettingsActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        addPreferencesFromResource(R.xml.preferences);
    }
}