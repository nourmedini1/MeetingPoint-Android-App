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
import com.app.meetingpoint.databinding.ActivityChooseYourGroupsAcitivityBinding;
import com.app.meetingpoint.helpers.Constants;
import com.app.meetingpoint.helpers.PrefrencesManager;
import com.app.meetingpoint.models.Group;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.prefs.PreferenceChangeEvent;

public class ChooseYourGroupsAcitivity extends BaseActivity {
    private ActivityChooseYourGroupsAcitivityBinding binding ;
    private PrefrencesManager prefrencesManager ;
    private ArrayList<Group> allGroups;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChooseYourGroupsAcitivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        prefrencesManager = new PrefrencesManager(getApplicationContext());
        allGroups = new ArrayList<>();
        setupToolBar(prefrencesManager);
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
                            setupTrendingGroups(allGroups);
                            setupRelatedToFavoriteTopicsGroups(allGroups,prefrencesManager);
                            setupRelatedToUsersGroups(allGroups);
                        }
                    }
                });
        binding.createGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),CreateGroup.class);
                startActivity(intent);
                finish();
            }
        });


    }
    private void setupTrendingGroups(ArrayList<Group> allGroups){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        ArrayList<Group> trending = new ArrayList<>(allGroups);
        ChooseGroupsAdapter chooseGroupsAdapter = new ChooseGroupsAdapter(getApplicationContext(), trending);
        binding.chooseGroupsTrendingRecycler.setLayoutManager(linearLayoutManager);
        binding.chooseGroupsTrendingRecycler.setAdapter(chooseGroupsAdapter);
        SnapHelper snapHelper = new LinearSnapHelper() ;
        snapHelper.attachToRecyclerView(binding.chooseGroupsTrendingRecycler);
    }
    private void setupRelatedToUsersGroups(ArrayList<Group> allGroups){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        ArrayList<Group> relatedToUsers = new ArrayList<>(allGroups);
        ChooseGroupsAdapter chooseGroupsAdapter = new ChooseGroupsAdapter(getApplicationContext(), relatedToUsers);
        binding.chooseGroupsPeopleInYourGroupsRecycler.setLayoutManager(linearLayoutManager);
        binding.chooseGroupsPeopleInYourGroupsRecycler.setAdapter(chooseGroupsAdapter);
        SnapHelper snapHelper = new LinearSnapHelper() ;
        snapHelper.attachToRecyclerView(binding.chooseGroupsPeopleInYourGroupsRecycler);



    }
    private void setupRelatedToFavoriteTopicsGroups(ArrayList<Group> allGroups,PrefrencesManager prefrencesManager){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        ArrayList<Group> relatedToTopics = new ArrayList<>();
        ArrayList<String> relatedToUserFavoriteList = new ArrayList<>
                (Arrays.asList(prefrencesManager.getString(Constants.ATTRIBUTE_USERS_FAVORITE_TOPICS_KEY).split(",")));
        for (Group group: allGroups) {
            if (relatedToUserFavoriteList.contains(group.getTopic())){
                relatedToTopics.add(group);
            }
        }
        ChooseGroupsAdapter chooseGroupsAdapter = new ChooseGroupsAdapter(getApplicationContext(), relatedToTopics);
        binding.chooseGroupsFavoriteTopicsRecycler.setLayoutManager(linearLayoutManager);
        binding.chooseGroupsFavoriteTopicsRecycler.setAdapter(chooseGroupsAdapter);
        SnapHelper snapHelper = new LinearSnapHelper() ;
        snapHelper.attachToRecyclerView(binding.chooseGroupsFavoriteTopicsRecycler);
    }
    private void setupToolBar(PrefrencesManager prefrencesManager){

        byte[] bytes  = Base64.decode(prefrencesManager.getString(Constants.ATTRIBUTE_USERS_IMAGE_KEY), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.chooseGroupsToolbar.toolbarImg.setImageBitmap(decodedByte);
        binding.chooseGroupsToolbar.toolbarHome.setTitle("Choose your groups");
    }

    @Override
    public void onBackPressed() {
        Intent intentToUserProfile = new Intent(getApplicationContext(),UserProfileActivity.class);
        startActivity(intentToUserProfile);
        finish();
        super.onBackPressed();
    }
}