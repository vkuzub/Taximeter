package com.vkuzub.taximeter.service;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import com.vkuzub.taximeter.main.MainActivity;
import com.vkuzub.taximeter.utils.TaximeterUtils;

/**
 * Created by Vyacheslav on 28.10.2014.
 */
public class CounterService extends Service implements LocationListener {

    private int notifyId = 1337;
    private boolean isProcessStarted;
    private boolean isFirstLocationFinded;
    private boolean isPaused;
    private double latlon[] = new double[2];
    private final IBinder binder = new LocalBinder();

    private Context context;
    private Location location;
    private LocationManager locationManager;
    private double lat = 0, lon = 0;
    public static final int UPDATE_TIME = 1000;
    public static final int DISTANCE_FOR_UPDATE = 10;

    private PendingIntent pendingIntent;

    @Override
    public IBinder onBind(Intent intent) {
        pendingIntent = intent.getParcelableExtra(MainActivity.PARAM_INTENT);
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        initGPS();
        //show notify
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopUsingGps();
        //hide notify
    }

    public double getLat() {

        if (location != null) {
            lat = location.getLatitude();
        }
        return lat;
    }

    public double getLon() {
        if (location != null) {
            lon = location.getLongitude();
        }
        return lon;
    }

    public void initGPS() {
        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        if (location == null) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, UPDATE_TIME, DISTANCE_FOR_UPDATE, this);
            Log.d(TaximeterUtils.LOG_TAG, "initGPS");
        }
    }

    public void stopUsingGps() {

        if (locationManager != null) {

            locationManager.removeUpdates(CounterService.this);

        }
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        lat = location.getLatitude();
        lon = location.getLongitude();


        if (lat != 0 && lon != 0) {
            if (!isFirstLocationFinded) {
                latlon = new double[]{lat, lon};
                isFirstLocationFinded = true;
                Log.d(TaximeterUtils.LOG_TAG, getClass().getCanonicalName() + "onLocationChanged INIT" + lat + " : " + lon);

                Intent intent = null;
                intent = new Intent().putExtra(MainActivity.PARAM_STATUS_GPS, true);
                try {
                    pendingIntent.send(CounterService.this, MainActivity.PARAM_CODE_DISTANCE, intent);
                } catch (PendingIntent.CanceledException e) {
                    e.printStackTrace();
                }

            } else {
                calculateDistance(new double[]{lat, lon});
                Log.d(TaximeterUtils.LOG_TAG, getClass().getCanonicalName() + "onLocationChanged" + lat + " : " + lon);
            }
        }
    }

    private void calculateDistance(double[] latlonNew) {
        double distance = 0;

        Location locationA = new Location("point A");
        locationA.setLatitude(latlon[0]);
        locationA.setLongitude(latlon[1]);

        Location locationB = new Location("point B");
        locationB.setLatitude(latlonNew[0]);
        locationB.setLongitude(latlonNew[1]);

        distance = locationA.distanceTo(locationB);

        if (isPaused) {
            distance = 0;
        }

        Intent intent = null;
        intent = new Intent().putExtra(MainActivity.PARAM_STATUS_GPS, true);
        intent.putExtra(MainActivity.DISTANCE, distance);
        try {
            pendingIntent.send(CounterService.this, MainActivity.PARAM_CODE_DISTANCE, intent);
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }

        latlon = latlonNew.clone();
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public class LocalBinder extends Binder {
        public CounterService getService() {
            return CounterService.this;
        }

    }

    private Notification createNotification(String text) {
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);

        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(android.R.drawable.ic_media_play).setContentTitle("Taximeter").setContentText(text).setAutoCancel(true).setTicker(text);

        Notification notification = builder.getNotification();
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        return notification;
    }

    private void updateNotification(String text) {
        Notification notification = createNotification(text);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notifyId, notification);
    }


    public void startCounter() {
        if (!isProcessStarted) {
            startForeground(notifyId, createNotification("Работает"));
            isProcessStarted = true;
            isPaused = false;
        } else {
            updateNotification("Работает");
        }
    }


    public void pauseCounter() {
        updateNotification("В режиме ожидания");
        isPaused = true;
        isFirstLocationFinded = false;

    }

    public void stopCounter() {
        Toast.makeText(getApplicationContext(), "Поездка завершена", Toast.LENGTH_SHORT).show();
        isProcessStarted = false;
        isFirstLocationFinded = false;
        stopForeground(true);
    }

}
