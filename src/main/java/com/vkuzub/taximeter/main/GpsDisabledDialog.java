package com.vkuzub.taximeter.main;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;

/**
 * Created by Vyacheslav on 14.08.2014.
 */
public class GpsDisabledDialog extends DialogFragment implements DialogInterface.OnClickListener {


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());

        adb.setTitle("Невозможно определить местоположение");

        adb.setMessage("Включите определение местоположения по спутникам GPS");

        adb.setPositiveButton("Настройки", this);
        adb.setNegativeButton("Выход", this);
        setCancelable(false);
        return adb.create();
    }


    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        switch (i) {
            case Dialog.BUTTON_POSITIVE:
                Intent gpsSettings = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(gpsSettings);
                dismiss();
                break;
            case Dialog.BUTTON_NEGATIVE:
                getActivity().finish();
                break;
        }
    }

}
