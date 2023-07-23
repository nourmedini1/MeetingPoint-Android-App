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

import com.app.meetingpoint.adapters.ChooseFavoriteTopicsAdapter;
import com.app.meetingpoint.adapters.ChooseGroupsAdapter;
import com.app.meetingpoint.databinding.ActivityChooseYourFavoriteTopicsBinding;
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
import java.util.Arrays;
import java.util.List;

public class ChooseYourFavoriteTopicsActivity extends BaseActivity {
    ActivityChooseYourFavoriteTopicsBinding binding ;
    PrefrencesManager prefrencesManager ;
    ArrayList<Topic> allTopics ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChooseYourFavoriteTopicsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        prefrencesManager = new PrefrencesManager(this);
        allTopics = new ArrayList<>();
        setupToolBar(prefrencesManager);
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
                            setupTrendingTopics(allTopics);
                            setupRelatedToMostPosts(allTopics);
                            setupRelatedToPeopleInterests(allTopics,prefrencesManager);
                        }
                    }
                });
    }
    private void setupTrendingTopics(ArrayList<Topic> allTopics){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        ArrayList<Topic> trending = new ArrayList<>(allTopics);
        ChooseFavoriteTopicsAdapter chooseFavoriteTopicsAdapter = new ChooseFavoriteTopicsAdapter(getApplicationContext(), trending);
        binding.chooseFavoriteTopicsTrendingRecycler.setLayoutManager(linearLayoutManager);
        binding.chooseFavoriteTopicsTrendingRecycler.setAdapter(chooseFavoriteTopicsAdapter);
        SnapHelper snapHelper = new LinearSnapHelper() ;
        snapHelper.attachToRecyclerView(binding.chooseFavoriteTopicsTrendingRecycler);
    }
    private void setupRelatedToMostPosts(ArrayList<Topic> allTopics){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        ArrayList<Topic> relatedToUsers = new ArrayList<>(allTopics);
        ChooseFavoriteTopicsAdapter chooseFavoriteTopicsAdapter = new ChooseFavoriteTopicsAdapter(getApplicationContext(), relatedToUsers);
        binding.chooseFavoriteTopicsPostsThisMonthRecycler.setLayoutManager(linearLayoutManager);
        binding.chooseFavoriteTopicsPostsThisMonthRecycler.setAdapter(chooseFavoriteTopicsAdapter);
        SnapHelper snapHelper = new LinearSnapHelper() ;
        snapHelper.attachToRecyclerView(binding.chooseFavoriteTopicsPostsThisMonthRecycler);



    }
    private void setupRelatedToPeopleInterests(ArrayList<Topic> allTopics,PrefrencesManager prefrencesManager){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        ArrayList<Topic> relatedToPeopleInterests = new ArrayList<>();
        List<String> relatedToUserFavoriteList = Arrays.asList(
                prefrencesManager.getString(Constants.ATTRIBUTE_USERS_FAVORITE_TOPICS_KEY).split(","));
        for (Topic topic: allTopics) {
            if (relatedToUserFavoriteList.contains(topic.getTopic())){
                relatedToPeopleInterests.add(topic);
            }
        }
        ChooseFavoriteTopicsAdapter chooseFavoriteTopicsAdapter = new ChooseFavoriteTopicsAdapter(getApplicationContext(), relatedToPeopleInterests);
        binding.chooseFavoriteTopicsPeopleInterestedRecycler.setLayoutManager(linearLayoutManager);
        binding.chooseFavoriteTopicsPeopleInterestedRecycler.setAdapter(chooseFavoriteTopicsAdapter);
        SnapHelper snapHelper = new LinearSnapHelper() ;
        snapHelper.attachToRecyclerView(binding.chooseFavoriteTopicsPeopleInterestedRecycler);
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
