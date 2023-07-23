package com.app.meetingpoint.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.RecyclerView;

import com.app.meetingpoint.R;
import com.app.meetingpoint.helpers.Constants;

import com.app.meetingpoint.helpers.PrefrencesManager;
import com.app.meetingpoint.models.Group;
import com.app.meetingpoint.models.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.HashMap;

public class SharePostAdapter extends RecyclerView.Adapter<SharePostAdapter.SharePostViewHolder> {
    private Context context ;
    private ArrayList<Group> groups ;
    private String postId ;

    public SharePostAdapter(String postId,Context context, ArrayList<Group> groups) {
        this.context = context;
        this.groups = groups;
        this.postId = postId ;
    }

    @NonNull
    @Override
    public SharePostAdapter.SharePostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.share_post_item,parent,false);
        System.out.println("adapter am in on create View Holder");
        return new SharePostAdapter.SharePostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SharePostAdapter.SharePostViewHolder holder, int position) {
        Group group = groups.get(position);
        byte[] bytes = android.util.Base64.decode(group.getGroupImage(), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        holder.imageView.setImageBitmap(bitmap);
        holder.textView.setText(group.getGroupName());
        holder.button.setVisibility(View.VISIBLE);
        holder.imageView.setVisibility(View.VISIBLE);
        holder.textView.setVisibility(View.VISIBLE);
        System.out.println("the name of the group "+group.getGroupName());
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrefrencesManager prefrencesManager = new PrefrencesManager(context);
                FirebaseFirestore database = FirebaseFirestore.getInstance();
                database.collection(Constants.COLLECTION_POST_KEY)
                        .document(postId)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()){
                                    Post post = task.getResult().toObject(Post.class);
                                    assert post != null;
                                    ArrayList<String> s = post.getShareTo();
                                    if(post.getShareTo().get(0).equals("")){
                                        s.remove(0);
                                    }
                                    String sharerGroup =group.getId();
                                    String sharerName = prefrencesManager.getString(Constants.ATTRIBUTE_USERS_USERNAME_KEY);
                                    String sharerId = prefrencesManager.getString(Constants.ATTRIBUTE_USERS_ID_KEY);
                                    String sharerGroupName = group.getGroupName();
                                    s.add(sharerGroup+Constants.SEPERATOR+sharerName+Constants.SEPERATOR+sharerId+Constants.SEPERATOR+sharerGroupName);
                                    HashMap<String,Object> hashMap = new HashMap<>();
                                    hashMap.put(Constants.ATTRIBUTE_POST_SHARE_TO_KEY,s);
                                    hashMap.put(Constants.ATTRIBUTE_POST_NB_SHARES_KEY,post.getNbShares()+1);
                                    database.collection(Constants.COLLECTION_POST_KEY)
                                            .document(postId)
                                            .update(hashMap)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(context, "post shared to "+group.getGroupName(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            }
                        });
            }
        });

    }

    @Override
    public int getItemCount() {
        return groups.size();
    }
    public static class SharePostViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView imageView;
        TextView textView;
        AppCompatButton button ;
        public SharePostViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.share_post_item_image);
            textView = itemView.findViewById(R.id.share_post_item_txt);
            button = itemView.findViewById(R.id.share_button);
        }

    }
}
