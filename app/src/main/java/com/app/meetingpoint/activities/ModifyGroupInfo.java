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
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.app.meetingpoint.R;
import com.app.meetingpoint.databinding.ActivityModifyGroupBinding;
import com.app.meetingpoint.helpers.Constants;
import com.app.meetingpoint.helpers.PrefrencesManager;
import com.app.meetingpoint.models.Group;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class ModifyGroupInfo extends BaseActivity {
    ActivityModifyGroupBinding binding ;
    String encodedImage,encodedBackImage ;
    Group group ;
    PrefrencesManager prefrencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityModifyGroupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        prefrencesManager = new PrefrencesManager(getApplicationContext());
        group = (Group) getIntent().getSerializableExtra("group");
        encodedBackImage = group.getBackImage();
        encodedImage = group.getGroupImage();
        setImage(group);
        setBackImage(group);
        initializeEditTexts(group);
        binding.modifyGroupInfoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPickImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intentPickImage.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pickProfileImage.launch(intentPickImage);
            }
        });
        binding.modifyGroupInfoBackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPickImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intentPickImage.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pickBackImage.launch(intentPickImage);
            }
        });
        binding.modifyGroupInfoApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkDataCorrectness()){
                    applyChanges(prefrencesManager,group);
                }
            }
        });
    }
    public void onBackPressed() {
        super.onBackPressed();
        Intent intentToGroup = new Intent(getApplicationContext(),GroupActivity.class);
        intentToGroup.putExtra("group",group);
        startActivity(intentToGroup);
        finish();
    }
    private void setImage(Group group ){
        if(!(group.getGroupImage().equals(""))){
            byte[] bytes  = Base64.decode(group.getGroupImage(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            binding.modifyGroupInfoImage.setImageBitmap(decodedByte);
        }

    }
    private void setBackImage(Group group ){
        if(!(group.getBackImage().equals(""))){
            byte[] bytes  = Base64.decode(group.getBackImage(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            binding.modifyGroupInfoImage.setImageBitmap(decodedByte);
        }

    }


    private void initializeEditTexts(Group group){
        if(!(group.getGroupName() == null) ||
                !(group.getGroupName().equals(""))){
            binding.modifyGroupInfoGroupname.setText(group.getGroupName());
        }
        if(!(group.getDescription()==null)
                || !(group.getDescription().equals(""))){
            binding.modifyGroupInfoDescription.setText(group.getDescription());
        }
        if(!(group.getTopic()==null)
                || !(group.getTopic().equals(""))){
            binding.modifyGroupInfoTopic.setText(group.getTopic());
        }


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
                            binding.modifyGroupInfoBackImage.setImageBitmap(bitmap);
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
                            binding.modifyGroupInfoImage.setImageBitmap(bitmap);
                            encodedImage = encodeImage(bitmap);
                        }catch (FileNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
    );
    private void applyChanges(PrefrencesManager prefrencesManager,Group group ){
        group.setGroupImage(encodedImage);
        group.setBackImage(encodedBackImage);
        group.setGroupName(binding.modifyGroupInfoGroupname.getText().toString().trim());
        group.setDescription(binding.modifyGroupInfoDescription.getText().toString().trim());
        group.setTopic(binding.modifyGroupInfoTopic.getText().toString());
        HashMap<String,Object> changes = new HashMap<>();
        changes.put(Constants.ATTRIBUTE_GROUP_NAME_KEY,group.getGroupName());
        changes.put(Constants.ATTRIBUTE_GROUP_TOPIC_KEY,group.getTopic());
        changes.put(Constants.ATTRIBUTE_GROUP_DESCRIPTION_KEY,group.getDescription());
        changes.put(Constants.ATTRIBUTE_GROUP_RATING_KEY,group.getGroupRating());
        changes.put(Constants.ATTRIBUTE_GROUP_ADMIN_KEY,group.getAdminId());
        changes.put(Constants.ATTRIBUTE_GROUP_IMAGE_KEY,encodedImage);
        changes.put(Constants.ATTRIBUTE_GROUP_BACK_IMAGE_KEY,group.getBackImage());
        changes.put(Constants.ATTRIBUTE_GROUP_NOUSERS_KEY,group.getNumberOfUsers());
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.COLLECTION_GROUP_KEY)
                .document(group.getId())
                .update(changes).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(ModifyGroupInfo.this, "Profile Modified", Toast.LENGTH_SHORT).show();
                    }
                });

    }
    private void showToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    private boolean checkDataCorrectness(){
        boolean toReturn = false;
        String groupname = binding.modifyGroupInfoGroupname.getText().toString().trim();
        String topic = binding.modifyGroupInfoTopic.getText().toString().trim();
        String description = binding.modifyGroupInfoDescription.getText().toString().trim();
        Animation animation = AnimationUtils.loadAnimation(ModifyGroupInfo.this, R.anim.bounce_animation);
        if (groupname.equals("")){
            binding.modifyGroupInfoGroupname.setAnimation(animation);
            showToast("choose a groupname");
        }
        else if (description.equals("")){
            binding.modifyGroupInfoDescription.setAnimation(animation);
            showToast("please describe your group");
        }
        else if (topic.equals("")){
            binding.modifyGroupInfoTopic.setAnimation(animation);
            showToast("choose the topic of your group");
        }
        else{
            toReturn = true ;
        }
        return toReturn;
    }
}