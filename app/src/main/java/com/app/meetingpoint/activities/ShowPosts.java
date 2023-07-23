package com.app.meetingpoint.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.app.meetingpoint.R;
import com.app.meetingpoint.adapters.GroupPostsAdapter;
import com.app.meetingpoint.adapters.SharePostAdapter;
import com.app.meetingpoint.adapters.UserProfileMyGroupsAdapter;
import com.app.meetingpoint.databinding.ActivitySharePostBinding;
import com.app.meetingpoint.databinding.ActivityShowPostsBinding;
import com.app.meetingpoint.helpers.Constants;
import com.app.meetingpoint.helpers.PrefrencesManager;
import com.app.meetingpoint.interfaces.RecyclerViewInterface;
import com.app.meetingpoint.models.Group;
import com.app.meetingpoint.models.Post;
import com.app.meetingpoint.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ShowPosts extends BaseActivity implements RecyclerViewInterface {
    ActivityShowPostsBinding binding;
    PrefrencesManager prefrencesManager;
    Toolbar toolbar ;
    Group group ;
    int i = 0 ;
    String type,userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowPostsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prefrencesManager = new PrefrencesManager(this);
        GroupPostsAdapter adapter = new GroupPostsAdapter(new ArrayList<Post>(),getApplicationContext(),this);
        binding.showPostsRecycler.setAdapter(adapter);
        String text = getIntent().getStringExtra("toolbarTitle");
        type = getIntent().getStringExtra("type");
        if(type == null){
            type = "";
        }
        group = (Group) getIntent().getSerializableExtra("group");
        userId = getIntent().getStringExtra("userId");
        setupToolBar(prefrencesManager,text);
        binding.toolbar.toolbarImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),UserProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });
        getPosts(type,prefrencesManager);
    }
    private void setupToolBar(PrefrencesManager prefrencesManager,String text) {

        byte[] bytes = Base64.decode(prefrencesManager.getString(Constants.ATTRIBUTE_USERS_IMAGE_KEY), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.toolbar.toolbarImg.setImageBitmap(decodedByte);
        toolbar = binding.toolbar.toolbarHome;
        toolbar.setTitle(text);
        setSupportActionBar(toolbar);
    }
    private void getPosts(String type,PrefrencesManager prefrencesManager ){
        ArrayList<Post> posts = new ArrayList<>();
        if(type.equals("group")){
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            database.collection(Constants.COLLECTION_POST_KEY)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful() && task.getResult().size() >0) {
                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                    Post post = documentSnapshot.toObject(Post.class);
                                    post.setPostId(documentSnapshot.getId());
                                    if(post.getGroupId().equals(group.getId())) {
                                        posts.add(post);
                                    }
                                    if(post.getShareTo() != null  && post.getShareTo().size()>0){
                                        for (String element: post.getShareTo()) {
                                            String[] strings = element.split(Constants.SEPERATOR);
                                            if(strings[0].equals(group.getId())){
                                                post.setShareGroup(strings[0]);
                                                post.setSharerName(strings[1]);
                                                post.setSharerId(strings[2]);
                                                post.setShareGroupName(strings[3]);
                                                posts.add(post);
                                            }
                                        }
                                    }

                                }
                               setupPosts(posts);

                            }
                        }});

        }
        else if(type.equals("userInGroup")){
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            database.collection(Constants.COLLECTION_POST_KEY)
                    .whereEqualTo(Constants.ATTRIBUTE_POST_POSTER_ID_KEY,prefrencesManager.getString(Constants.ATTRIBUTE_USERS_ID_KEY))
                    .whereEqualTo(Constants.ATTRIBUTE_POST_GROUP_ID_KEY,group.getId())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful() && task.getResult().size() != 0){
                                for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                    Post post = documentSnapshot.toObject(Post.class);
                                    post.setPostId(documentSnapshot.getId());
                                    posts.add(post);
                                }
                                setupPosts(posts);
                            }

                        }
                    });

        }
        else if(type.equals("checkProfile")){
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            database.collection(Constants.COLLECTION_POST_KEY)
                    .whereEqualTo(Constants.ATTRIBUTE_POST_POSTER_ID_KEY,userId)
                    .whereEqualTo(Constants.ATTRIBUTE_POST_VISIBILITY_KEY,"public")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful() && task.getResult().size() != 0){
                                for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                    Post post = documentSnapshot.toObject(Post.class);
                                    post.setPostId(documentSnapshot.getId());
                                    posts.add(post);
                                }
                               setupPosts(posts);
                            }

                        }
                    });

        }
        else {
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            database.collection(Constants.COLLECTION_POST_KEY)
                    .whereEqualTo(Constants.ATTRIBUTE_POST_POSTER_ID_KEY,prefrencesManager.getString(Constants.ATTRIBUTE_USERS_ID_KEY))
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful() && task.getResult().size() != 0){
                                for(QueryDocumentSnapshot documentSnapshot : task.getResult()){
                                    Post post = documentSnapshot.toObject(Post.class);
                                    post.setPostId(documentSnapshot.getId());
                                    posts.add(post);
                                }
                                setupPosts(posts);
                            }

                        }
                    });
        }
    }
    private void setupPosts(ArrayList<Post> posts){
        GroupPostsAdapter groupPostsAdapter = new
                GroupPostsAdapter(posts,getApplicationContext(),ShowPosts.this);
        LinearLayoutManager linearLayoutManager = new
                LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false);
        binding.showPostsRecycler.setLayoutManager(linearLayoutManager);
        binding.showPostsRecycler.setAdapter(groupPostsAdapter);
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
                System.out.println(item);
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
                System.out.println(item);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(type.equals("group")){
                Intent intent = new Intent(getApplicationContext(),GroupActivity.class);
                intent.putExtra("group",group);
                startActivity(intent);
                finish();

        }
        else if(type.equals("checkProfile")){
                Intent intent = new Intent(getApplicationContext(),CheckProfile.class);
                intent.putExtra("userId",userId);
                startActivity(intent);
                finish();

        }
        else if(type.equals("userInGroup")){
            Intent intent = new Intent(getApplicationContext(),GroupActivity.class);
            intent.putExtra("group",group);
            startActivity(intent);
            finish();
        }
        else{
            Intent intent = new Intent(getApplicationContext(),UserProfileActivity.class);
            startActivity(intent);
            finish();
        }


    }

}