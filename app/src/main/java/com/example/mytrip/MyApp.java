package com.example.mytrip;

import android.app.Application;

public class MyApp extends Application {

    private static MyApp mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
    }

    public static MyApp getContext() {
        return mContext;
    }
}
