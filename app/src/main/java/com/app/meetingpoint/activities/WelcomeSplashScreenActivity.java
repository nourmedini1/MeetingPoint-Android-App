package com.app.meetingpoint.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.app.meetingpoint.R;
import com.app.meetingpoint.helpers.Constants;
import com.app.meetingpoint.helpers.PrefrencesManager;

import java.util.Objects;

public class WelcomeSplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_splash_screen);
        PrefrencesManager prefrencesManager = new PrefrencesManager(this);
        prefrencesManager.putBoolean(Constants.SIGNED_IN_KEY,true);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                    Intent i=new Intent(WelcomeSplashScreenActivity.this, HomeScreenActivity.class);
                    startActivity(i);
                    finish();
                }
        },1000 );
    }
}