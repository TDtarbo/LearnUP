package com.webeedesign.learnup.ui.welcome_activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;

import com.webeedesign.learnup.R;
import com.webeedesign.learnup.ui.login.LoginActivity;
import com.webeedesign.learnup.ui.navigation_drawer.NavigationDrawerActivity;
import com.webeedesign.learnup.ui.registration.SignUpActivity;

public class WelcomeActivity extends AppCompatActivity {

    private Button getStartBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity);

        getSupportActionBar().hide();

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        boolean firstStart = prefs.getBoolean("firstStart", true);
        Log.d("FirstStart", String.valueOf(firstStart));

        if (firstStart){

            ImageView imageView = findViewById(R.id.imageView);
            getStartBtn = findViewById(R.id.getStartBtn);

            imageView.setAlpha(0f);
            getStartBtn.setAlpha(0f);
            getStartBtn.setTranslationY(100f);

            // Start the image animation
            imageView.animate()
                    .alpha(1f)
                    .setDuration(1000)
                    .start();

            // Start the button animation after the image animation finishes
            getStartBtn.animate()
                    .alpha(1f)
                    .translationY(0f)
                    .setDuration(300)
                    .setStartDelay(1000)
                    .start();

            // Make the ImageView and Button visible
            imageView.setVisibility(View.VISIBLE);
            getStartBtn.setVisibility(View.VISIBLE);



            getStartBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(WelcomeActivity.this, SignUpActivity.class);
                    startActivity(intent);

                    SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("firstStart", false);
                    editor.commit();
                    finish();
                }
            });
        }else{
            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }


    }
}