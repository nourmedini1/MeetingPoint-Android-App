package com.app.meetingpoint.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.app.meetingpoint.R;
import com.app.meetingpoint.databinding.ActivityCreatePostBinding;
import com.app.meetingpoint.helpers.Constants;
import com.app.meetingpoint.helpers.PrefrencesManager;
import com.app.meetingpoint.models.Group;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;

public class CreatePost extends BaseActivity {
    ActivityCreatePostBinding binding ;
    PrefrencesManager prefrencesManager;
    Group group ;
    String encodedImage="";
    String visibility ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreatePostBinding.inflate(getLayoutInflater());
        prefrencesManager = new PrefrencesManager(this);
        setContentView(binding.getRoot());
        group = (Group) getIntent().getSerializableExtra("group");
        visibility = getIntent().getStringExtra("visibility");
        setupUserImage(prefrencesManager);
        setupUserName(prefrencesManager);
        setupVisibility(visibility);
        binding.createPostCancelPhoto.setVisibility(View.INVISIBLE);
        binding.createPostAddPhoto.setVisibility(View.VISIBLE);
        binding.createPostImage.setVisibility(View.INVISIBLE);
        binding.createPostBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToGroup = new Intent(getApplicationContext(),GroupActivity.class);
                intentToGroup.putExtra("group",group);
                startActivity(intentToGroup);
                finish();
            }
        });
        binding.createPostAddPhotoController.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onClick(View v) {
                if(encodedImage.equals("")){
                    Intent intentPickImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intentPickImage.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    pickImage.launch(intentPickImage);
                    binding.createPostImage.setVisibility(View.VISIBLE);
                    binding.createPostAddPhoto.setVisibility(View.INVISIBLE);
                    binding.createPostCancelPhoto.setVisibility(View.VISIBLE);
                }
                else {
                    encodedImage = "";
                    binding.createPostImage.setImageDrawable(null);
                    binding.createPostImage.setVisibility(View.INVISIBLE);
                    binding.createPostAddPhoto.setVisibility(View.VISIBLE);
                    binding.createPostCancelPhoto.setVisibility(View.INVISIBLE);
                }


            }
        });
        binding.createPostPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPost(prefrencesManager,group);
                Intent intentToGroup = new Intent(getApplicationContext(),GroupActivity.class);
                intentToGroup.putExtra("group",group);
                startActivity(intentToGroup);
                finish();
            }
        });

    }
    private String encodeImage(@NonNull Bitmap bitmap){
        int previewWidth = bitmap.getWidth() ;
        int previewHeight = bitmap.getHeight();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap,previewWidth,previewHeight,false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes,Base64.DEFAULT);

    }
    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if(result.getResultCode() == RESULT_OK){
                    if(result.getData() != null){
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            binding.createPostImage.setImageBitmap(bitmap);
                            encodedImage = encodeImage(bitmap);
                        }catch (FileNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
    );
    private void createPost(PrefrencesManager prefrencesManager , Group group){
        if(binding.createPostCaption.getText().toString().trim().equals("")){
            Toast.makeText(this, "the post is empty !", Toast.LENGTH_SHORT).show();
        }
        else {
            HashMap<String,Object> toPut = new HashMap<>();
            toPut.put(Constants.ATTRIBUTE_POST_CAPTION_KEY,binding.createPostCaption.getText().toString().trim());
            toPut.put(Constants.ATTRIBUTE_POST_GROUP_ID_KEY,group.getId());
            toPut.put(Constants.ATTRIBUTE_POST_IMAGE_KEY,encodedImage);
            toPut.put(Constants.ATTRIBUTE_POST_VISIBILITY_KEY,visibility);
            toPut.put(Constants.ATTRIBUTE_POST_GROUP_NAME_KEY,group.getGroupName());
            toPut.put(Constants.ATTRIBUTE_POST_NB_COMMENTS_KEY,0);
            toPut.put(Constants.ATTRIBUTE_POST_NB_REACTS_KEY,0);
            toPut.put(Constants.ATTRIBUTE_POST_NB_SHARES_KEY,0);
            toPut.put(Constants.ATTRIBUTE_POST_SHARE_TO_KEY,new ArrayList<String>(Collections.singleton("")));
            toPut.put(Constants.ATTRIBUTE_POST_COMMENTS_KEY,new ArrayList<String>(Collections.singleton("")));
            toPut.put(Constants.ATTRIBUTE_POST_REACTS_KEY,new ArrayList<String>(Collections.singleton("")));
            toPut.put(Constants.ATTRIBUTE_POST_POSTER_ID_KEY,prefrencesManager.getString(Constants.ATTRIBUTE_USERS_ID_KEY));
            toPut.put(Constants.ATTRIBUTE_POST_POSTER_IMAGE_KEY,prefrencesManager.getString(Constants.ATTRIBUTE_USERS_IMAGE_KEY));
            toPut.put(Constants.ATTRIBUTE_POST_POSTER_NAME_KEY,prefrencesManager.getString(Constants.ATTRIBUTE_USERS_USERNAME_KEY));
            String timeStamp = new SimpleDateFormat("yyyy/MM/dd HH:mm").format(Calendar.getInstance().getTime());
            toPut.put(Constants.ATTRIBUTE_POST_DATE_KEY,timeStamp);
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            database.collection(Constants.COLLECTION_POST_KEY)
                    .add(toPut)
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(CreatePost.this, "post published !", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
    private void setupUserImage(PrefrencesManager prefrencesManager){
        byte[] userBytes  = Base64.decode(prefrencesManager.getString(Constants.ATTRIBUTE_USERS_IMAGE_KEY), Base64.DEFAULT);
        Bitmap userDecodedBytes = BitmapFactory.decodeByteArray(userBytes, 0, userBytes.length);
        binding.createPostUserImage.setImageBitmap(userDecodedBytes);
    }
    private void setupVisibility(String visibility){
        if(visibility.equals("public")){
            binding.createPostVisibility.setImageDrawable(getDrawable(R.drawable.ic_baseline_cell_tower_24));

        }
        else {
            binding.createPostVisibility.setImageDrawable(getDrawable(R.drawable.ic_baseline_lock_person_24));

        }
    }
    private void setupUserName(PrefrencesManager prefrencesManager){
        binding.createPostUsername.setText(prefrencesManager.getString(Constants.ATTRIBUTE_USERS_USERNAME_KEY));
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intentToGroup = new Intent(getApplicationContext(),GroupActivity.class);
        intentToGroup.putExtra("group",group);
        startActivity(intentToGroup);
        finish();
    }
}