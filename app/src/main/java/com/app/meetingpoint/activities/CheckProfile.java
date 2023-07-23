package com.app.meetingpoint.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.app.meetingpoint.R;
import com.app.meetingpoint.adapters.UserProfileMyFavoriteTopicsAdapter;
import com.app.meetingpoint.adapters.UserProfileMyGroupsAdapter;
import com.app.meetingpoint.databinding.ActivityCheckProfileBinding;
import com.app.meetingpoint.helpers.Constants;
import com.app.meetingpoint.helpers.PrefrencesManager;
import com.app.meetingpoint.interfaces.RecyclerViewInterface;
import com.app.meetingpoint.models.Conversation;
import com.app.meetingpoint.models.Group;
import com.app.meetingpoint.models.Post;
import com.app.meetingpoint.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

public class CheckProfile extends BaseActivity implements RecyclerViewInterface {
    ActivityCheckProfileBinding binding ;
    PrefrencesManager prefrencesManager ;
    int i = 0 ;
    int position ;
    private Timer timer ;
    private TimerTask timerTask ;
    LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckProfileBinding.inflate(getLayoutInflater());
        prefrencesManager = new PrefrencesManager(this);
        setContentView(binding.getRoot());
        User[] user1 = new User[1];
        user1[0] = new User();
        String userId = getIntent().getStringExtra("userId");
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.COLLECTION_USERS_KEY)
                .document(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            User user = task.getResult().toObject(User.class);
                            assert user != null;
                            user.setUserId(task.getResult().getId());
                            setupBackImage(user);
                            setupImage(user);
                            setupRating(user);
                            setupUserName(user);
                            setupSharedGroups(prefrencesManager, user);
                            setupSharedTopics(prefrencesManager, user);
                            user1[0].setUserId(user.getUserId());
                            user1[0].setAddress(user.getAddress());
                            user1[0].setBackImage(user.getBackImage());
                            user1[0].setEducation(user.getEducation());
                            user1[0].setImage(user.getImage());
                            user1[0].setEmail(user.getEmail());
                            user1[0].setFavoriteTopics(user.getFavoriteTopics());
                            user1[0].setGroups(user.getGroups());
                            user1[0].setPassword(user.getPassword());
                            user1[0].setRating(user.getRating());
                            user1[0].setUsername(user.getUsername());
                            user1[0].setWork(user.getWork());


                        }

                    }
                });

        binding.checkProfilePosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupPosts(user1[0]);
            }
        });
        binding.checkProfileSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = prefrencesManager.getString(Constants.ATTRIBUTE_USERS_ID_KEY);
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection(Constants.COLLECTION_CONVERSATIONS_KEY)
                        .where(Filter.or(
                                Filter.and(
                                        Filter.equalTo(Constants.ATTRIBUTE_CONVERSATIONS_FIRST_PARTY_KEY, userId),
                                        Filter.equalTo(Constants.ATTRIBUTE_CONVERSATIONS_SECOND_PARTY_KEY, user1[0].getUserId())
                                ),
                                Filter.and(
                                        Filter.equalTo(Constants.ATTRIBUTE_CONVERSATIONS_FIRST_PARTY_KEY, user1[0].getUserId()),
                                        Filter.equalTo(Constants.ATTRIBUTE_CONVERSATIONS_SECOND_PARTY_KEY, userId)
                                )
                        ))
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (task.getResult().getDocuments().size() > 0) {
                                        Conversation conversation = task.getResult().getDocuments().get(0).toObject(Conversation.class);
                                        assert conversation != null;
                                        conversation.setConversationId(task.getResult().getDocuments().get(0).getId());
                                        Intent intent = new Intent(getApplicationContext(), Chat.class);
                                        intent.putExtra("conversation", conversation);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Date date = new Date();
                                        HashMap<String, Object> conversation = new HashMap<>();
                                        conversation.put(Constants.ATTRIBUTE_CONVERSATIONS_SEEN_KEY, false);
                                        conversation.put(Constants.ATTRIBUTE_CONVERSATIONS_GROUP_KEY, "");
                                        conversation.put(Constants.ATTRIBUTE_CONVERSATIONS_FIRST_PARTY_KEY, prefrencesManager.getString(Constants.ATTRIBUTE_USERS_ID_KEY));
                                        conversation.put(Constants.ATTRIBUTE_CONVERSATIONS_LAST_MESSAGE_KEY, "New conversation");
                                        conversation.put(Constants.ATTRIBUTE_CONVERSATIONS_SECOND_PARTY_KEY, user1[0].getUserId());
                                        conversation.put(Constants.ATTRIBUTE_CONVERSATIONS_TIMESTAMP_KEY, date);
                                        conversation.put(Constants.ATTRIBUTE_CONVERSATIONS_LAST_MESSAGE_SENDER_KEY, prefrencesManager
                                                .getString(Constants.ATTRIBUTE_USERS_ID_KEY));
                                        db.collection(Constants.COLLECTION_CONVERSATIONS_KEY).add(conversation)
                                                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                                        Conversation conversation1 = new Conversation();
                                                        conversation1.setConversationId(task.getResult().getId());
                                                        conversation1.setFirstParty(prefrencesManager.
                                                                getString(Constants.ATTRIBUTE_USERS_ID_KEY));
                                                        conversation1.setGroup("");
                                                        conversation1.setTimestamp(date);
                                                        conversation1.setLastMessage("New conversation");
                                                        conversation1.setSecondParty(user1[0].getUserId());
                                                        conversation1.setSeen(false);
                                                        conversation1.setLastMessageSender(
                                                                prefrencesManager.getString(Constants.ATTRIBUTE_USERS_ID_KEY));
                                                        Intent intent = new Intent(getApplicationContext(), Chat.class);
                                                        intent.putExtra("conversation", conversation1);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                });
                                    }
                                }
                                else {
                                    Toast.makeText(CheckProfile.this, "something went wrong", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
    private void setupBackImage(User user){
        byte[] bytes = Base64.decode(user.getBackImage(),Base64.DEFAULT);
        Bitmap decodedBytes = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        binding.checkProfileUserBackImage.setImageBitmap(decodedBytes);
    }
    private void setupImage(User user){
        byte[] bytes = Base64.decode(user.getImage(),Base64.DEFAULT);
        Bitmap decodedBytes = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        binding.checkProfileUserImage.setImageBitmap(decodedBytes);
    }
    private void setupRating(User user){
        switch (user.getRating()){
            case 0 :
                binding.checkProfileRating.setAnimation(R.raw.stars0);
                break;
            case 1 :
                binding.checkProfileRating.setAnimation(R.raw.stars1);
                break;
            case 2 :
                binding.checkProfileRating.setAnimation(R.raw.stars2);
                break;
            case 3 :
                binding.checkProfileRating.setAnimation(R.raw.stars3);
                break;
            case 4 :
                binding.checkProfileRating.setAnimation(R.raw.stars4);
                break;
            case 5 :
                binding.checkProfileRating.setAnimation(R.raw.stars5);
                break;
        }
    }
    private void setupUserName(User user){
        binding.checkProfileUsername.setText(user.getUsername());
    }
    private void setupSharedGroups(PrefrencesManager prefrencesManager,User user) {
        ArrayList<Group> groups = new ArrayList<>();
        ArrayList<String> myGroups = new ArrayList<>(Arrays.asList(prefrencesManager.getString(Constants.ATTRIBUTE_USERS_GROUPS_KEY).split(","))) ;

        myGroups.retainAll(user.getGroups());
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        binding.checkProfileSharedGroups.setLayoutManager(linearLayoutManager);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        CollectionReference groupCollection = database.collection(Constants.COLLECTION_GROUP_KEY);
        int end = user.getGroups().size();
        for (String groupId : user.getGroups()) {
            DocumentReference documentReference = groupCollection.document(groupId);
            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    Group group = task.getResult().toObject(Group.class);
                    assert group != null;
                    group.setId(task.getResult().getId());
                    groups.add(group);
                    if (i == end-1) {

                        UserProfileMyGroupsAdapter userProfileMyGroupsAdapter = new UserProfileMyGroupsAdapter(getApplicationContext(), groups,CheckProfile.this);
                        binding.checkProfileSharedGroups.setAdapter(userProfileMyGroupsAdapter);
                        position = Integer.MAX_VALUE / 2;
                        binding.checkProfileSharedGroups.scrollToPosition(position);
                        SnapHelper snapHelper = new LinearSnapHelper();
                        snapHelper.attachToRecyclerView(binding.checkProfileSharedGroups);
                        binding.checkProfileSharedGroups.smoothScrollBy(5, 0);
                        binding.checkProfileSharedGroups.addOnScrollListener(new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                                super.onScrollStateChanged(recyclerView, newState);
                                if (newState == 1) {
                                    stopAutoScrollBanner();
                                } else if (newState == 0) {
                                    position = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
                                    runAutoScrollBanner();
                                }
                            }
                        });

                    } else {
                        i++;
                    }
                }
            });
        }



    }
    private void setupSharedTopics(PrefrencesManager prefrencesManager , User user){
        LinearLayoutManager linearLayoutManager = new
                LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false);
        ArrayList<String> myTopics = new
                ArrayList<>(Arrays.asList(prefrencesManager.getString(Constants.ATTRIBUTE_USERS_FAVORITE_TOPICS_KEY).split(",")));

        myTopics.retainAll(user.getFavoriteTopics());
        UserProfileMyFavoriteTopicsAdapter userProfileMyFavoriteTopicsAdapter=
                new UserProfileMyFavoriteTopicsAdapter(getApplicationContext(),myTopics);
        SnapHelper snapHelper = new LinearSnapHelper();
        binding.checkProfileSharedTopics.setLayoutManager(linearLayoutManager);
        binding.checkProfileSharedTopics.setAdapter(userProfileMyFavoriteTopicsAdapter);
        snapHelper.attachToRecyclerView(binding.checkProfileSharedTopics);
    }
    private void setupPosts(User user){
        Intent intent = new Intent(CheckProfile.this, ShowPosts.class);
        intent.putExtra("type","checkProfile");
        intent.putExtra("toolbarTitle",user.getUsername());
        intent.putExtra("userId",user.getUserId());
        startActivity(intent);
        finish();


    }


    @Override
    protected void onResume() {
        super.onResume();
        runAutoScrollBanner();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopAutoScrollBanner();
    }

    private void stopAutoScrollBanner() {
        if (timer != null && timerTask != null) {
            timerTask.cancel();
            timer.cancel();
            timer = null;
            timerTask = null;
            position = linearLayoutManager.findFirstCompletelyVisibleItemPosition();
        }
    }

    private void runAutoScrollBanner() {
        if (timer == null && timerTask == null) {
            timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    if (position == Integer.MAX_VALUE) {
                        position = Integer.MAX_VALUE / 2;
                        binding.checkProfileSharedGroups.scrollToPosition(position);
                        binding.checkProfileSharedGroups.smoothScrollBy(5, 0);
                    } else {
                        position++;
                        binding.checkProfileSharedGroups.smoothScrollToPosition(position);
                    }
                }
            };
            timer.schedule(timerTask, 4000, 4000);
        }
    }

    @Override
    public void onItemClick(int position, String item, String extras) {
         if(item.equals("group")){
             System.out.println("this is extras : "+extras);
             Intent intentToGroup = new Intent(getApplicationContext(),GroupActivity.class);
             FirebaseFirestore database = FirebaseFirestore.getInstance();
             database.collection(Constants.COLLECTION_GROUP_KEY)
                     .document(extras)
                     .get()
                     .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                         @Override
                         public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                             if(task.isSuccessful()){
                                 Group group = task.getResult().toObject(Group.class);
                                 intentToGroup.putExtra("group",group);
                                 startActivity(intentToGroup);
                             }
                         }
                     });

        }
    }
}