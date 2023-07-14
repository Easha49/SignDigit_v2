package com.example.youtubeactivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;

public class MainActivity extends AppCompatActivity {
    LinearLayout  map , model , video , rating  , post ;
    private LottieAnimationView animationView;
    ImageView UserProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature (Window.FEATURE_NO_TITLE);
        this.getWindow ().setFlags (WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar ().hide ();
        setContentView(R.layout.activity_main);
        UserProfile  = findViewById(R.id.userProfile) ;
        map = findViewById(R.id.gotoMap) ;
        model = findViewById(R.id.machineLearning) ;
        video = findViewById(R.id.embeddedVideo) ;
        rating = findViewById(R.id.Rating) ;
        post = findViewById(R.id.post) ;
        animationView = findViewById(R.id.animation_view);

        // Start the animation
        animationView.playAnimation();

        // Login Page
        UserProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,Profile.class));
            }
        });


        // Machine Learning Page

        model.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,ModelActivity.class));
            }
        });

        // Google MAP
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,MapsActivity.class));
            }
        });

        // Embedded Video of Youtube
        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, YoutubeActivity.class));
            }
        });


        rating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,Rating.class));
            }
        });


        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,Post_Page.class));
            }
        });

    }
}