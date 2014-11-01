package com.vkuzub.taximeter.utils;

import android.content.Context;
import android.location.LocationManager;

/**
 * Created by Vyacheslav on 28.10.2014.
 */
public class TaximeterUtils {

    public static final String LOG_TAG = "Taximeter";

    public static boolean isGPSLocationAvailable(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

}
