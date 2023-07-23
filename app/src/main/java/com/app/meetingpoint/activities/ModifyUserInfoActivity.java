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
import com.app.meetingpoint.databinding.ActivityModifyUserInfoBinding;
import com.app.meetingpoint.helpers.Constants;
import com.app.meetingpoint.helpers.PrefrencesManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.units.qual.C;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;

public class ModifyUserInfoActivity extends BaseActivity {
    private ActivityModifyUserInfoBinding binding ;
    private PrefrencesManager prefrencesManager ;
    private String encodedImage ,profileEncodedImage;
    private boolean newUser ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityModifyUserInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        newUser = getIntent().getBooleanExtra("new_user",false);
        prefrencesManager = new PrefrencesManager(getApplicationContext());
        profileEncodedImage = prefrencesManager.getString(Constants.ATTRIBUTE_USERS_IMAGE_KEY);
        encodedImage = prefrencesManager.getString(Constants.ATTRIBUTE_USERS_BACK_IMAGE_KEY);
        initializeEditTexts(prefrencesManager);
        setBackImage(prefrencesManager);
        setImage(prefrencesManager);
        binding.userProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPickImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intentPickImage.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pickProfileImage.launch(intentPickImage);
            }
        });
        binding.modifyUserBackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPickImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intentPickImage.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pickBackImage.launch(intentPickImage);
            }
        });
        binding.modifyUserInfoApplyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkDataCorrectness()){
                    applyChanges(prefrencesManager);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intentToUserProfileActivity = new Intent(getApplicationContext(),UserProfileActivity.class);
        startActivity(intentToUserProfileActivity);
        finish();
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
            binding.modifyUserBackImage.setImageBitmap(decodedByte);
        }

    }

    private void initializeEditTexts(PrefrencesManager prefrencesManager){
        if(!(prefrencesManager.getString(Constants.ATTRIBUTE_USERS_ADDRESS_KEY) == null) ||
                !(prefrencesManager.getString(Constants.ATTRIBUTE_USERS_ADDRESS_KEY).equals(""))){
            binding.modifyUserInfoAddressEdit.setText(prefrencesManager.getString(Constants.ATTRIBUTE_USERS_ADDRESS_KEY));
        }
        if(!(prefrencesManager.getString(Constants.ATTRIBUTE_USERS_EDUCATION_KEY)==null)
        || !(prefrencesManager.getString(Constants.ATTRIBUTE_USERS_EDUCATION_KEY).equals(""))){
            binding.modifyUserInfoEducationEdit.setText(prefrencesManager.getString(Constants.ATTRIBUTE_USERS_EDUCATION_KEY));
        }
        if(!(prefrencesManager.getString(Constants.ATTRIBUTE_USERS_WORK_KEY)==null)
        || !(prefrencesManager.getString(Constants.ATTRIBUTE_USERS_WORK_KEY).equals(""))){
            binding.modifyUserInfoWorkEdit.setText(prefrencesManager.getString(Constants.ATTRIBUTE_USERS_WORK_KEY));
        }
        binding.modifyUserInfoUsernameEdit.setText(prefrencesManager.getString(Constants.ATTRIBUTE_USERS_USERNAME_KEY));
        binding.modifyUserInfoEmailEdit.setText(prefrencesManager.getString(Constants.ATTRIBUTE_USERS_EMAIL_KEY));

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
                            binding.modifyUserBackImage.setImageBitmap(bitmap);
                            encodedImage = encodeImage(bitmap);
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
                            binding.userProfileImage.setImageBitmap(bitmap);
                            profileEncodedImage = encodeImage(bitmap);
                        }catch (FileNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
    );
    private void applyChanges(PrefrencesManager prefrencesManager){
        prefrencesManager.putString(Constants.ATTRIBUTE_USERS_USERNAME_KEY,binding.modifyUserInfoUsernameEdit
                .getText().toString().trim());
        prefrencesManager.putString(Constants.ATTRIBUTE_USERS_EMAIL_KEY,binding.modifyUserInfoEmailEdit
                .getText().toString().trim());
        prefrencesManager.putString(Constants.ATTRIBUTE_USERS_ADDRESS_KEY,binding.modifyUserInfoAddressEdit
                .getText().toString().trim());
        prefrencesManager.putString(Constants.ATTRIBUTE_USERS_EDUCATION_KEY,binding.modifyUserInfoEducationEdit
                .getText().toString().trim());
        prefrencesManager.putString(Constants.ATTRIBUTE_USERS_WORK_KEY,binding.modifyUserInfoWorkEdit
                .getText().toString().trim());
        prefrencesManager.putString(Constants.ATTRIBUTE_USERS_BACK_IMAGE_KEY,encodedImage);
        prefrencesManager.putString(Constants.ATTRIBUTE_USERS_IMAGE_KEY,profileEncodedImage);
        HashMap<String,Object> changes = new HashMap<>();
        changes.put(Constants.ATTRIBUTE_USERS_USERNAME_KEY,binding.modifyUserInfoUsernameEdit
                .getText().toString().trim());
        changes.put(Constants.ATTRIBUTE_USERS_EMAIL_KEY,binding.modifyUserInfoEmailEdit
                .getText().toString().trim());
        changes.put(Constants.ATTRIBUTE_USERS_ADDRESS_KEY,binding.modifyUserInfoAddressEdit
                .getText().toString().trim());
        changes.put(Constants.ATTRIBUTE_USERS_EDUCATION_KEY,binding.modifyUserInfoEducationEdit
                .getText().toString().trim());
        changes.put(Constants.ATTRIBUTE_USERS_WORK_KEY,binding.modifyUserInfoWorkEdit
                .getText().toString().trim());
        changes.put(Constants.ATTRIBUTE_USERS_BACK_IMAGE_KEY,encodedImage);
        changes.put(Constants.ATTRIBUTE_USERS_IMAGE_KEY,profileEncodedImage);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
                database.collection(Constants.COLLECTION_USERS_KEY)
                .document(prefrencesManager.getString(Constants.ATTRIBUTE_USERS_ID_KEY))
                .update(changes).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(ModifyUserInfoActivity.this, "Profile Modified", Toast.LENGTH_SHORT).show();
                        if(newUser){
                            Intent intentToChooseGroups = new Intent(getApplicationContext(),NewUserGroupsAndTopicsActivity.class);
                            startActivity(intentToChooseGroups);
                            finish();
                        }
                    }
                });

    }
    private void showToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    private boolean checkDataCorrectness(){
        boolean toReturn = false;
        String email = binding.modifyUserInfoEmailEdit.getText().toString().trim();
        String username = binding.modifyUserInfoUsernameEdit.getText().toString().trim();
        Animation animation = AnimationUtils.loadAnimation(ModifyUserInfoActivity.this,R.anim.bounce_animation);
        if (email.equals("")){
            binding.modifyUserInfoEmailEdit.setAnimation(animation);
            showToast("we need your email");
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            showToast("invalid email");
            binding.modifyUserInfoEmailEdit.setAnimation(animation);
        }
        else if (username.equals("")){
            binding.modifyUserInfoUsernameEdit.setAnimation(animation);
            showToast("choose your username");
        }
        else{
            toReturn = true ;
        }
        return toReturn;
    }
}