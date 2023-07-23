package com.app.meetingpoint.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;

import com.app.meetingpoint.R;
import com.app.meetingpoint.adapters.SharePostAdapter;
import com.app.meetingpoint.databinding.ActivitySharePostBinding;
import com.app.meetingpoint.helpers.Constants;
import com.app.meetingpoint.helpers.PrefrencesManager;
import com.app.meetingpoint.models.Group;
import com.app.meetingpoint.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.prefs.PreferenceChangeEvent;

public class SharePost extends BaseActivity {
    ActivitySharePostBinding binding ;
    int i = 0 ;
    String postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySharePostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        PrefrencesManager prefrencesManager = new PrefrencesManager(getApplicationContext());
        postId= getIntent().getStringExtra("postId");
        setupMyGroupsToShare(prefrencesManager,postId);


    }
    private void setupMyGroupsToShare(PrefrencesManager prefrencesManager,String postId) {
        ArrayList<Group> myGroups = new ArrayList<>();
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.COLLECTION_USERS_KEY)
                .document(prefrencesManager.getString(Constants.ATTRIBUTE_USERS_ID_KEY))
                .get()
                .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                DocumentSnapshot documentSnapshot = task.getResult();
                                User user = documentSnapshot.toObject(User.class);
                                assert user != null;
                                user.setUserId(documentSnapshot.getId());
                                CollectionReference groupCollection = database.collection(Constants.COLLECTION_GROUP_KEY);
                                int end = user.getGroups().size()-1;
                                for (String groupId : user.getGroups()) {
                                    DocumentReference documentReference = groupCollection.document(groupId);
                                    documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            Group group = task.getResult().toObject(Group.class);
                                            assert group != null;
                                            group.setId(task.getResult().getId());
                                            myGroups.add(group);
                                            if (i == end) {
                                                System.out.println("the number of groups is "+myGroups.size());
                                                SharePostAdapter sharePostAdapter = new
                                                        SharePostAdapter(postId,getApplicationContext(),myGroups);
                                                LinearLayoutManager linearLayoutManager =
                                                        new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false);
                                                binding.sharePostRecycler.setLayoutManager(linearLayoutManager);
                                                binding.sharePostRecycler.setAdapter(sharePostAdapter);
                                                System.out.println("The adapter is set");
                                                binding.sharePostRecycler.setHasFixedSize(true);

                                            } else {
                                                i++;
                                            }
                                        }
                                    });
                                }
                            }
                        }
                );
    }
}