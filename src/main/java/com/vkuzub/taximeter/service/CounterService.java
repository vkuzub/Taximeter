package com.vkuzub.taximeter.service;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;
import com.vkuzub.taximeter.main.MainActivity;

/**
 * Created by Vyacheslav on 28.10.2014.
 */
public class CounterService extends Service {

    private int notifyId = 1337;
    private boolean isProcessStarted;
    private final IBinder binder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        //show notify
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //hide notify
    }

    public class LocalBinder extends Binder {
        public CounterService getService() {
            return CounterService.this;
        }
    }

    private Notification createNotification(String text) {
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);

        Notification.Builder builder = new Notification.Builder(this);
        //TODO сделать update для pause
        builder.setSmallIcon(android.R.drawable.ic_media_play).setContentTitle("Taximeter").setContentText(text).setAutoCancel(true).setTicker(text).setContentIntent(pendingIntent);

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
        Toast.makeText(getApplicationContext(), "Start", Toast.LENGTH_SHORT).show();
        if (!isProcessStarted) {
            startForeground(notifyId, createNotification("Работает"));
            isProcessStarted = true;
        } else {
            updateNotification("Работает");
        }
    }


    public void pauseCounter() {
        Toast.makeText(getApplicationContext(), "Pause", Toast.LENGTH_SHORT).show();
        updateNotification("На паузе");
    }

    public void stopCounter() {
        Toast.makeText(getApplicationContext(), "Stop", Toast.LENGTH_SHORT).show();
        isProcessStarted = false;
        stopForeground(true);
    }

}
