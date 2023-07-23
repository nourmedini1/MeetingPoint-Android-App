package com.app.meetingpoint.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.SnapHelper;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;

import com.app.meetingpoint.R;
import com.app.meetingpoint.adapters.ChooseGroupsAdapter;
import com.app.meetingpoint.adapters.NewUserFavoriteTopicsAdapter;
import com.app.meetingpoint.adapters.NewUserPickGroupsAdapter;
import com.app.meetingpoint.adapters.UserProfileMyFavoriteTopicsAdapter;
import com.app.meetingpoint.databinding.ActivityNewUserGroupsAndTopicsBinding;
import com.app.meetingpoint.helpers.Constants;
import com.app.meetingpoint.helpers.PrefrencesManager;
import com.app.meetingpoint.models.Group;
import com.app.meetingpoint.models.Topic;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class NewUserGroupsAndTopicsActivity extends AppCompatActivity {
    private ActivityNewUserGroupsAndTopicsBinding binding;
    private PrefrencesManager prefrencesManager;
    private ArrayList<Group> allGroups;
    private ArrayList<Topic> allTopics ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewUserGroupsAndTopicsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        prefrencesManager = new PrefrencesManager(this);
        setupToolBar();
        setupGroups();
        setupTopics();
        binding.newUserGroupsTopicsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToWelcomeSplashScreen = new Intent(NewUserGroupsAndTopicsActivity.this,WelcomeSplashScreenActivity.class);
                startActivity(intentToWelcomeSplashScreen);
                finish();
            }
        });

    }


    private void setupGroups() {
        allGroups = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.COLLECTION_GROUP_KEY)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                Group group = documentSnapshot.toObject(Group.class);
                                group.setId(documentSnapshot.getId());
                                allGroups.add(group);
                            }
                            NewUserPickGroupsAdapter newUserPickGroupsAdapter = new NewUserPickGroupsAdapter(getApplicationContext(),allGroups);
                            binding.newUserGroupsTopicsGroupsRecycler.setLayoutManager(linearLayoutManager);
                            binding.newUserGroupsTopicsGroupsRecycler.setAdapter(newUserPickGroupsAdapter);
                            SnapHelper snapHelper = new LinearSnapHelper();
                            snapHelper.attachToRecyclerView(binding.newUserGroupsTopicsGroupsRecycler);
                        }
                    }
                });
    }
    private void setupTopics() {
        allTopics = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.COLLECTION_TOPICS_KEY)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                Topic topic = documentSnapshot.toObject(Topic.class);
                                topic.setTopicId(documentSnapshot.getId());
                                allTopics.add(topic);
                            }
                            NewUserFavoriteTopicsAdapter userProfileMyFavoriteTopicsAdapter =
                                    new NewUserFavoriteTopicsAdapter(getApplicationContext(),allTopics);
                            binding.newUserGroupsTopicsTopicsRecycler.setLayoutManager(linearLayoutManager);
                            binding.newUserGroupsTopicsTopicsRecycler.setAdapter(userProfileMyFavoriteTopicsAdapter);
                            SnapHelper snapHelper = new LinearSnapHelper();
                            snapHelper.attachToRecyclerView(binding.newUserGroupsTopicsTopicsRecycler);
                        }
                    }
                });
    }
    private void setupToolBar(){

        byte[] bytes  = Base64.decode(prefrencesManager.getString(Constants.ATTRIBUTE_USERS_IMAGE_KEY), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.newUserGroupsTopicsToolbar.toolbarImg.setImageBitmap(decodedByte);

        binding.newUserGroupsTopicsToolbar.toolbarHome.setTitle("Choose your groups and topics");
        setSupportActionBar(binding.newUserGroupsTopicsToolbar.toolbarHome);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intentToModifyUserInfo = new Intent(NewUserGroupsAndTopicsActivity.this,ModifyUserInfoActivity.class);
        startActivity(intentToModifyUserInfo);
        finish();
    }
}