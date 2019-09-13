package com.example.mytrip.helper;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.mytrip.dialog.CustomAlertDialogFragment;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;


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

    public static boolean isEmailValid(String email) {
        return email.contains("@");
    }

    public static boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    public static String getDate(int timestamp) {

        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
        formatter.setTimeZone(TimeZone.getDefault());
        //convert seconds to milliseconds
        Date date = new Date(timestamp * 1000L);
        String dateString = formatter.format(date);
        return dateString;
    }

    public static String getTime(int timestamp) {

        SimpleDateFormat formatter = new SimpleDateFormat(TIME_FORMAT, Locale.ENGLISH);
        formatter.setTimeZone(TimeZone.getDefault());
        //convert seconds to milliseconds
        Date date = new Date(timestamp * 1000L);
        String timeString = formatter.format(date);
        return timeString;
    }

    public static String getDateTimeFromTimeStamp(int timestamp) {

        SimpleDateFormat formatter = new SimpleDateFormat(DATE_TIME_FORMAT, Locale.ENGLISH);
        formatter.setTimeZone(TimeZone.getDefault());
        //convert seconds to milliseconds
        Date date = new Date(timestamp * 1000L);
        String java_date = formatter.format(date);
        return java_date;
    }

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

    public static void showConfirmAlertDialog(FragmentActivity activity, String message, String confirmBtnText) {
        CustomAlertDialogFragment alertDialog = CustomAlertDialogFragment.newInstance(message, confirmBtnText);
        alertDialog.show(activity.getSupportFragmentManager(), DIALOG_TAG_CONFIRM);
    }

    public static void showConfirmAlertDialog(Fragment fragment, String message, String confirmBtnText) {
        CustomAlertDialogFragment alertDialog = CustomAlertDialogFragment.newInstance(message, confirmBtnText);
        alertDialog.setTargetFragment(fragment, 0);
        alertDialog.show(Objects.requireNonNull(fragment.getActivity()).getSupportFragmentManager(), DIALOG_TAG_CONFIRM);
    }
}
