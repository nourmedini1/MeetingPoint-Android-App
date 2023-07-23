package com.app.meetingpoint.adapters;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.app.meetingpoint.R;
import com.app.meetingpoint.helpers.Constants;
import com.app.meetingpoint.helpers.PrefrencesManager;
import com.app.meetingpoint.interfaces.RecyclerViewInterface;
import com.app.meetingpoint.models.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.HashMap;

public class GroupPostsAdapter extends RecyclerView.Adapter<GroupPostsAdapter.GroupPostsViewHolder> {
    ArrayList<Post> posts ;
    Context context ;
    RecyclerViewInterface recyclerViewInterface ;


    public GroupPostsAdapter(ArrayList<Post> posts, Context context,RecyclerViewInterface recyclerViewInterface) {
        this.posts = posts;
        this.context = context;
        this.recyclerViewInterface = recyclerViewInterface ;
    }

    @NonNull
    @Override
    public GroupPostsAdapter.GroupPostsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.post_item,parent,false);
        return new GroupPostsAdapter.GroupPostsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupPostsAdapter.GroupPostsViewHolder holder, int position) {
        PrefrencesManager prefrencesManager = new PrefrencesManager(context);
        Post post = posts.get(position);
        holder.caption.setText(post.getCaption());
        if(!(post.getImage() == "")|| post.getImage() == null){
            byte[] bytes  = Base64.decode(post.getImage(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            holder.postImage.setImageBitmap(decodedByte);
            holder.postImage.setVisibility(View.VISIBLE);
        }
        else {
            holder.postImage.setVisibility(View.GONE);
        }
        if(post.getSharerName().equals("")){
            holder.sharerName.setVisibility(View.GONE);
            holder.shareIn.setVisibility(View.GONE);
            holder.shareGroup.setVisibility(View.GONE);
        }
        else{
            holder.sharerName.setText(post.getSharerName());
            holder.shareGroup.setText(post.getShareGroupName());
        }
        holder.dateAndTime.setText(post.getPostDate());
        byte[] userBytes  = Base64.decode(post.getPosterImage(), Base64.DEFAULT);
        Bitmap decodeByteArray = BitmapFactory.decodeByteArray(userBytes, 0, userBytes.length);
        holder.posterImage.setImageBitmap(decodeByteArray);
        holder.posterName.setText(post.getPosterName());

        if(post.getPostVisibility().equals("public")){
            holder.visibility.setImageResource(R.drawable.ic_baseline_cell_tower_24);
        }
        else{
            holder.visibility.setImageResource(R.drawable.ic_baseline_lock_person_24);
        }
        if(post.getGroupName().equals("")){
            holder.groupName.setVisibility(View.GONE);
            holder.in.setVisibility(View.GONE);
        }
        else{
            holder.groupName.setText(post.getGroupName());
        }
        holder.nbShares.setText(setupNumber(post.getNbShares()));
        holder.nbReacts.setText(setupNumber(post.getNbReacts()));
        holder.nbComments.setText(setupNumber(post.getNbComments()));
        holder.comments.setAnimation(R.raw.comment);
        holder.reacts.setAnimation(R.raw.heart_react);
        holder.shares.setAnimation(R.raw.share);
        holder.react.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!post.getReacts().contains(prefrencesManager.getString(Constants.ATTRIBUTE_USERS_ID_KEY))){
                    ArrayList<String> newReacts ;
                    newReacts = post.getReacts();
                    newReacts.add(prefrencesManager.getString(Constants.ATTRIBUTE_USERS_ID_KEY));
                    post.setReacts(newReacts);
                    int nbrec = post.getNbReacts()+1;
                    post.setNbReacts(nbrec);
                    HashMap<String,Object> hashMap = new HashMap<>();
                    hashMap.put(Constants.ATTRIBUTE_POST_NB_REACTS_KEY,nbrec);
                    hashMap.put(Constants.ATTRIBUTE_POST_REACTS_KEY,newReacts);
                    FirebaseFirestore database = FirebaseFirestore.getInstance();
                    database.collection(Constants.COLLECTION_POST_KEY)
                            .document(post.getPostId())
                            .update(hashMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                }
                            });

                }



            }
        });
        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recyclerViewInterface != null){
                    int position = holder.getBindingAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        recyclerViewInterface.onItemClick(position,"share",post.getPostId());
                    }
                }

            }
        });
        holder.reacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recyclerViewInterface != null){
                    int position = holder.getBindingAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        recyclerViewInterface.onItemClick(position,"reacts",post.getPostId());
                    }
                }
            }

        });
        holder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recyclerViewInterface != null){
                    int position = holder.getBindingAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        recyclerViewInterface.onItemClick(position,"comments",post.getPostId());
                    }
                }
            }
        });
        holder.shareGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recyclerViewInterface != null){
                    int position = holder.getBindingAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        recyclerViewInterface.onItemClick(position,"group",posts.get(position).getShareGroup());
                    }
                }
            }
        });
        holder.sharerName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recyclerViewInterface != null){
                    int position = holder.getBindingAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        recyclerViewInterface.onItemClick(position,"userProfile",posts.get(position).getSharerId());
                    }
                }
            }
        });
        holder.posterImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recyclerViewInterface != null){
                    int position = holder.getBindingAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        recyclerViewInterface.onItemClick(position,"userProfile",posts.get(position).getPosterId());
                    }
                }
            }
        });
        holder.groupName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recyclerViewInterface != null){
                    int position = holder.getBindingAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        recyclerViewInterface.onItemClick(position,"group",posts.get(position).getGroupId());
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public static class GroupPostsViewHolder extends RecyclerView.ViewHolder {
        ShapeableImageView postImage ;
        TextView posterName ;
        TextView dateAndTime ;
        TextView caption ;
        ImageView visibility ;
        RoundedImageView posterImage ;
        LottieAnimationView shares,comments,reacts;
        TextView nbShares,nbComments,nbReacts,react,share,comment;
        TextView in ;
        TextView groupName ,sharerName , shareGroup , shareIn;

        public GroupPostsViewHolder(@NonNull View itemView) {
            super(itemView);
            posterImage = itemView.findViewById(R.id.post_poster_image);
            postImage = itemView.findViewById(R.id.post_image);
            posterName = itemView.findViewById(R.id.post_poster_name);
            dateAndTime = itemView.findViewById(R.id.post_date_time);
            caption = itemView.findViewById(R.id.post_caption);
            visibility = itemView.findViewById(R.id.post_visibility);
            shares = itemView.findViewById(R.id.post_shares);
            comments = itemView.findViewById(R.id.post_comments);
            reacts = itemView.findViewById(R.id.post_heart_reacts);
            nbComments = itemView.findViewById(R.id.nb_comments);
            nbReacts = itemView.findViewById(R.id.nb_heart_reacts);
            nbShares = itemView.findViewById(R.id.nb_shares);
            share = itemView.findViewById(R.id.share);
            comment = itemView.findViewById(R.id.comment);
            react = itemView.findViewById(R.id.heart_react);
            in = itemView.findViewById(R.id.in);
            groupName = itemView.findViewById(R.id.post_group_name);
            shareGroup = itemView.findViewById(R.id.post_share_group_name);
            shareIn = itemView.findViewById(R.id.shared_this);
            sharerName = itemView.findViewById(R.id.post_sharer_name);


        }

    }
    private String setupNumber(int noUsers){

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
        return toPut;
    }
}
