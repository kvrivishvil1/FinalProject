package com.example.finalproject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.room.TypeConverter;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Helper {

    @TypeConverter
    public static Date toDate(Long dateLong){
        return dateLong == null ? null: new Date(dateLong);
    }

    @TypeConverter
    public static Long fromDate(Date date){
        return date == null ? null : date.getTime();
    }

    public static String formatDate(Date date, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    public static void closeKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null)
            view = new View(activity);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void turnWifiOn(Activity activity, final PermissionListener listener) {
        final WifiManager wifiManager = (WifiManager) activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        if (wifiManager.isWifiEnabled()) {
            listener.onSuccess();
            return;
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder
                .setCancelable(false)
                .setMessage("აპლიკაციის მუშაობისთვის აუცილებელია WiFi. გსურთ ჩართვა?")
                .setPositiveButton("კი",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                wifiManager.setWifiEnabled(true);
                                listener.onSuccess();
                                dialogInterface.dismiss();
                            }
                        })
                .setNegativeButton("არა",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface,int id) {
                                listener.onFailure();
                                dialogInterface.cancel();
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public static void turnGpsOn(final Activity activity, final PermissionListener listener) {
        final LocationManager manager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            listener.onSuccess();
            return;
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("აპლიკაციის მუშაობისთვის აუცილებელია GPS. გსურთ ჩართოთ?")
                .setCancelable(false)
                .setPositiveButton("კი", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        listener.onSuccess();
                        activity.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("არა", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        listener.onFailure();
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public interface PermissionListener {
        void onSuccess();
        void onFailure();
    }

}