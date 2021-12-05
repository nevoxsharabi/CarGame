package com.example.cargame;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;

public class SplashScreen extends AppCompatActivity {
    private ImageView imageViewSplashIcon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        imageViewSplashIcon = findViewById(R.id.imageViewSplashIcon);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        imageViewSplashIcon.setX(height / 2);
        imageViewSplashIcon.setScaleX(1.0f);
        imageViewSplashIcon.setScaleY(1.0f);
        imageViewSplashIcon.animate().scaleX(1.0f).scaleY(1.0f).translationX(-height / 2).setDuration(3000).setInterpolator(new AccelerateInterpolator()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }
}