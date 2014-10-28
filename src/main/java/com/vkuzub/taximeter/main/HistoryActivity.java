package com.vkuzub.taximeter.main;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by Vyacheslav on 28.10.2014.
 */
public class HistoryActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActionBar().setDisplayHomeAsUpEnabled(true);
    }
}