package com.vkuzub.taximeter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements View.OnClickListener {

    private Button btnStart, btnStop, btnHistory, btnSettings;
    private TextView tvDistance, tvSum;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initWidgets();
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

