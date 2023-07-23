package com.app.meetingpoint.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.meetingpoint.R;
import com.app.meetingpoint.interfaces.RecyclerViewInterface;
import com.app.meetingpoint.models.Comment;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentViewHolder> {
    Context context;
    ArrayList<Comment> comments ;
    RecyclerViewInterface recyclerViewInterface ;

    public CommentsAdapter(Context context, ArrayList<Comment> comments, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.comments = comments;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public CommentsAdapter.CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.comment_item,parent,false);
        return new CommentsAdapter.CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentsAdapter.CommentViewHolder holder, int position) {
        Comment comment = comments.get(position);
        byte[] bytes  = android.util.Base64.decode(comment.getUserImage(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        holder.userImage.setImageBitmap(decodedByte);
        holder.userName.setText(comment.getUserName());
        holder.commentBody.setText(comment.getComment());
        holder.userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recyclerViewInterface != null){
                    int position = holder.getBindingAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        recyclerViewInterface.onItemClick(position,"userId",comment.getUserId());
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }
    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView userImage ;
        TextView userName ,commentBody;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.comment_user_image);
            userName = itemView.findViewById(R.id.comment_user_name);
            commentBody = itemView.findViewById(R.id.comment_comment);
        }
    }
}
