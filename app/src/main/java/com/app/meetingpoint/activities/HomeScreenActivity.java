package com.app.meetingpoint.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.app.meetingpoint.R;
import com.app.meetingpoint.adapters.GroupPostsAdapter;
import com.app.meetingpoint.adapters.GroupsShowerAdapter;
import com.app.meetingpoint.adapters.DrawerGroupsAdapter;
import com.app.meetingpoint.databinding.ActivityHomeScreenBinding;
import com.app.meetingpoint.helpers.Constants;
import com.app.meetingpoint.helpers.PrefrencesManager;
import com.app.meetingpoint.interfaces.RecyclerViewInterface;

import com.app.meetingpoint.models.Group;
import com.app.meetingpoint.models.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class HomeScreenActivity extends BaseActivity implements RecyclerViewInterface {
    private ActionBarDrawerToggle toggle;
    private Toolbar toolbar;
    private ActivityHomeScreenBinding binding;
    private PrefrencesManager prefrencesManager;
    private int i = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        prefrencesManager = new PrefrencesManager(getApplicationContext());
        setupToolBar();
        getToken(prefrencesManager);
        setupPostsShower(prefrencesManager);
        setupDrawer(prefrencesManager);
        binding.toolbar.toolbarImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToUserProfile = new Intent(getApplicationContext(), UserProfileActivity.class);
                startActivity(intentToUserProfile);
                finish();
            }
        });

    }
    private void getToken(PrefrencesManager prefrencesManager){
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if(task.isSuccessful() && task.getResult() != null){
                            updateToken(prefrencesManager,task.getResult());
                        }
                    }
                });
    }
    private void updateToken(PrefrencesManager prefrencesManager,String token){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.COLLECTION_USERS_KEY)
                .document(prefrencesManager.getString(Constants.ATTRIBUTE_USERS_ID_KEY))
                .update(Constants.ATTRIBUTE_USERS_FCM_TOKEN_KEY,token);
        prefrencesManager.putString(Constants.ATTRIBUTE_USERS_FCM_TOKEN_KEY,token);
    }


    private void setupDrawer(PrefrencesManager prefrencesManager) {
        ArrayList<Group> groups = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        CollectionReference groupCollection = database.collection(Constants.COLLECTION_GROUP_KEY);
        List<String> myGroups =
                Arrays.asList(prefrencesManager.getString(Constants.ATTRIBUTE_USERS_GROUPS_KEY).split(","));
        int end = myGroups.size() - 1;
        for (String id : myGroups) {
            groupCollection.document(id).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                Group group = task.getResult().toObject(Group.class);
                                assert group != null;
                                group.setId(task.getResult().getId());
                                groups.add(group);
                                if (i == end) {
                                    binding.drawerGroupRecycler.setLayoutManager(layoutManager);
                                    binding.drawerGroupRecycler.setHasFixedSize(true);
                                    DrawerGroupsAdapter drawerGroupsAdapter = new
                                            DrawerGroupsAdapter(groups, HomeScreenActivity.this);
                                    binding.drawerGroupRecycler.setAdapter(drawerGroupsAdapter);
                                    toggle = new ActionBarDrawerToggle(HomeScreenActivity.this, binding.drawerLayout, toolbar,
                                            R.string.navigation_drawer_open, R.string.navigation_drawer_close);
                                    binding.drawerLayout.addDrawerListener(toggle);
                                } else {
                                    i++;
                                }
                            }


                        }
                    });
        }


    }

    private void setupToolBar() {

        byte[] bytes = Base64.decode(prefrencesManager.getString(Constants.ATTRIBUTE_USERS_IMAGE_KEY), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.toolbar.toolbarImg.setImageBitmap(decodedByte);
        toolbar = binding.toolbar.toolbarHome;
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);
    }

    private void setupPostsShower(PrefrencesManager prefrencesManager) {
        ArrayList<Post> posts = new ArrayList<>();
        List<String> groups = Arrays.asList(prefrencesManager.getString(Constants.ATTRIBUTE_USERS_GROUPS_KEY).split(","));
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.COLLECTION_POST_KEY)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                Post post = documentSnapshot.toObject(Post.class);
                                post.setPostId(documentSnapshot.getId());
                                if (groups.contains(post.getGroupId())) {
                                    posts.add(post);
                                }
                                if (post.getShareTo() != null && post.getShareTo().size() > 0) {
                                    for (String element : post.getShareTo()) {
                                        String[] strings = element.split(Constants.SEPERATOR);
                                        if (groups.contains(strings[0])) {
                                            post.setShareGroup(strings[0]);
                                            post.setSharerName(strings[1]);
                                            post.setSharerId(strings[2]);
                                            post.setShareGroupName(strings[3]);
                                            posts.add(post);
                                        }
                                    }
                                }

                            }
                            GroupPostsAdapter groupPostsAdapter = new
                                    GroupPostsAdapter(posts, getApplicationContext(), HomeScreenActivity.this);

                            LinearLayoutManager linearLayoutManager = new
                                    LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                            binding.posts.setLayoutManager(linearLayoutManager);
                            binding.posts.setAdapter(groupPostsAdapter);

                        }
                    }
                });

    }

    @Override
    public void onItemClick(int position, String item, String extras) {
        switch (item) {
            case "reacts":
                Intent intentToShowReacts = new Intent(getApplicationContext(), ShowReacts.class);
                intentToShowReacts.putExtra("postId", extras);
                startActivity(intentToShowReacts);
                break;
            case "share":
                Intent intentToSharePostActivity = new Intent(getApplicationContext(), SharePost.class);
                intentToSharePostActivity.putExtra("postId", extras);
                startActivity(intentToSharePostActivity);
                break;
            case "comments":
                Intent intentToShowComments = new Intent(getApplicationContext(), ShowComments.class);
                intentToShowComments.putExtra("postId", extras);
                startActivity(intentToShowComments);
                break;
            case "group":

                Intent intentToGroup = new Intent(getApplicationContext(), GroupActivity.class);
                FirebaseFirestore database = FirebaseFirestore.getInstance();
                database.collection(Constants.COLLECTION_GROUP_KEY)
                        .document(extras)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    Group group = task.getResult().toObject(Group.class);
                                    assert group != null;
                                    group.setId(extras);
                                    intentToGroup.putExtra("group", group);
                                    startActivity(intentToGroup);
                                }
                            }
                        });


                break;
            case "userProfile":
                PrefrencesManager prefrencesManager = new PrefrencesManager(getApplicationContext());
                if (extras.equals(prefrencesManager.getString(Constants.ATTRIBUTE_USERS_ID_KEY))) {
                    Intent intent = new Intent(getApplicationContext(), UserProfileActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getApplicationContext(), CheckProfile.class);
                    intent.putExtra("userId", extras);
                    startActivity(intent);
                }
                break;
        }

    }
}
