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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ChooseFavoriteTopicsAdapter extends RecyclerView.Adapter<ChooseFavoriteTopicsAdapter.ViewHolderGroupShower> {


    private final Context context;

    private final ArrayList<Topic> topics;
    public ChooseFavoriteTopicsAdapter(Context context,ArrayList<Topic> topics){
        this.context=context;
        this.topics=topics;
    }

    @NonNull
    @Override
    public ViewHolderGroupShower onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.user_profile_my_favorite_topics,parent,false);
        return new ViewHolderGroupShower(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChooseFavoriteTopicsAdapter.ViewHolderGroupShower holder, int position) {
        PrefrencesManager prefrencesManager = new PrefrencesManager(context);
        Topic topic=topics.get(position);
        holder.topic.setText(topic.getTopic());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> userTopicsList ;
                if(prefrencesManager.getString(Constants.ATTRIBUTE_USERS_FAVORITE_TOPICS_KEY).length()>0) {
                    String[] userTopics = prefrencesManager.getString(Constants.ATTRIBUTE_USERS_FAVORITE_TOPICS_KEY).split(",");
                    userTopicsList = new ArrayList<>(Arrays.asList(userTopics));
                    if (userTopicsList.contains(topic.getTopicId())) {
                        Toast.makeText(context, "you already follow " + topic.getTopic(), Toast.LENGTH_SHORT).show();
                    } else {
                        userTopicsList.add(topic.getTopicId());
                        String tpcs = "";
                        for (String tp : userTopicsList) {
                            tpcs += tp + ",";
                        }
                        tpcs = tpcs.substring(0, tpcs.length() - 1);
                        prefrencesManager.putString(Constants.ATTRIBUTE_USERS_FAVORITE_TOPICS_KEY,tpcs);
                        FirebaseFirestore database = FirebaseFirestore.getInstance();
                        database.collection(Constants.COLLECTION_USERS_KEY)
                                .document(prefrencesManager.getString(Constants.ATTRIBUTE_USERS_ID_KEY))
                                .update(Constants.ATTRIBUTE_USERS_FAVORITE_TOPICS_KEY,userTopicsList)
                                .addOnCompleteListener(task ->
                                        Toast.makeText(context, "you are now following "+topic.getTopic(), Toast.LENGTH_SHORT).show());
                    }
                }
                else{
                    userTopicsList = new ArrayList<>();
                    userTopicsList.add(topic.getTopic());
                    String tpcs = topic.getTopicId();
                    prefrencesManager.putString(Constants.ATTRIBUTE_USERS_FAVORITE_TOPICS_KEY,tpcs);
                    FirebaseFirestore database = FirebaseFirestore.getInstance();
                    database.collection(Constants.COLLECTION_USERS_KEY)
                            .document(prefrencesManager.getString(Constants.ATTRIBUTE_USERS_ID_KEY))
                            .update(Constants.ATTRIBUTE_USERS_FAVORITE_TOPICS_KEY,userTopicsList)
                            .addOnCompleteListener(task ->
                                    Toast.makeText(context, "You are now following "+topic.getTopic(),
                                            Toast.LENGTH_SHORT).show());}
            }

        });
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


