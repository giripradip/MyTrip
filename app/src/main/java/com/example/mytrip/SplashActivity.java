package com.example.mytrip;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mytrip.helper.KeyHelper;

import java.nio.charset.Charset;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme); // setup Theme for launcher
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, AddMyTripActivity.class);
        startActivity(intent);
        finish();
    }
}
