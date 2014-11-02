package com.vkuzub.taximeter.main;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.*;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.vkuzub.taximeter.R;
import com.vkuzub.taximeter.database.DBHelper;
import com.vkuzub.taximeter.service.CounterService;
import com.vkuzub.taximeter.utils.TaximeterUtils;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

public class MainActivity extends Activity implements View.OnClickListener {

    private Button btnStart, btnStop, btnHistory, btnSettings;
    private TextView tvDistance, tvPrice, tvGpsStatus;
    private LinearLayout llGpsStatus;

    private double prefMinTarif, prefTarifEnter, prefTarif;
    private double price = 0, distance = 0;
    private double latlonPrevious[] = new double[2];

    private CounterService counterService;
    private boolean isServiceBound;

    private boolean isProcessActive;
    private boolean isGPSActive;

    public static final String PARAM_INTENT = "pendingIntent";
    public static final String DISTANCE = "distance";
    public static final String PARAM_STATUS_GPS = "gps_status";
    public static final int PARAM_CODE_DISTANCE = 333;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initWidgets();
        initCounterService();
    }

    @Override
    protected void onStart() {
        super.onStart();
        readPreferences();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!TaximeterUtils.isGPSLocationAvailable(getApplicationContext())) {
            GpsDisabledDialog gpsDisabledDialog = new GpsDisabledDialog();
            gpsDisabledDialog.show(getFragmentManager(), "GpsDisabledDialog");
            return;
        }
        if (isServiceBound == false) {
            initCounterService();
        }
    }


    private void updateGPSStatus(String status) {
        tvGpsStatus.setText(status);
        llGpsStatus.setBackgroundColor(Color.GREEN);
    }

    private void updateDistanceSum(String distance, String sum) {
        tvDistance.setText(distance);
        tvPrice.setText(sum);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case PARAM_CODE_DISTANCE:
                isGPSActive = data.getBooleanExtra(MainActivity.PARAM_STATUS_GPS, false);
                if (isGPSActive) {
                    updateGPSStatus("Спутники найдены");
                }
                distance += data.getDoubleExtra(MainActivity.DISTANCE, 0);
                if (isProcessActive) {
                    updateInfo(distance);
                }
                Log.d(TaximeterUtils.LOG_TAG, isGPSActive + " " + distance);
                break;


        }
    }

    private void updateInfo(double distance) {
        price = calculatePrice(distance);

        DecimalFormat df = new DecimalFormat("0.00");

        tvDistance.setText(String.valueOf(df.format(distance / 1000)) + " км");
//        tvPrice.setText(df.format(price) + " uah");

        Log.d(TaximeterUtils.LOG_TAG, (distance / 1000) + " " + price);
    }

    private void clearInfo() {
        tvDistance.setText("0");
        tvPrice.setText("0");
    }

    private double calculatePrice(double distance) {
        if (distance < 5 * 1000) {
            return prefMinTarif;
        } else {
            price = prefTarif * (distance / 1000) + prefTarifEnter;
        }
        return price;
    }


    private void initCounterService() {
        PendingIntent pendingIntent = createPendingResult(PARAM_CODE_DISTANCE, new Intent(), 0);
        Intent intent = new Intent(getApplicationContext(), CounterService.class).putExtra(PARAM_INTENT, pendingIntent);
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
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
        tvPrice = (TextView) findViewById(R.id.tvSum);
        tvGpsStatus = (TextView) findViewById(R.id.tvGPSStatus);

        llGpsStatus = (LinearLayout) findViewById(R.id.llGPSStatus);
        llGpsStatus.setBackgroundColor(Color.RED);
    }

    private void readPreferences() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        prefMinTarif = Float.parseFloat(sp.getString("min_tarif", "17"));
        prefTarifEnter = Float.parseFloat(sp.getString("vhodit", "5"));
        prefTarif = Float.parseFloat(sp.getString("tarif", "2.2"));

        Log.d(TaximeterUtils.LOG_TAG, prefMinTarif + " " + prefTarifEnter + " " + prefTarif);
    }

    private boolean writeToDB(String sum, String distance) {
        DBHelper dbHelper = new DBHelper(this, null);

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
        dbHelper.close();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

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
                if (isGPSActive) {
                    if (!isProcessActive) {
                        //TODO start service;
                        counterService.startCounter();
                        isProcessActive = true;
                        btnStart.setText("Пауза");

                        distance = 0;
                        price = 0;
                        clearInfo();
                    } else {
                        //TODO pause
                        counterService.pauseCounter();
                        btnStart.setText("Старт");
                        isProcessActive = false;

                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Поиск спутников GPS, ожидайте", Toast.LENGTH_SHORT).show();
                }


                break;
            case R.id.btnStop:
                //TODO stop service, запись в БД;

                isProcessActive = false;
                btnStart.setText("Старт");
                counterService.stopCounter();
                writeToDB(tvPrice.getText().toString(), tvDistance.getText().toString());

                DecimalFormat df = new DecimalFormat("0.00");

                tvPrice.setText(df.format(calculatePrice(distance)) + " uah");


                distance = 0;
                price = 0;

                break;
            case R.id.btnHistory:
                if (!isProcessActive) {
                    intent = new Intent(this, HistoryActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Недоступно во время поездки", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnSettings:
                if (!isProcessActive) {
                    intent = new Intent(this, SettingsActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Недоступно во время поездки", Toast.LENGTH_SHORT).show();
                }

                break;
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
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

