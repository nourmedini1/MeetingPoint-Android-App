package com.app.meetingpoint.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.app.meetingpoint.R;
import com.app.meetingpoint.databinding.ActivityFirstScreenBinding;

public class FirstScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActivityFirstScreenBinding binding ;
        super.onCreate(savedInstanceState);
        binding = ActivityFirstScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.signupRedirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToSignUpActivity = new Intent(getApplicationContext(),SignUpActivity.class);
                startActivity(intentToSignUpActivity);
                finish();
            }
        });
        binding.loginButton.setOnClickListener(v -> {
            Intent intentToLoginActivity = new Intent(getApplicationContext(),SignInActivity.class);
            startActivity(intentToLoginActivity);
            finish();
        });


    }
    public void onBackPressed() {
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setTitle("Leaving The App :( ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }

                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        builder.create();
        builder.show();

    }

}