package com.app.meetingpoint.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.app.meetingpoint.R;
import com.app.meetingpoint.helpers.Constants;
import com.app.meetingpoint.helpers.PrefrencesManager;
import com.app.meetingpoint.models.Group;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class UserProfileMyChatsAdapter extends RecyclerView.Adapter<UserProfileMyChatsAdapter.ViewHolderGroupShower> {


    private Context context;
    private ArrayList<Group> groups;
    public UserProfileMyChatsAdapter(Context context,ArrayList<Group> groups){
        this.context=context;
        this.groups=groups;
    }

    @NonNull
    @Override
    public ViewHolderGroupShower onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.user_profile_my_chats_item,parent,false);
        return new ViewHolderGroupShower(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserProfileMyChatsAdapter.ViewHolderGroupShower holder, int position) {


    }





    @Override
    public int getItemCount() {
        return 0 ;
    }





    public static class ViewHolderGroupShower extends RecyclerView.ViewHolder{

        LottieAnimationView ratingAnimation;
        public ViewHolderGroupShower(@NonNull View itemView) {
            super(itemView);

        }

    }
}

