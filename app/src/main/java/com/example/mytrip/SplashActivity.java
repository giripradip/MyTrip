package com.example.mytrip;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme); // setup Theme for launcher
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, DefaultSearchActivity.class);
        startActivity(intent);
        finish();
    }
}