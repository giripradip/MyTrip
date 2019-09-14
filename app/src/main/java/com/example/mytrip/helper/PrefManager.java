package com.example.mytrip.helper;

import android.content.Context;
import android.content.SharedPreferences;


public class PrefManager {

    private static final String PREF_NAME = "android_myTrip_app";
    private static final String IS_SYNC_REQUIRED = "is_sync_required";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    // shared pref mode
    private int PRIVATE_MODE = 0;

    public PrefManager(Context context) {

        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setIsSyncRequired(boolean status) {

        editor.putBoolean(IS_SYNC_REQUIRED, status);
        editor.commit();
    }

    public boolean isSyncRequired() {
        return pref.getBoolean(IS_SYNC_REQUIRED, true);
    }

    public void cleanIsSyncRequired() {

        pref.edit().remove(IS_SYNC_REQUIRED).commit();
    }


    public void clearAll() {

        pref.edit().clear().commit();
    }
}
