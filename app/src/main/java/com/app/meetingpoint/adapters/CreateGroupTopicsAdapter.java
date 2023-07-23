package com.app.meetingpoint.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.meetingpoint.R;

import com.app.meetingpoint.helpers.Constants;
import com.app.meetingpoint.helpers.PrefrencesManager;
import com.app.meetingpoint.interfaces.RecyclerViewInterface;
import com.app.meetingpoint.models.Topic;


import java.util.ArrayList;

public class CreateGroupTopicsAdapter extends RecyclerView.Adapter<CreateGroupTopicsAdapter.CreateGroupTopicsViewHolder> {
    private final Context context;
    private final RecyclerViewInterface recyclerViewInterface;
    private final ArrayList<Topic> topics;
    public CreateGroupTopicsAdapter(RecyclerViewInterface recyclerViewInterface,Context context,ArrayList<Topic> topics){
        this.recyclerViewInterface = recyclerViewInterface;
        this.context=context;
        this.topics=topics;
    }

    @NonNull
    @Override
    public CreateGroupTopicsAdapter.CreateGroupTopicsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.user_profile_my_favorite_topics,parent,false);
        return new CreateGroupTopicsAdapter.CreateGroupTopicsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CreateGroupTopicsAdapter.CreateGroupTopicsViewHolder holder, int position) {
        PrefrencesManager prefrencesManager = new PrefrencesManager(context);
        Topic topic=topics.get(position);
        holder.topic.setText(topic.getTopic());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recyclerViewInterface != null){
                    int position = holder.getBindingAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        recyclerViewInterface.onItemClick(position,"topic",topic.getTopic()+ Constants.SEPERATOR+topic.getTopicId());

                    }
                }
            }
        });


    }





    @Override
    public int getItemCount() {
        return topics.size();
    }



    public static class CreateGroupTopicsViewHolder extends RecyclerView.ViewHolder {
        TextView topic;

        public CreateGroupTopicsViewHolder(@NonNull View itemView) {
            super(itemView);
            topic = itemView.findViewById(R.id.user_profile_my_favourite_topics_topic);


        }
    }
}
