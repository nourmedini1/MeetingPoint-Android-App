package com.app.meetingpoint.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.meetingpoint.R;
import com.app.meetingpoint.interfaces.RecyclerViewInterface;
import com.app.meetingpoint.models.User;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class GroupMembersAdapter extends RecyclerView.Adapter<GroupMembersAdapter.GroupMembersViewHolder> {
    private Context context ;
    private ArrayList<User> users ;
    private RecyclerViewInterface recyclerViewInterface;

    public GroupMembersAdapter(Context context, ArrayList<User> users, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.users = users;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public GroupMembersAdapter.GroupMembersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.group_member_item,parent,false);
        return new GroupMembersAdapter.GroupMembersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupMembersAdapter.GroupMembersViewHolder holder, int position) {
        User user = users.get(position);
        byte[] bytes = Base64.decode(user.getImage(),Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        holder.imageView.setImageBitmap(bitmap);
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recyclerViewInterface != null){
                    int position = holder.getBindingAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        recyclerViewInterface.onItemClick(position,"userToCheck", user.getUserId());
                    }
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return users.size();
    }
    public static class GroupMembersViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView imageView;
        public GroupMembersViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.memeber_image);
        }
    }
}
