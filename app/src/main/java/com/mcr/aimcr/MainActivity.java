package com.mcr.aimcr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = () -> MainActivity.this.startActivity(new Intent(MainActivity.this,HomePage.class));

        handler.postDelayed(runnable,3000);
    }
}