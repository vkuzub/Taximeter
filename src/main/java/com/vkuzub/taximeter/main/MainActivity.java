package com.vkuzub.taximeter.main;

import android.app.Activity;
import android.content.*;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.vkuzub.taximeter.R;
import com.vkuzub.taximeter.database.DBHelper;
import com.vkuzub.taximeter.service.CounterService;
import com.vkuzub.taximeter.utils.TaximeterUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends Activity implements View.OnClickListener {

    private Button btnStart, btnStop, btnHistory, btnSettings;
    private TextView tvDistance, tvSum;

    private double prefMinTarif, prefTarifEnter, prefTarif;

    private DBHelper dbHelper;

    private CounterService counterService;
    private boolean isServiceBound;

    private boolean isProcessActive;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initWidgets();
        initCounterService();

        dbHelper = new DBHelper(this, null);
    }

    @Override
    protected void onStart() {
        super.onStart();
        readPreferences();

        Intent intent = new Intent(this, CounterService.class);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);

    }

    private void initCounterService() {

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

    private boolean writeToDB(String sum, String distance) {
        ContentValues cv = new ContentValues();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        String date = dateFormat.format(cal.getTime());

        cv.put("date", date);
        cv.put("sum", sum);
        cv.put("distance", distance);

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.insert(DBHelper.tableName, null, cv);
        db.close();
        return true;
    }

    @Override
    protected void onDestroy() {


        super.onDestroy();
        dbHelper.close();

        if (isServiceBound) {
            unbindService(connection);
            isServiceBound = false;
        }


    }

    @Override
    public void onBackPressed() {

        if (isProcessActive) {
            Toast.makeText(getApplicationContext(), "Остановите процесс подсчета, для выхода", Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent;

        switch (v.getId()) {
            case R.id.btnStart:
                if (!isProcessActive) {
                    //TODO start service;
                    counterService.startCounter();
                    isProcessActive = true;
                    btnStart.setText("Пауза");
                } else {
                    //TODO pause
                    counterService.pauseCounter();
                    btnStart.setText("Старт");
                }

                break;
            case R.id.btnStop:
                //TODO stop service, запись в БД;

                isProcessActive = false;
                counterService.stopCounter();
                writeToDB(tvSum.getText().toString(), tvDistance.getText().toString());
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


    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            CounterService.LocalBinder binder = (CounterService.LocalBinder) iBinder;
            counterService = binder.getService();
            isServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isServiceBound = false;
        }
    };

}

