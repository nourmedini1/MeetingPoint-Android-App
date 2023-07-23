

package com.app.meetingpoint.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;


import com.app.meetingpoint.R;
import com.app.meetingpoint.adapters.GroupPostsAdapter;
import com.app.meetingpoint.adapters.UserProfileMyFavoriteTopicsAdapter;
import com.app.meetingpoint.adapters.UserProfileMyGroupsAdapter;
import com.app.meetingpoint.databinding.ActivityUserProfileBinding;
import com.app.meetingpoint.helpers.Constants;
import com.app.meetingpoint.helpers.PrefrencesManager;
import com.app.meetingpoint.interfaces.RecyclerViewInterface;
import com.app.meetingpoint.models.Group;
import com.app.meetingpoint.models.Post;
import com.app.meetingpoint.models.Topic;
import com.app.meetingpoint.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;


public class UserProfileActivity extends BaseActivity implements RecyclerViewInterface {
    ActivityUserProfileBinding binding ;
    PrefrencesManager prefrencesManager ;
    private ArrayList<Group> myGroups ;
    private ArrayList<String> myTopics ;
    private LinearLayoutManager linearLayoutManager ;
    private int position ;
    private Timer timer ;
    private TimerTask timerTask ;
    int i = 0 ;
    int j = 0 ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        prefrencesManager = new PrefrencesManager(getApplicationContext());
        setImage(prefrencesManager);
        setupRating(prefrencesManager);
        setUserName(prefrencesManager);

        setBackImage(prefrencesManager);
        setupMyGroupsShower(prefrencesManager);
        setupMyFavoriteTopicsShower(prefrencesManager);
        binding.myPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupMyPosts(prefrencesManager);
            }
        });
        binding.logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout(prefrencesManager);
            }
        });
        binding.userProfileMyGroups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToChooseGroups = new Intent(getApplicationContext(),ChooseYourGroupsAcitivity.class);
                startActivity(intentToChooseGroups);
                finish();
            }
        });
        binding.userProfileUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToModifyUserActivity = new Intent(getApplicationContext(),ModifyUserInfoActivity.class);
                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        UserProfileActivity.this,
                        binding.userProfilePhotosFrame,
                        Objects.requireNonNull(ViewCompat.getTransitionName(binding.userProfilePhotosFrame)));
                startActivity(intentToModifyUserActivity,optionsCompat.toBundle());
                finish();
            }
        });
        binding.userProfileMyFavouriteTopics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToChooseFavoriteTopics = new Intent(getApplicationContext(),ChooseYourFavoriteTopicsActivity.class);
                startActivity(intentToChooseFavoriteTopics);
                finish();
            }
        });
        binding.userConversation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),ChatList.class));
                finish();
            }
        });
    }
    private void setImage(PrefrencesManager prefrencesManager){
        if(!(prefrencesManager.getString(Constants.ATTRIBUTE_USERS_IMAGE_KEY) == null)){
            byte[] bytes  = Base64.decode(prefrencesManager.getString(Constants.ATTRIBUTE_USERS_IMAGE_KEY), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            binding.userProfileImage.setImageBitmap(decodedByte);
        }

    }
    private void setBackImage(PrefrencesManager prefrencesManager){
        if(!(prefrencesManager.getString(Constants.ATTRIBUTE_USERS_BACK_IMAGE_KEY) == null)){
            byte[] bytes  = Base64.decode(prefrencesManager.getString(Constants.ATTRIBUTE_USERS_BACK_IMAGE_KEY), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            binding.userProfileBackImage.setImageBitmap(decodedByte);
        }

    }
    private void setUserName(PrefrencesManager prefrencesManager){
        binding.userProfileUsername.setText(prefrencesManager.getString(Constants.ATTRIBUTE_USERS_USERNAME_KEY));
    }
    private void setupMyGroupsShower(PrefrencesManager prefrencesManager) {
        myGroups = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        binding.userProfileMyGroupsRecycler.setLayoutManager(linearLayoutManager);
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
                        int end = user.getGroups().size();
                        for (String groupId : user.getGroups()) {
                            System.out.println("the group id : "+groupId);
                            DocumentReference documentReference = groupCollection.document(groupId);
                            documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    Group group = task.getResult().toObject(Group.class);
                                    assert group != null;
                                    System.out.println("this is the id of the group "+task.getResult().getId());
                                    group.setId(task.getResult().getId());
                                    myGroups.add(group);
                                    if (i == end-1) {

                                        UserProfileMyGroupsAdapter userProfileMyGroupsAdapter = new UserProfileMyGroupsAdapter(getApplicationContext(), myGroups,UserProfileActivity.this);
                                        binding.userProfileMyGroupsRecycler.setAdapter(userProfileMyGroupsAdapter);
                                        if (myGroups != null) {
                                            position = Integer.MAX_VALUE / 2;
                                            binding.userProfileMyGroupsRecycler.scrollToPosition(position);
                                        }
                                        SnapHelper snapHelper = new LinearSnapHelper();
                                        snapHelper.attachToRecyclerView(binding.userProfileMyGroupsRecycler);
                                        binding.userProfileMyGroupsRecycler.smoothScrollBy(5, 0);
                                        binding.userProfileMyGroupsRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                            }
                            );
                        }
                    }
                }
                );
    }
    private void setupMyFavoriteTopicsShower(PrefrencesManager prefrencesManager){
        myTopics = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        binding.userProfileMyFavouriteTopicsRecycler.setLayoutManager(linearLayoutManager);
        String[] strings = prefrencesManager.getString(Constants.ATTRIBUTE_USERS_FAVORITE_TOPICS_KEY).split(",");
        myTopics.addAll(Arrays.asList(strings));
        UserProfileMyFavoriteTopicsAdapter userProfileMyFavoriteTopicsAdapter =
                new UserProfileMyFavoriteTopicsAdapter(getApplicationContext(), myTopics);
        binding.userProfileMyFavouriteTopicsRecycler.setAdapter(userProfileMyFavoriteTopicsAdapter);
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(binding.userProfileMyFavouriteTopicsRecycler);


    }
    private String encodeImage(@NonNull Bitmap bitmap){
        int previewWidth = 150 ;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap,previewWidth,previewHeight,false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG,80,byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes,Base64.DEFAULT);
    }
    private void setupMyPosts(PrefrencesManager prefrencesManager){

        Intent intent = new Intent(UserProfileActivity.this, ShowPosts.class);
        intent.putExtra("toolbarTitle",prefrencesManager.getString(Constants.ATTRIBUTE_USERS_USERNAME_KEY));
        startActivity(intent);
        finish();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intentToHomeScreen = new Intent(UserProfileActivity.this,HomeScreenActivity.class);
        startActivity(intentToHomeScreen);
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
                        binding.userProfileMyGroupsRecycler.scrollToPosition(position);
                        binding.userProfileMyGroupsRecycler.smoothScrollBy(5, 0);
                    } else {
                        position++;
                        binding.userProfileMyGroupsRecycler.smoothScrollToPosition(position);
                    }
                }
            };
            timer.schedule(timerTask, 4000, 4000);
        }
    }
    private void logout(PrefrencesManager prefrencesManager){
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setTitle("Logout ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        prefrencesManager.clear();
                        Intent intentToFirstScreen = new Intent(getApplicationContext(),FirstScreenActivity.class);
                        startActivity(intentToFirstScreen);
                        finish();
                    }

                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        builder.create();
        builder.show();


    }
    private void setupRating(PrefrencesManager prefrencesManager){

        switch (Integer.parseInt(prefrencesManager.getString(Constants.ATTRIBUTE_USERS_RATING_KEY))){
            case 0 :
                binding.userProfileRating.setAnimation(R.raw.stars0);
                break;
            case 1 :
                binding.userProfileRating.setAnimation(R.raw.stars1);
                break;
            case 2 :
                binding.userProfileRating.setAnimation(R.raw.stars2);
                break;
            case 3 :
                binding.userProfileRating.setAnimation(R.raw.stars3);
                break;
            case 4 :
                binding.userProfileRating.setAnimation(R.raw.stars4);
                break;
            case 5 :
                binding.userProfileRating.setAnimation(R.raw.stars5);
                break;
        }
    }

    @Override
    public void onItemClick(int position, String item, String extras) {
        if(item.equals("group")){
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            database.collection(Constants.COLLECTION_GROUP_KEY)
                    .document(extras)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                Group toSendGroup = task.getResult().toObject(Group.class);
                                assert toSendGroup != null;
                                toSendGroup.setId(task.getResult().getId());
                                Intent intentToGroup = new Intent(getApplicationContext(),GroupActivity.class);
                                intentToGroup.putExtra("group",toSendGroup);
                                startActivity(intentToGroup);
                                finish();
                            }
                        }
                    });
        }
        else if(item.equals("reacts")){
            Intent intentToShowReacts = new Intent(getApplicationContext(),ShowReacts.class);
            intentToShowReacts.putExtra("reacts",extras);
            startActivity(intentToShowReacts);
        }
        else if(item.equals("comments")) {
            Intent intentToShowComments = new Intent(getApplicationContext(),ShowComments.class);
            intentToShowComments.putExtra("comments",extras);
            startActivity(intentToShowComments);
        }
    }
}

