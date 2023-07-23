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
import com.app.meetingpoint.adapters.GroupMembersAdapter;
import com.app.meetingpoint.adapters.GroupPostsAdapter;
import com.app.meetingpoint.databinding.ActivityGroupBinding;
import com.app.meetingpoint.helpers.Constants;
import com.app.meetingpoint.helpers.PrefrencesManager;
import com.app.meetingpoint.interfaces.RecyclerViewInterface;
import com.app.meetingpoint.models.Conversation;
import com.app.meetingpoint.models.Group;
import com.app.meetingpoint.models.Post;
import com.app.meetingpoint.models.Topic;
import com.app.meetingpoint.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.prefs.PreferenceChangeEvent;
public class GroupActivity extends BaseActivity implements RecyclerViewInterface {
    ActivityGroupBinding binding ;
    PrefrencesManager prefrencesManager ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        prefrencesManager = new PrefrencesManager(getApplicationContext());
        Group group = (Group) getIntent().getSerializableExtra("group") ;
            binding.groupEditIcon.setVisibility(View.INVISIBLE);
            binding.groupActualTopic.setText(group.getTopic());
            binding.groupDescription.setText(group.getDescription());
            if(group.getAdminId().equals(prefrencesManager.getString(Constants.ATTRIBUTE_USERS_ID_KEY))){
                binding.groupEditIcon.setVisibility(View.VISIBLE);
                binding.groupEditIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intentToEditGroup =new Intent(getApplicationContext(),ModifyGroupInfo.class);
                        intentToEditGroup.putExtra("group",group);
                        startActivity(intentToEditGroup);
                        finish();
                    }
                });
            }
            binding.groupGroupName.setText(group.getGroupName());
            setupRating(group);
            setupGroupBackImage(group);
            setupGroupImage(group);
            setupNousers(group);
            setupUserImage(prefrencesManager);
            setupGroupMembers(group);
            binding.groupPosts.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setupPosts(prefrencesManager,group);
                }
            });
            binding.groupPostText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentToCreatePost = new Intent(getApplicationContext(),ChooseVisibilityOfPost.class);
                    intentToCreatePost.putExtra("group",group);
                    startActivity(intentToCreatePost);
                    finish();
                }
            });
            binding.groupConversation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    seeConversation(group);
                }
            });
            binding.myPostsInThisGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setupMyPosts(prefrencesManager,group);

                }
            });


    }
    private void seeConversation(Group group){
       Intent intent = new Intent(getApplicationContext(),Chat.class);
       Conversation conversation = new Conversation();
       conversation.setConversationId(group.getConversationId());
       conversation.setGroup(group.getId());
       intent.putExtra("conversation",conversation);
       intent.putExtra("group",group);
       startActivity(intent);
       finish();
    }
    private void setupUserImage(PrefrencesManager prefrencesManager){
        byte[] userBytes  = Base64.decode(prefrencesManager.getString(Constants.ATTRIBUTE_USERS_IMAGE_KEY), Base64.DEFAULT);
        Bitmap userDecodedBytes = BitmapFactory.decodeByteArray(userBytes, 0, userBytes.length);
        binding.groupPostUserImage.setImageBitmap(userDecodedBytes);
    }
    private void setupRating(Group group ){

        switch (group.getGroupRating()){
            case 0 :
                binding.groupRating.setAnimation(R.raw.stars0);
                break;
            case 1 :
                binding.groupRating.setAnimation(R.raw.stars1);
                break;
            case 2 :
                binding.groupRating.setAnimation(R.raw.stars2);
                break;
            case 3 :
                binding.groupRating.setAnimation(R.raw.stars3);
                break;
            case 4 :
                binding.groupRating.setAnimation(R.raw.stars4);
                break;
            case 5 :
                binding.groupRating.setAnimation(R.raw.stars5);
                break;
        }
    }
    private void setupGroupImage(Group group){
        byte[] bytes  = Base64.decode(group.getGroupImage(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.groupImage.setImageBitmap(decodedByte);
    }
    private void setupGroupBackImage(Group group){
        if(group.getBackImage() != null){
            if(!group.getBackImage().equals("")){
                byte[] bytes  = Base64.decode(group.getBackImage(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                binding.groupBackImage.setImageBitmap(decodedByte);
            }
        }

    }
    private void setupNousers(Group group){
        int noUsers  = group.getNumberOfUsers();
        String val = String.valueOf(noUsers);
        String toPut = null ;
        if(noUsers< 1000){
            toPut = "0."+val.charAt(0)+"k";
        }
        else if (noUsers<10000){
            toPut = val.charAt(0)+"."+val.charAt(1)+"k";
        }
        else if (noUsers<100000){
            toPut = val.charAt(0)+val.charAt(1)+"."+val.charAt(2)+"k";
        }
        binding.groupNousers.setText(toPut);
    }

    @Override
    public void onItemClick(int position, String item, String extras) {
        PrefrencesManager prefrencesManager = new PrefrencesManager(getApplicationContext());
        if(item.equals("userToCheck")){
            if(extras.equals(prefrencesManager.getString(Constants.ATTRIBUTE_USERS_ID_KEY))){
                Intent intent = new Intent(getApplicationContext(),UserProfileActivity.class);
                startActivity(intent);
            }
            else {
                Intent intent = new Intent(getApplicationContext(),CheckProfile.class);
                intent.putExtra("userId",extras);
                startActivity(intent);
            }

        }

    }
    private void setupPosts(PrefrencesManager prefrencesManager,Group group){

        Intent intent = new Intent(GroupActivity.this, ShowPosts.class);
        intent.putExtra("type","group");
        intent.putExtra("toolbarTitle",group.getGroupName());
        intent.putExtra("group",group);
        startActivity(intent);
        finish();
    }
    private void setupMyPosts(PrefrencesManager prefrencesManager,Group group){

        Intent intent = new Intent(GroupActivity.this, ShowPosts.class);
        intent.putExtra("type","userInGroup");
        intent.putExtra("toolbarTitle",prefrencesManager.getString(Constants.ATTRIBUTE_USERS_USERNAME_KEY));
        intent.putExtra("group",group);
        startActivity(intent);
        finish();
    }
    private void setupGroupMembers(Group group){
        ArrayList<User> users = new ArrayList<>();
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.COLLECTION_USERS_KEY)
                .whereArrayContains(Constants.ATTRIBUTE_USERS_GROUPS_KEY,group.getId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot documentSnapshot: task.getResult()) {
                                User user = documentSnapshot.toObject(User.class);
                                user.setUserId(documentSnapshot.getId());
                                users.add(user);
                            }
                            GroupMembersAdapter groupMembersAdapter = new
                                    GroupMembersAdapter(getApplicationContext(),users,GroupActivity.this);
                            LinearLayoutManager linearLayoutManager = new
                                    LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false);
                            binding.groupMembers.setHasFixedSize(true);
                            SnapHelper snapHelper = new LinearSnapHelper();
                            snapHelper.attachToRecyclerView(binding.groupMembers);
                            binding.groupMembers.setLayoutManager(linearLayoutManager);
                            binding.groupMembers.setAdapter(groupMembersAdapter);
                        }

                    }
                });
    }
}