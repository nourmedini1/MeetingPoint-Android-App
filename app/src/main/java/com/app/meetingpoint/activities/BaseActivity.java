package com.app.meetingpoint.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.app.meetingpoint.helpers.Constants;
import com.app.meetingpoint.helpers.PrefrencesManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class BaseActivity extends AppCompatActivity {
    DocumentReference documentReference ;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PrefrencesManager prefrencesManager = new PrefrencesManager(getApplicationContext());
        documentReference = FirebaseFirestore.getInstance().collection(Constants.COLLECTION_USERS_KEY)
                .document(prefrencesManager.getString(Constants.ATTRIBUTE_USERS_ID_KEY));



    }

    @Override
    protected void onResume() {
        super.onResume();
        documentReference.update(Constants.ATTRIBUTR_USERS_AVAILABLE_KEY,true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        documentReference.update(Constants.ATTRIBUTR_USERS_AVAILABLE_KEY,false);
    }
}
