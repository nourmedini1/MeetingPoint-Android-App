package com.app.meetingpoint.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.app.meetingpoint.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class GroupsShowerAdapter extends RecyclerView.Adapter<GroupsShowerAdapter.ViewHolderGroupShower> {


        private Context context;
        private ArrayList<Group> groups;
        public GroupsShowerAdapter(Context context,ArrayList<Group> groups){
            this.context=context;
            this.groups=groups;
        }

        @NonNull
        @Override
        public ViewHolderGroupShower onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(context).inflate(R.layout.item_groups_home,parent,false);
            return new ViewHolderGroupShower(view);
        }

    @Override
    public void onBindViewHolder(@NonNull GroupsShowerAdapter.ViewHolderGroupShower holder, int position) {
        PrefrencesManager prefrencesManager = new PrefrencesManager(context);
        Group group=groups.get(position%groups.size());
        byte[] bytes  = Base64.decode(group.getGroupImage(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        holder.groupImage.setImageBitmap(decodedByte);
        holder.groupName.setText(group.getGroupName());
        switch (group.getGroupRating()){
            case 0 :
                holder.rating.setAnimation(R.raw.stars0);
                break;
            case 1 :
                holder.rating.setAnimation(R.raw.stars1);
                break;
            case 2 :
                holder.rating.setAnimation(R.raw.stars2);
                break;
            case 3 :
                holder.rating.setAnimation(R.raw.stars3);
                break;
            case 4 :
                holder.rating.setAnimation(R.raw.stars4);
                break;
            case 5 :
                holder.rating.setAnimation(R.raw.stars5);
                break;
        }
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        joinHandler(database,prefrencesManager.getString(Constants.ATTRIBUTE_USERS_ID_KEY),holder,group);
        }
    private void joinHandler(FirebaseFirestore database, String userId,ViewHolderGroupShower holder,Group group) {

        database.collection(Constants.COLLECTION_USERS_KEY)
                .document(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        User user = documentSnapshot.toObject(User.class);
                        assert user != null;
                        if (user.getGroups().contains(group.getId())){
                            holder.joinTxt.setVisibility(View.VISIBLE);
                            holder.joinButton.setVisibility(View.INVISIBLE);
                        }
                        else {
                            holder.joinTxt.setVisibility(View.INVISIBLE);
                            holder.joinButton.setVisibility(View.VISIBLE);
                            holder.joinButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    List<String> updatedGroupsList = user.getGroups();
                                    updatedGroupsList.add(group.getId());
                                    database.collection(Constants.COLLECTION_USERS_KEY)
                                            .document(userId)
                                            .update(Constants.ATTRIBUTE_USERS_GROUPS_KEY,updatedGroupsList)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(context, "joined "+group.getGroupName(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            });
                        }
                    }
                });


    }





        @Override
        public int getItemCount() {
            return groups==null?0:Integer.MAX_VALUE;
        }





        public static class ViewHolderGroupShower extends RecyclerView.ViewHolder{
            ImageView groupImage ;
            AppCompatTextView  groupName ;
            AppCompatButton joinButton ;
            AppCompatTextView joinTxt ;
            LottieAnimationView rating ;
            public ViewHolderGroupShower(@NonNull View itemView) {
                super(itemView);
                groupImage = itemView.findViewById(R.id.group_img);
                groupName = itemView.findViewById(R.id.group_name_txt);
                joinButton = itemView.findViewById(R.id.join_button);
                joinTxt = itemView.findViewById(R.id.joined_txt);
                rating = itemView.findViewById(R.id.rating);
            }

        }
}
