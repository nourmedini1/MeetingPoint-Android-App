package com.app.meetingpoint.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.SnapHelper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.app.meetingpoint.adapters.CommentsAdapter;
import com.app.meetingpoint.adapters.ReactsAdapter;
import com.app.meetingpoint.databinding.ActivityShowReactsBinding;
import com.app.meetingpoint.helpers.Constants;
import com.app.meetingpoint.helpers.PrefrencesManager;
import com.app.meetingpoint.interfaces.RecyclerViewInterface;
import com.app.meetingpoint.models.Comment;
import com.app.meetingpoint.models.Post;
import com.app.meetingpoint.models.React;
import com.app.meetingpoint.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.Arrays;

public class ShowReacts extends BaseActivity implements RecyclerViewInterface {
    ActivityShowReactsBinding binding ;
    String postId ;
    ArrayList<React> toPostReacts ;
    ReactsAdapter reactsAdapter ;
    int i=0 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowReactsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.noReacts.setVisibility(View.INVISIBLE);
        toPostReacts = new ArrayList<>();
        reactsAdapter = null;
        postId = getIntent().getStringExtra("postId");
        setReacts(postId);
        checkForChanges(postId);

    }
    private void checkForChanges(String postId){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.COLLECTION_POST_KEY)
                .document(postId)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(error != null){
                            return ;
                        }
                        assert value != null;
                        Post post = value.toObject(Post.class);
                        assert post != null;
                        if(post.getReacts().size()>toPostReacts.size() && toPostReacts.size() >0){
                            i=0;
                            toPostReacts.clear();
                            reactsAdapter = null ;
                            setReacts(postId);
                        }

                    }
                });
    }
    private void setReacts(String postId){
        toPostReacts.clear();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference users = db.collection(Constants.COLLECTION_USERS_KEY);
        CollectionReference posts = db.collection(Constants.COLLECTION_POST_KEY);
        posts.document(postId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            Post post = task.getResult().toObject(Post.class);
                            assert post != null;
                            post.setPostId(task.getResult().getId());
                            ArrayList<String> reacts = post.getReacts();
                            if(reacts.get(0).equals("")){
                                reacts.remove(0);
                            }
                            if(reacts.size()>0){
                                int end = reacts.size()-1;
                                toPostReacts.clear();
                                for (String element:reacts) {
                                    users.document(element).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if(task.isSuccessful()){
                                                User user = task.getResult().toObject(User.class);
                                                assert user != null;
                                                user.setUserId(task.getResult().getId());
                                                React react = new React(user.getUserId(),user.getImage(),user.getUsername());
                                                toPostReacts.add(react);
                                                if(i == end){
                                                    reactsAdapter = new
                                                            ReactsAdapter(getApplicationContext(),
                                                            toPostReacts,ShowReacts.this);
                                                    LinearLayoutManager linearLayoutManager = new
                                                            LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false);
                                                    binding.showReactsRecycler.setLayoutManager(linearLayoutManager);
                                                    binding.showReactsRecycler.setAdapter(reactsAdapter);

                                                }
                                                else {
                                                    i++;
                                                }

                                            }
                                        }
                                    });
                                }
                            }
                            else {
                                binding.noReacts.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
    }


    @Override
    public void onItemClick(int position, String item, String extras) {
        PrefrencesManager prefrencesManager = new PrefrencesManager(getApplicationContext());
        if(item.equals("reacts")){
            if(extras.equals(prefrencesManager.getString(Constants.ATTRIBUTE_USERS_ID_KEY))){
                Intent intentToUserProfile = new Intent(getApplicationContext(),UserProfileActivity.class);
                startActivity(intentToUserProfile);
            }
            else {
                Intent intentToCheckProfile = new Intent(getApplicationContext(),CheckProfile.class);
                intentToCheckProfile.putExtra("userId",extras);
                startActivity(intentToCheckProfile);
            }

        }
    }
}