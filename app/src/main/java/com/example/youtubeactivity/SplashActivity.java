package com.example.youtubeactivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        requestWindowFeature (Window.FEATURE_NO_TITLE);
        this.getWindow ().setFlags (WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar ().hide ();
        setContentView (R.layout.activity_splash);

        new Handler ().postDelayed (new Runnable () {
            @Override
            public void run() {
                Intent iHome=new Intent (SplashActivity.this,Login.class);
                startActivity (iHome);
                finish ();
            }
        },1000);


    }
}