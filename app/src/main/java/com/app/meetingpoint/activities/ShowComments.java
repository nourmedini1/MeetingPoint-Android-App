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
import android.widget.Toast;

import com.app.meetingpoint.R;
import com.app.meetingpoint.adapters.CommentsAdapter;
import com.app.meetingpoint.databinding.ActivityShowCommentsBinding;
import com.app.meetingpoint.helpers.Constants;
import com.app.meetingpoint.helpers.PrefrencesManager;
import com.app.meetingpoint.interfaces.RecyclerViewInterface;
import com.app.meetingpoint.models.Comment;
import com.app.meetingpoint.models.Post;
import com.app.meetingpoint.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.ref.PhantomReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ShowComments extends BaseActivity implements RecyclerViewInterface {
    ActivityShowCommentsBinding binding ;
    PrefrencesManager prefrencesManager ;
    ArrayList<Comment> toPostComments ;
    CommentsAdapter commentsAdapter ;
    int i = 0 ;
    String postId ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityShowCommentsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.noComments.setVisibility(View.INVISIBLE);
        prefrencesManager = new PrefrencesManager(this);
        postId = getIntent().getStringExtra("postId");
        toPostComments = new ArrayList<>();
        commentsAdapter = null ;
        setupComments(postId);
        checkForChanges(postId);
        binding.postComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postComment(prefrencesManager,postId);
            }
        });


    }
    private void postComment(PrefrencesManager prefrencesManager,String postId){
        String theComment = binding.addComment.getText().toString().trim();
        if(theComment.equals("")){
            Toast.makeText(this, "comment is empty", Toast.LENGTH_SHORT).show();
        }
        else {
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            database.collection(Constants.COLLECTION_POST_KEY)
                    .document(postId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                Post post = task.getResult().toObject(Post.class);
                                assert post != null;
                                ArrayList<String> comments = post.getComments();
                                comments.add(prefrencesManager.
                                        getString(Constants.ATTRIBUTE_USERS_ID_KEY)+Constants.SEPERATOR+theComment);
                                HashMap<String,Object> hashMap = new HashMap<>();
                                hashMap.put(Constants.ATTRIBUTE_POST_COMMENTS_KEY,comments);
                                hashMap.put(Constants.ATTRIBUTE_POST_NB_COMMENTS_KEY,post.getNbComments()+1);
                                database.collection(Constants.COLLECTION_POST_KEY)
                                        .document(postId)
                                        .update(hashMap)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(ShowComments.this, "comment added", Toast.LENGTH_SHORT).show();
                                                binding.noComments.setVisibility(View.INVISIBLE);
                                                binding.addComment.setText(null);

                                            }
                                        });
                            }
                        }
                    });



        }
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
                        if(post.getComments().size()>toPostComments.size() && toPostComments.size()!=0){
                            i=0;
                            toPostComments.clear();
                            commentsAdapter = null;
                            binding.showCommentsRecycler.setAdapter(null);
                            setupComments(postId);
                        }

                    }
                });
    }

    private void setupComments(String postId) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference users = db.collection(Constants.COLLECTION_USERS_KEY);
            CollectionReference posts = db.collection(Constants.COLLECTION_POST_KEY);
            posts.document(postId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                Post post = task.getResult().toObject(Post.class);
                                assert post != null;
                                post.setPostId(task.getResult().getId());
                                ArrayList<String> comments = post.getComments();
                                if (comments.get(0).equals("")) {
                                    comments.remove(0);
                                }
                                if (comments.size() > 0) {
                                    int end = comments.size() - 1;
                                    toPostComments.clear();
                                    System.out.println("the size of to postComments "+toPostComments.size());
                                    for (String element : comments) {
                                        String[] elementArray = element.split(Constants.SEPERATOR);
                                        users.document(elementArray[0])
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        if (task.isSuccessful()) {
                                                            User user = task.getResult().toObject(User.class);
                                                            assert user != null;
                                                            user.setUserId(task.getResult().getId());
                                                            Comment comment = new Comment(user.getUserId(), user.getImage(), user.getUsername(), elementArray[1]);
                                                            toPostComments.add(comment);
                                                            if (i == end) {
                                                                System.out.println("the comments in setupComments "+toPostComments);
                                                                System.out.println("the size from setup "+toPostComments.size());
                                                                commentsAdapter = new
                                                                        CommentsAdapter(getApplicationContext(), toPostComments, ShowComments.this);
                                                                LinearLayoutManager linearLayoutManager = new
                                                                        LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false);
                                                                binding.showCommentsRecycler.setLayoutManager(linearLayoutManager);
                                                                binding.showCommentsRecycler.setAdapter(commentsAdapter);

                                                            } else {
                                                                i++;
                                                            }

                                                        }
                                                    }
                                                });
                                    }
                                } else {
                                    binding.noComments.setVisibility(View.VISIBLE);
                                }
                            }

                        }
                    });



    }




    @Override
    public void onItemClick(int position, String item, String extras) {
        if(extras.equals(prefrencesManager.getString(Constants.ATTRIBUTE_USERS_ID_KEY))) {
            Intent intentToUserProfile = new Intent(getApplicationContext(),UserProfileActivity.class);
            startActivity(intentToUserProfile);
        }
        else {
            Intent intentToProfile = new Intent(getApplicationContext(),CheckProfile.class);
            intentToProfile.putExtra("userId",extras);
            startActivity(intentToProfile);
        }

    }
}