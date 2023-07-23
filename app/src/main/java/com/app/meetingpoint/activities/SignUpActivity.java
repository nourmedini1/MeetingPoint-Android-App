package com.app.meetingpoint.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;
import com.app.meetingpoint.MainActivity;
import com.app.meetingpoint.R;
import com.app.meetingpoint.databinding.ActivitySignUpBinding;
import com.app.meetingpoint.helpers.Constants;
import com.app.meetingpoint.helpers.PrefrencesManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class SignUpActivity extends AppCompatActivity {
    private ActivitySignUpBinding binding ;
    private String encodedImage ;
    private PrefrencesManager prefrencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        prefrencesManager = new PrefrencesManager(getApplicationContext());
        binding.signupButton.setOnClickListener(v -> {
            if(checkDataCorrectness()){
                signUp();
            }

        });
        binding.frameImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPickImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intentPickImage.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pickImage.launch(intentPickImage);
            }
        });
    }
    public void ShowHidePass(View view) {
        if(view.getId()==R.id.showhidepass){
            if(binding.passEdittext.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                ((ImageView)(view)).setImageResource(R.drawable.ic_visibility);
                binding.passEdittext.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
            else{
                ((ImageView)(view)).setImageResource(R.drawable.ic_visibility_off);
                binding.passEdittext.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        }
        else if (view.getId() == R.id.showhidepassconf){
            if(binding.confirmPasswordEdittext.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())){
                ((ImageView)(view)).setImageResource(R.drawable.ic_visibility);
                binding.confirmPasswordEdittext.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
            else{
                ((ImageView)(view)).setImageResource(R.drawable.ic_visibility_off);
                binding.confirmPasswordEdittext.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        }
    }
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setTitle("Leaving The App :( ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intentToFirstScreen = new Intent(getApplicationContext(),FirstScreenActivity.class);
                        startActivity(intentToFirstScreen);
                        android.os.Process.killProcess(android.os.Process.myPid());

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

    private String encodeImage(@NonNull Bitmap bitmap){
        int previewWidth = 150 ;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
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
                            binding.image.setImageBitmap(bitmap);
                            binding.addImageTxt.setVisibility(View.GONE);
                            encodedImage = encodeImage(bitmap);
                        }catch (FileNotFoundException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
    );
    private void loading(boolean isloading){
        if(isloading){
            binding.signupButton.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        }
        else {
            binding.checkMark.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }
    private boolean checkDataCorrectness(){
        boolean toReturn = false;
        String email = binding.emailEdittext.getText().toString().trim();
        String password = binding.passEdittext.getText().toString().trim();
        String confirmPassword = binding.confirmPasswordEdittext.getText().toString().trim();
        Animation animation = AnimationUtils.loadAnimation(SignUpActivity.this,R.anim.bounce_animation);
        if(encodedImage == null ){
            binding.image.setAnimation(animation);
            showToast("choose an image");
        }
         else if (email.equals("")){
            binding.emailEdittext.setAnimation(animation);
            showToast("we need your email");
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            showToast("invalid email");
            binding.emailEdittext.setAnimation(animation);
        }
        else if (password.equals("")){
            binding.passEdittext.setAnimation(animation);
            showToast("choose your password");
        }
        else if (confirmPassword.equals("")){
            binding.confirmPasswordEdittext.setAnimation(animation);
            showToast("confirm the password");
        }
        else if(!confirmPassword.equals(password)){
            binding.confirmPasswordEdittext.setAnimation(animation);
            showToast("confirmation doesn't match the password");
        }
        else{
            toReturn = true ;
        }
        return toReturn;
    }
    private void signUp(){
            loading(true);
            String email = binding.emailEdittext.getText().toString().trim();
            String password = binding.passEdittext.getText().toString().trim();
            String username = email.split("@")[0];
            List<String> favoriteTopics = Collections.singletonList("");
            List<String> groups = Collections.singletonList("");
            FirebaseFirestore database = FirebaseFirestore.getInstance();
            HashMap<String,Object> user = new HashMap<>();
            user.put(Constants.ATTRIBUTE_USERS_USERNAME_KEY,username);
            user.put(Constants.ATTRIBUTE_USERS_EMAIL_KEY,email);
            user.put(Constants.ATTRIBUTE_USERS_PASSWORD_KEY,password);
            user.put(Constants.ATTRIBUTE_USERS_IMAGE_KEY,encodedImage);
            user.put(Constants.ATTRIBUTE_USERS_BACK_IMAGE_KEY,"");
            user.put(Constants.ATTRIBUTE_USERS_FAVORITE_TOPICS_KEY, favoriteTopics);
            user.put(Constants.ATTRIBUTE_USERS_GROUPS_KEY,groups);
            user.put(Constants.ATTRIBUTE_USERS_RATING_KEY,0);
            user.put(Constants.ATTRIBUTE_USERS_WORK_KEY,"");
            user.put(Constants.ATTRIBUTE_USERS_EDUCATION_KEY,"");
            user.put(Constants.ATTRIBUTE_USERS_ADDRESS_KEY,"");
            user.put(Constants.ATTRIBUTE_USERS_FCM_TOKEN_KEY,"");
            user.put(Constants.ATTRIBUTR_USERS_AVAILABLE_KEY,false);
            database.collection(Constants.COLLECTION_USERS_KEY)
                    .add(user)
                    .addOnSuccessListener(documentReference -> {
                        loading(false);
                        prefrencesManager.clear();
                        prefrencesManager.putString(Constants.ATTRIBUTE_USERS_ID_KEY,documentReference.getId());
                        prefrencesManager.putString(Constants.ATTRIBUTE_USERS_USERNAME_KEY,username);
                        prefrencesManager.putString(Constants.ATTRIBUTE_USERS_IMAGE_KEY,encodedImage);
                        prefrencesManager.putString(Constants.ATTRIBUTE_USERS_EMAIL_KEY,email);
                        prefrencesManager.putString(Constants.ATTRIBUTE_USERS_BACK_IMAGE_KEY,"");
                        prefrencesManager.putString(Constants.ATTRIBUTE_USERS_FAVORITE_TOPICS_KEY, "");
                        prefrencesManager.putString(Constants.ATTRIBUTE_USERS_GROUPS_KEY,"");
                        prefrencesManager.putString(Constants.ATTRIBUTE_USERS_RATING_KEY,String.valueOf(0));
                        prefrencesManager.putString(Constants.ATTRIBUTE_USERS_WORK_KEY,"");
                        prefrencesManager.putString(Constants.ATTRIBUTE_USERS_EDUCATION_KEY,"");
                        prefrencesManager.putString(Constants.ATTRIBUTE_USERS_ADDRESS_KEY,"");

                        Intent intentToModifyUserInfo = new Intent(getApplicationContext(), ModifyUserInfoActivity.class);
                        intentToModifyUserInfo.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intentToModifyUserInfo.putExtra("new_user",true);
                        startActivity(intentToModifyUserInfo);

                    })
                    .addOnFailureListener(exception -> {
                            loading(false);
                            showToast(exception.getMessage());
                    });

        }
    private void showToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}