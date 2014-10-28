package com.vkuzub.taximeter.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Vyacheslav on 28.10.2014.
 */
public class CounterService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
