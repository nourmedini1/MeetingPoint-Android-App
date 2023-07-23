package com.app.meetingpoint.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.app.meetingpoint.MainActivity;
import com.app.meetingpoint.R;
import com.app.meetingpoint.databinding.ActivitySignInBinding;
import com.app.meetingpoint.firebase.MessagingService;
import com.app.meetingpoint.helpers.Constants;
import com.app.meetingpoint.helpers.PrefrencesManager;
import com.app.meetingpoint.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.List;

public class SignInActivity extends AppCompatActivity {
    private ActivitySignInBinding binding;
    private PrefrencesManager prefrencesManager ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        prefrencesManager = new PrefrencesManager(getApplicationContext());
        binding.loginButton.setOnClickListener(v -> {
            if(checkDataCorrectness()){
                signIn(prefrencesManager);
            }
        });




    }
    private void signIn(PrefrencesManager prefrencesManager){
        loading(true);
        String email = binding.emailEdittext.getText().toString().trim();
        String password = binding.passEdittext.getText().toString().trim();
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.COLLECTION_USERS_KEY)
                .whereEqualTo(Constants.ATTRIBUTE_USERS_EMAIL_KEY,email)
                .whereEqualTo(Constants.ATTRIBUTE_USERS_PASSWORD_KEY,password)
                .get()
                .addOnCompleteListener(task -> {
                    loading(false);
                    if(task.isSuccessful()
                            && task.getResult() != null
                            && task.getResult().getDocuments().size() > 0){
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        User user = documentSnapshot.toObject(User.class);
                        prefrencesManager.putBoolean(Constants.SIGNED_IN_KEY,true);
                        System.out.println(prefrencesManager.getBoolean(Constants.SIGNED_IN_KEY));
                        assert user != null;
                        prefrencesManager.putString(Constants.ATTRIBUTE_USERS_ID_KEY, documentSnapshot.getId());
                        prefrencesManager.putString(Constants.ATTRIBUTE_USERS_USERNAME_KEY,
                              user.getUsername());
                        prefrencesManager.putString(Constants.ATTRIBUTE_USERS_IMAGE_KEY,
                                user.getImage());
                        prefrencesManager.putString(Constants.ATTRIBUTE_USERS_EMAIL_KEY,
                                user.getEmail());
                        prefrencesManager.putString(Constants.ATTRIBUTE_USERS_RATING_KEY,
                                String.valueOf(user.getRating()));
                        prefrencesManager.putString(Constants.ATTRIBUTE_USERS_EDUCATION_KEY,
                                user.getEducation());
                        prefrencesManager.putString(Constants.ATTRIBUTE_USERS_ADDRESS_KEY,
                                user.getAddress());
                        prefrencesManager.putString(Constants.ATTRIBUTE_USERS_WORK_KEY,
                                user.getWork());
                        prefrencesManager.putString(Constants.ATTRIBUTE_USERS_BACK_IMAGE_KEY,
                                user.getBackImage());
                        String favoriteTopics ="";
                        for (String topic: user.getFavoriteTopics()) {
                            favoriteTopics+=topic+",";
                        }
                        favoriteTopics = favoriteTopics.substring(0,favoriteTopics.length()-1);
                        String groups ="";
                        for (String group: user.getGroups()) {
                            groups+=group+",";
                        }
                        groups = groups.substring(0,groups.length()-1);
                        prefrencesManager.putString(Constants.ATTRIBUTE_USERS_FAVORITE_TOPICS_KEY,favoriteTopics);
                        prefrencesManager.putString(Constants.ATTRIBUTE_USERS_GROUPS_KEY,groups);
                        Intent intentToMainScreen = new Intent(getApplicationContext(), HomeScreenActivity.class);
                        intentToMainScreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intentToMainScreen);
                    }
                    else {
                        showToast("Unable to login");
                    }
                });

    }
    private void loading(boolean isloading){
        if(isloading){
            binding.loginButton.setVisibility(View.INVISIBLE);
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
        Animation animation = AnimationUtils.loadAnimation(SignInActivity.this,R.anim.bounce_animation);
        if (email.equals("")){
            binding.emailEdittext.setAnimation(animation);
            showToast("we need your email");
            binding.loginButton.setVisibility(View.VISIBLE);
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            showToast("invalid email");
            binding.emailEdittext.setAnimation(animation);
            binding.loginButton.setVisibility(View.VISIBLE);
        }
        else if (password.equals("")){
            binding.passEdittext.setAnimation(animation);
            showToast("choose your password");
            binding.loginButton.setVisibility(View.VISIBLE);
        }
        else{
            toReturn = true ;
        }
        return toReturn;
    }
    private void showToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    public void ShowHidePass(View view) {

        if (view.getId() == R.id.show_pass_btn) {
            if (binding.passEdittext.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                ((ImageView) (view)).setImageResource(R.drawable.ic_visibility);
                //Show Password
                binding.passEdittext.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                ((ImageView) (view)).setImageResource(R.drawable.ic_visibility_off);
                //Hide Password
                binding.passEdittext.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
        }
    }
    private void setAnimations() {
        Animation animationEdit = AnimationUtils.loadAnimation(SignInActivity.this, R.anim.anim_translation_left_right);
        Animation animationTxt = AnimationUtils.loadAnimation(SignInActivity.this, R.anim.anim_trans_right_left);
        binding.emailEdittext.setAnimation(animationEdit);
        binding.passEdittext.setAnimation(animationEdit);
        binding.emailTxt.setAnimation(animationTxt);
        binding.passTxt.setAnimation(animationTxt);
    }
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setTitle("Leaving The App :( ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intentToFirstScreen = new Intent(getApplicationContext(), FirstScreenActivity.class);
                        intentToFirstScreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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

}