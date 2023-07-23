package com.app.meetingpoint.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.app.meetingpoint.R;
import com.app.meetingpoint.adapters.CreateGroupTopicsAdapter;
import com.app.meetingpoint.databinding.ActivityCreateGroupBinding;
import com.app.meetingpoint.helpers.Constants;
import com.app.meetingpoint.helpers.PrefrencesManager;
import com.app.meetingpoint.interfaces.RecyclerViewInterface;
import com.app.meetingpoint.models.Group;
import com.app.meetingpoint.models.Topic;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class CreateGroup extends BaseActivity implements RecyclerViewInterface {
    ActivityCreateGroupBinding binding;
    String groupTopic = null;
    String encodedImage = "", encodedBackImage = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateGroupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        PrefrencesManager prefrencesManager = new PrefrencesManager(getApplicationContext());
        setupTopics();
        binding.createGroupInfoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPickImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intentPickImage.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pickProfileImage.launch(intentPickImage);
            }
        });
        binding.createGroupInfoBackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPickImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intentPickImage.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pickBackImage.launch(intentPickImage);
            }
        });
        binding.createGroupInfoApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkDataCorrectness()){
                    applyChanges(prefrencesManager);
                }
            }
        });


    }
    public void onBackPressed() {
        super.onBackPressed();
        Intent intentToGroup = new Intent(getApplicationContext(),ChooseYourGroupsAcitivity.class);
        startActivity(intentToGroup);
        finish();
    }
    private String encodeImage(@NonNull Bitmap bitmap){
        int previewWidth = 250 ;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap,previewWidth,previewHeight,false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG,90,byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes,Base64.DEFAULT);

    }
    private final ActivityResultLauncher<Intent> pickBackImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == RESULT_OK){
                    if(result.getData() != null){
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            binding.createGroupInfoBackImage.setImageBitmap(bitmap);
                            encodedBackImage = encodeImage(bitmap);
                        }catch (FileNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
    );
    private final ActivityResultLauncher<Intent> pickProfileImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == RESULT_OK){
                    if(result.getData() != null){
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            binding.createGroupInfoImage.setImageBitmap(bitmap);
                            encodedImage = encodeImage(bitmap);
                        }catch (FileNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
    );
    private void applyChanges(PrefrencesManager prefrencesManager){

        HashMap<String,Object> changes = new HashMap<>();
        changes.put(Constants.ATTRIBUTE_GROUP_NAME_KEY,binding.createGroupInfoGroupname.getText().toString().trim());
        changes.put(Constants.ATTRIBUTE_GROUP_TOPIC_KEY,groupTopic);
        changes.put(Constants.ATTRIBUTE_GROUP_DESCRIPTION_KEY,binding.createGroupInfoDescription.getText().toString().trim());
        changes.put(Constants.ATTRIBUTE_GROUP_RATING_KEY,0);
        changes.put(Constants.ATTRIBUTE_GROUP_ADMIN_KEY,prefrencesManager.getString(Constants.ATTRIBUTE_USERS_ID_KEY));
        changes.put(Constants.ATTRIBUTE_GROUP_IMAGE_KEY,encodedImage);
        changes.put(Constants.ATTRIBUTE_GROUP_BACK_IMAGE_KEY,encodedBackImage);
        changes.put(Constants.ATTRIBUTE_GROUP_NOUSERS_KEY,0);
        changes.put(Constants.ATTRIBUTE_GROUP_CONVERSATION_ID_KEY,"");
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.COLLECTION_GROUP_KEY)
                .add(changes)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()){
                            Group group = new Group();
                            group.setGroupRating(0);
                            group.setGroupName((String)changes.get(Constants.ATTRIBUTE_GROUP_NAME_KEY));
                            group.setId(task.getResult().getId());
                            group.setTopic(groupTopic);
                            group.setDescription((String) changes.get(Constants.ATTRIBUTE_GROUP_DESCRIPTION_KEY));
                            group.setGroupImage(encodedImage);
                            group.setBackImage(encodedBackImage);
                            group.setAdminId(prefrencesManager.getString(Constants.ATTRIBUTE_USERS_ID_KEY));
                            group.setNumberOfUsers(0);
                            HashMap<String,Object> conv = new HashMap<>();
                            conv.put(Constants.ATTRIBUTE_CONVERSATIONS_TIMESTAMP_KEY,new Date());
                            conv.put(Constants.ATTRIBUTE_CONVERSATIONS_SEEN_KEY,false);
                            conv.put(Constants.ATTRIBUTE_CONVERSATIONS_LAST_MESSAGE_KEY,"");
                            conv.put(Constants.ATTRIBUTE_CONVERSATIONS_LAST_MESSAGE_SENDER_KEY,"");
                            conv.put(Constants.ATTRIBUTE_CONVERSATIONS_SECOND_PARTY_KEY,"");
                            conv.put(Constants.ATTRIBUTE_CONVERSATIONS_FIRST_PARTY_KEY,"");
                            conv.put(Constants.ATTRIBUTE_CONVERSATIONS_GROUP_KEY,group.getId());
                            database.collection(Constants.COLLECTION_CONVERSATIONS_KEY)
                                    .add(conv)
                                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentReference> task) {
                                            if(task.isSuccessful()){
                                                group.setConversationId(task.getResult().getId());
                                                database.collection(Constants.COLLECTION_GROUP_KEY)
                                                        .document(group.getId())
                                                        .update(Constants.ATTRIBUTE_GROUP_CONVERSATION_ID_KEY,task.getResult().getId())
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if(task.isSuccessful()){
                                                                    Intent intent = new Intent(getApplicationContext(),GroupActivity.class);
                                                                    intent.putExtra("group",group);
                                                                    startActivity(intent);
                                                                    finish();
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });

                        }

                    }
                });

    }
    private void setupTopics(){
        ArrayList<Topic> topics = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.COLLECTION_TOPICS_KEY)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(DocumentSnapshot doc : task.getResult().getDocuments()){
                                Topic topic = doc.toObject(Topic.class);
                                assert topic != null;
                                topic.setTopicId(doc.getId());
                                topics.add(topic);
                            }
                            CreateGroupTopicsAdapter adapter = new CreateGroupTopicsAdapter(
                                    CreateGroup.this,
                                    getApplicationContext(),
                                    topics
                            );
                            binding.availableTopics.setAdapter(adapter);

                        }

                    }
                });
    }
    private void showToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    private boolean checkDataCorrectness(){
        boolean toReturn = false;
        String groupname = binding.createGroupInfoGroupname.getText().toString().trim();

        String description = binding.createGroupInfoDescription.getText().toString().trim();
        Animation animation = AnimationUtils.loadAnimation(CreateGroup.this, R.anim.bounce_animation);
        if (groupname.equals("")){
            binding.createGroupInfoGroupname.setAnimation(animation);
            showToast("choose a groupname");
        }
        else if(encodedImage.equals("")){
            showToast("please choose a group image");
        }
        else if (description.equals("")){
            binding.createGroupInfoDescription.setAnimation(animation);
            showToast("please describe your group");
        }
        else if (groupTopic == null){
            showToast("choose the topic of your group");
        }
        else{
            toReturn = true ;
        }
        return toReturn;
    }

    @Override
    public void onItemClick(int position, String item, String extras) {
        if(item.equals("topic")){
            String[] strings = extras.split(Constants.SEPERATOR);
            showToast(strings[0]+" was chosen as the group topic");
            groupTopic = strings[1];
        }

    }
}