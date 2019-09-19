package com.example.mytrip.helper;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.provider.Settings;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.mytrip.R;
import com.example.mytrip.dialog.CustomAlertDialogFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

import static com.example.mytrip.constant.HelperConstant.LOCATION_REQUEST_CODE;


public class Helper {

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";
    private static final String INPUT_DATE_TIME = "EEE MMM dd HH:mm:ss z yyyy";
    private static final String DATE_FORMAT = "dd.MM.yyyy";
    private static final String TIME_FORMAT = "HH:mm";
    public static final String DIALOG_TAG_CONFIRM = "alertTagConfirm";

    //Helper function to hide keyboard
    public static void hideSoftKeyboard(Activity activity) {
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    //Helper function to check text obtained from edit text is empty or not
    public static boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() <= 0;
    }

    /**
     * --------Function to get only Date from the given timestamp ----
     **/
    public static String getDate(int timestamp) {

        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
        formatter.setTimeZone(TimeZone.getDefault());
        //convert seconds to milliseconds
        Date date = new Date(timestamp * 1000L);
        String dateString = formatter.format(date);
        return dateString;
    }

    /**
     * --------Function to get only Time from the given timestamp ----
     **/
    public static String getTime(int timestamp) {

        SimpleDateFormat formatter = new SimpleDateFormat(TIME_FORMAT, Locale.ENGLISH);
        formatter.setTimeZone(TimeZone.getDefault());
        //convert seconds to milliseconds
        Date date = new Date(timestamp * 1000L);
        String timeString = formatter.format(date);
        return timeString;
    }

    /**
     * --------Function to get DATETIME from the given timestamp ----
     **/
    public static String getDateTimeFromTimeStamp(int timestamp) {

        SimpleDateFormat formatter = new SimpleDateFormat(DATE_TIME_FORMAT, Locale.ENGLISH);
        formatter.setTimeZone(TimeZone.getDefault());
        //convert seconds to milliseconds
        Date date = new Date(timestamp * 1000L);
        String java_date = formatter.format(date);
        return java_date;
    }

    /**
     * --------Function to get timestamp from the given datetime ----
     **/
    public static int getTimeStampFromDateTime(String dateTime) {

        SimpleDateFormat formatter = new SimpleDateFormat(DATE_TIME_FORMAT, Locale.ENGLISH);
        formatter.setTimeZone(TimeZone.getDefault());

        Date date = null;
        try {
            date = formatter.parse(dateTime);
            return (int) (date.getTime() / 1000);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;
    }

    /**
     * --------Function to change the date format ----
     **/
    public static String changeDateFormat(Date d) {

        SimpleDateFormat inputFormat = new SimpleDateFormat(INPUT_DATE_TIME, Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat(DATE_TIME_FORMAT, Locale.getDefault());
        try {
            Date date = inputFormat.parse(d.toString());
            String str = outputFormat.format(date);
            return str;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * --------Function to show confirmation alert dialog, needs to call from activity ----
     **/
    public static void showConfirmAlertDialog(FragmentActivity activity, String message, String confirmBtnText) {
        CustomAlertDialogFragment alertDialog = CustomAlertDialogFragment.newInstance(message, confirmBtnText);
        alertDialog.show(activity.getSupportFragmentManager(), DIALOG_TAG_CONFIRM);
    }

    /**
     * --------Function to show confirmation alert dialog, needs to call from fragment ----
     **/
    public static void showConfirmAlertDialog(Fragment fragment, String message, String confirmBtnText) {
        CustomAlertDialogFragment alertDialog = CustomAlertDialogFragment.newInstance(message, confirmBtnText);
        alertDialog.setTargetFragment(fragment, 0);
        alertDialog.show(Objects.requireNonNull(fragment.getActivity()).getSupportFragmentManager(), DIALOG_TAG_CONFIRM);
    }
}
