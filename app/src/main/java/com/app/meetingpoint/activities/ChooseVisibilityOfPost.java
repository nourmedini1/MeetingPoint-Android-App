package com.app.meetingpoint.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.app.meetingpoint.R;
import com.app.meetingpoint.databinding.ActivityChooseVisibityOfPostBinding;
import com.app.meetingpoint.helpers.PrefrencesManager;
import com.app.meetingpoint.models.Group;

public class ChooseVisibilityOfPost extends BaseActivity {
    ActivityChooseVisibityOfPostBinding binding ;
    PrefrencesManager prefrencesManager ;
    int checkedRadioButton = -1 ;
    Group group ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChooseVisibityOfPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.chooseVisibilityLottie.setAnimation(R.raw.community);
        group = (Group) getIntent().getSerializableExtra("group");
        binding.chooseVisibilityBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        binding.chooseVisibilityNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkedRadioButton = binding.chooseVisibilityRadioGroup.getCheckedRadioButtonId();
                if(checkedRadioButton == -1){
                    Toast.makeText(ChooseVisibilityOfPost.this, "please choose a visibility", Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent intentToCreatePost = new Intent(getApplicationContext(),CreatePost.class);
                    intentToCreatePost.putExtra("group",group);
                    if(checkedRadioButton == R.id.choose_visibility_radio_button_private){
                        intentToCreatePost.putExtra("visibility","private");
                    }
                    else {
                        intentToCreatePost.putExtra("visibility","public");
                    }
                    startActivity(intentToCreatePost);
                    finish();
                }

            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intentToGroup = new Intent(getApplicationContext(),GroupActivity.class);
        intentToGroup.putExtra("group",group);
        startActivity(intentToGroup);
        finish();
    }
}