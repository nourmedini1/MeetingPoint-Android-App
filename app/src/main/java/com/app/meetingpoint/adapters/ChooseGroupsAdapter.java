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

public class ChooseGroupsAdapter extends RecyclerView.Adapter<ChooseGroupsAdapter.ViewHolderGroupShower> {


    private final Context context;

    private final ArrayList<Group> groups;
    public ChooseGroupsAdapter(Context context,ArrayList<Group> groups){
        this.context=context;
        this.groups=groups;
    }

    @NonNull
    @Override
    public ViewHolderGroupShower onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.choose_groups_item,parent,false);
        return new ViewHolderGroupShower(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChooseGroupsAdapter.ViewHolderGroupShower holder, int position) {
        PrefrencesManager prefrencesManager = new PrefrencesManager(context);
        Group group=groups.get(position);
        byte[] bytes  = Base64.decode(group.getGroupImage(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        holder.groupImage.setImageBitmap(decodedByte);
        holder.groupName.setText(group.getGroupName());
        holder.description.setText(group.getDescription());
        int noUsers  = group.getNumberOfUsers();
        String val = String.valueOf(noUsers);
        String toPut = null ;
        if(noUsers< 1000){
            toPut = "0."+val.charAt(0)+"k";
        }
        else if (noUsers<10000){
            toPut = val.charAt(0)+"."+val.charAt(1)+"k";
        }
        else if (noUsers<100000){
            toPut = val.charAt(0)+val.charAt(1)+"."+val.charAt(2)+"k";
        }
        holder.groupMembers.setText(toPut);
        switch (group.getGroupRating()){
            case 0 :
                holder.ratingAnimation.setAnimation(R.raw.stars0);
                break;
            case 1 :
                holder.ratingAnimation.setAnimation(R.raw.stars1);
                break;
            case 2 :
                holder.ratingAnimation.setAnimation(R.raw.stars2);
                break;
            case 3 :
                holder.ratingAnimation.setAnimation(R.raw.stars3);
                break;
            case 4 :
                holder.ratingAnimation.setAnimation(R.raw.stars4);
                break;
            case 5 :
                holder.ratingAnimation.setAnimation(R.raw.stars5);
                break;
        }
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] userGroups = prefrencesManager.getString(Constants.ATTRIBUTE_USERS_GROUPS_KEY).split(",");
                ArrayList<String> userGroupsList = new ArrayList<>(Arrays.asList(userGroups));
                if(userGroupsList.contains(group.getId())){
                    Toast.makeText(context, "you are already in "+group.getGroupName(), Toast.LENGTH_SHORT).show();
                }
                else {
                    userGroupsList.add(group.getId());
                    String groups = "";
                    for (String gr: userGroupsList) {
                        groups+=gr+",";
                    }

                    groups=groups.substring(0,groups.length()-1);
                    prefrencesManager.putString(Constants.ATTRIBUTE_USERS_GROUPS_KEY,groups);
                    FirebaseFirestore database = FirebaseFirestore.getInstance();
                    database.collection(Constants.COLLECTION_USERS_KEY)
                            .document(prefrencesManager.getString(Constants.ATTRIBUTE_USERS_ID_KEY))
                            .update(Constants.ATTRIBUTE_USERS_GROUPS_KEY,userGroupsList)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(context, "successfully joined "+group.getGroupName(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

    }





    @Override
    public int getItemCount() {
        return groups.size();
    }



    public static class ViewHolderGroupShower extends RecyclerView.ViewHolder{
        ImageView groupImage ;
        TextView groupName ;
        TextView groupMembers ;
        TextView description ;
        LottieAnimationView ratingAnimation;
        AppCompatButton button;
        public ViewHolderGroupShower(@NonNull View itemView) {
            super(itemView);
            button = itemView.findViewById(R.id.join_group_button);
            groupImage = itemView.findViewById(R.id.choose_groups_item_image);
            groupName = itemView.findViewById(R.id.choose_groups_item_name);
            groupMembers = itemView.findViewById(R.id.choose_groups_item_nousers);
            ratingAnimation = itemView.findViewById(R.id.choose_groups_item_rating);
            description = itemView.findViewById(R.id.choose_groups_item_description);
        }

    }

}


