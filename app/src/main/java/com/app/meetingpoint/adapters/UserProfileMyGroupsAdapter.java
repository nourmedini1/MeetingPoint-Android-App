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
import com.app.meetingpoint.interfaces.RecyclerViewInterface;
import com.app.meetingpoint.models.Group;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class UserProfileMyGroupsAdapter extends RecyclerView.Adapter<UserProfileMyGroupsAdapter.ViewHolderGroupShower> {


    private final Context context;
    private final ArrayList<Group> groups;
    RecyclerViewInterface recyclerViewInterface;
    public UserProfileMyGroupsAdapter(Context context,ArrayList<Group> groups,RecyclerViewInterface recyclerViewInterface){
        this.context=context;
        this.groups=groups;
        this.recyclerViewInterface = recyclerViewInterface ;
    }

    @NonNull
    @Override
    public ViewHolderGroupShower onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.user_profile_my_groups_item,parent,false);
        return new ViewHolderGroupShower(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserProfileMyGroupsAdapter.ViewHolderGroupShower holder, int position) {
        Group group=groups.get(position% groups.size());
        byte[] bytes  = Base64.decode(group.getGroupImage(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        byte[] reshapedBytes = Base64.decode(encodeImage(decodedByte),Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(reshapedBytes,0,reshapedBytes.length);
        holder.groupImage.setImageBitmap(bitmap);
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
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recyclerViewInterface != null){
                    int position = holder.getBindingAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        recyclerViewInterface.onItemClick(position,"group",group.getId());
                    }
                }
            }
        });

    }





    @Override
    public int getItemCount() {
        return groups==null?0:Integer.MAX_VALUE;
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



    public static class ViewHolderGroupShower extends RecyclerView.ViewHolder{
        ImageView groupImage ;
        TextView groupName ;
        TextView groupMembers ;
        TextView description ;
        LottieAnimationView ratingAnimation;
        public ViewHolderGroupShower(@NonNull View itemView) {
            super(itemView);
            groupImage = itemView.findViewById(R.id.user_profile_my_groups_image);
            groupName = itemView.findViewById(R.id.user_profile_my_groups_groupname);
            groupMembers = itemView.findViewById(R.id.user_profile_my_groups_members);
            ratingAnimation = itemView.findViewById(R.id.user_profile_my_groups_rating);
            description = itemView.findViewById(R.id.user_profile_my_groups_description);
        }

    }

}

