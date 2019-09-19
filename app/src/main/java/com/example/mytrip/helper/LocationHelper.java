package com.example.mytrip.helper;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.provider.Settings;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.example.mytrip.R;

import static com.example.mytrip.constant.HelperConstant.LOCATION_REQUEST_CODE;

public class LocationHelper {


    /**
     * Return the current state of the permissions needed.
     */
    public static boolean checkPermissions(Context context) {

        int permissionState = ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionState1 = ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED && permissionState1 == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestLocationPermission(Context context) {

        Activity activity = (Activity) context;
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
    }

    public static boolean isLocationServiceEnabled(Context context) {
        LocationManager locationManager = null;
        boolean gps_enabled = false, network_enabled = false;

        if (locationManager == null)
            locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        return gps_enabled || network_enabled;

    }

    public static void enableLocation(Context context) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.app_name))
                .setMessage("Please enable location to use location services")
                .setPositiveButton("Ok", (dialog, id) -> {
                    dialog.dismiss();
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    context.startActivity(myIntent);
                })
                .setNegativeButton(R.string.cancel, (dialog, id) -> {
                    // User cancelled the dialog
                    dialog.dismiss();
                });
        // Create the AlertDialog object and return it
        AlertDialog d = builder.create();
        d.setOnShowListener(arg0 -> {
            d.getButton(android.app.AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getColor(R.color.colorPrimary));
            d.getButton(android.app.AlertDialog.BUTTON_POSITIVE).setTextColor(context.getColor(R.color.danger));
        });
        d.setCanceledOnTouchOutside(false);
        d.show();
    }

    public static boolean isLocationReady(Context context) {


        if (!LocationHelper.checkPermissions(context)) {
            LocationHelper.requestLocationPermission(context);
            return false;
        }
        if (!LocationHelper.isLocationServiceEnabled(context)) {
            LocationHelper.enableLocation(context);
            return false;
        }

        return true;
    }
}
