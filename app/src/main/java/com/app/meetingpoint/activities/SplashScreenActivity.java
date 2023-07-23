package com.app.meetingpoint.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import com.app.meetingpoint.databinding.ActivitySplashScreenBinding;
import com.app.meetingpoint.helpers.Constants;
import com.app.meetingpoint.helpers.PrefrencesManager;
import com.app.meetingpoint.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;


public class SplashScreenActivity extends AppCompatActivity {
    ActivitySplashScreenBinding binding ;
    PrefrencesManager prefrencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        prefrencesManager = new PrefrencesManager(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!(prefrencesManager.getBoolean(Constants.SIGNED_IN_KEY))){
                    Intent i=new Intent(SplashScreenActivity.this, FirstScreenActivity.class);
                    ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            SplashScreenActivity.this,binding.appTitle,
                            Objects.requireNonNull(ViewCompat.getTransitionName(binding.appTitle)));
                    startActivity(i,optionsCompat.toBundle());
                    finish();
                }
                else{
                    Intent intent = new Intent(SplashScreenActivity.this,HomeScreenActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        },3000 );


    }

    @Override
    protected void onResume() {
        super.onResume();
        prefrencesManager = new PrefrencesManager(this);
        System.out.println(prefrencesManager.getBoolean(Constants.SIGNED_IN_KEY));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!(prefrencesManager.getBoolean(Constants.SIGNED_IN_KEY))){
                    Intent i=new Intent(SplashScreenActivity.this, FirstScreenActivity.class);
                    ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            SplashScreenActivity.this,binding.appTitle,
                            Objects.requireNonNull(ViewCompat.getTransitionName(binding.appTitle)));
                    startActivity(i,optionsCompat.toBundle());
                    finish();
                }
                else{
                    Intent intent = new Intent(SplashScreenActivity.this,HomeScreenActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        },3000 );



    }

    @Override
    protected void onRestart() {
        super.onRestart();
        prefrencesManager = new PrefrencesManager(this);
        System.out.println(prefrencesManager.getBoolean(Constants.SIGNED_IN_KEY));
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!(prefrencesManager.getBoolean(Constants.SIGNED_IN_KEY))){
                    Intent i=new Intent(SplashScreenActivity.this, FirstScreenActivity.class);
                    ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                            SplashScreenActivity.this,binding.appTitle,
                            Objects.requireNonNull(ViewCompat.getTransitionName(binding.appTitle)));
                    startActivity(i,optionsCompat.toBundle());
                    finish();
                }
                else{
                    Intent intent = new Intent(SplashScreenActivity.this,HomeScreenActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        },3000 );

    }
}