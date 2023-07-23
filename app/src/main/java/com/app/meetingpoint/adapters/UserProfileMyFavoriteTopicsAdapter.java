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
import com.app.meetingpoint.models.Topic;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class UserProfileMyFavoriteTopicsAdapter extends RecyclerView.Adapter<UserProfileMyFavoriteTopicsAdapter.ViewHolderGroupShower> {


    private final Context context;
    private final ArrayList<String> topics;

    public UserProfileMyFavoriteTopicsAdapter(Context context, ArrayList<String> topics) {
        this.context = context;
        this.topics = topics;
    }

    @NonNull
    @Override
    public ViewHolderGroupShower onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_profile_my_favorite_topics, parent, false);
        return new ViewHolderGroupShower(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserProfileMyFavoriteTopicsAdapter.ViewHolderGroupShower holder, int position) {
        String topic = topics.get(position );
        holder.topic.setText(topic);
    }


    @Override
    public int getItemCount() {
        return topics.size();
    }


    public static class ViewHolderGroupShower extends RecyclerView.ViewHolder {

        TextView topic;

        public ViewHolderGroupShower(@NonNull View itemView) {
            super(itemView);
            topic = itemView.findViewById(R.id.user_profile_my_favourite_topics_topic);


        }
    }
}


