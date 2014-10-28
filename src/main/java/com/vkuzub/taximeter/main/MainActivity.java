package com.vkuzub.taximeter.main;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.vkuzub.taximeter.R;
import com.vkuzub.taximeter.utils.TaximeterUtils;

public class MainActivity extends Activity implements View.OnClickListener {

    private Button btnStart, btnStop, btnHistory, btnSettings;
    private TextView tvDistance, tvSum;

    private double prefMinTarif, prefTarifEnter, prefTarif;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initWidgets();
    }

    @Override
    protected void onStart() {
        super.onStart();
        readPreferences();
    }

    private void initWidgets() {
        btnStart = (Button) findViewById(R.id.btnStart);
        btnStart.setOnClickListener(this);

        btnStop = (Button) findViewById(R.id.btnStop);
        btnStop.setOnClickListener(this);

        btnHistory = (Button) findViewById(R.id.btnHistory);
        btnHistory.setOnClickListener(this);

        btnSettings = (Button) findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(this);

        tvDistance = (TextView) findViewById(R.id.tvDistance);
        tvSum = (TextView) findViewById(R.id.tvSum);

    }

    private void readPreferences() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        prefMinTarif = Float.parseFloat(sp.getString("min_tarif", "17"));
        prefTarifEnter = Float.parseFloat(sp.getString("vhodit", "5"));
        prefTarif = Float.parseFloat(sp.getString("tarif", "2.2"));

        Log.d(TaximeterUtils.LOG_TAG, prefMinTarif + " " + prefTarifEnter + " " + prefTarif);
    }

    @Override
    public void onClick(View v) {
        Intent intent;

        switch (v.getId()) {
            case R.id.btnStart:
                //TODO start service;
                break;
            case R.id.btnStop:
                //TODO stop service, запись в БД;
                break;
            case R.id.btnHistory:
                intent = new Intent(this, HistoryActivity.class);
                startActivity(intent);
                break;
            case R.id.btnSettings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
        }
    }
}

