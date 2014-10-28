package com.vkuzub.taximeter;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by Vyacheslav on 28.10.2014.
 */
public class SettingsActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
    }
}