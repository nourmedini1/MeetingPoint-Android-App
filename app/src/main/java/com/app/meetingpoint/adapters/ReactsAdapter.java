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
import com.app.meetingpoint.models.React;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class ReactsAdapter extends RecyclerView.Adapter<ReactsAdapter.ReactsViewHolder> {
    Context context ;
    ArrayList<React> reacts ;
    RecyclerViewInterface recyclerViewInterface ;

    public ReactsAdapter(Context context, ArrayList<React> reacts, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.reacts = reacts;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public ReactsAdapter.ReactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.react_item,parent,false);
        return new ReactsAdapter.ReactsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReactsAdapter.ReactsViewHolder holder, int position) {
        React react = reacts.get(position);
        byte[] bytes = Base64.decode(react.getReactorImage(),Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        holder.imageView.setImageBitmap(bitmap);
        holder.textView.setText(react.getReactorUserName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recyclerViewInterface != null){
                    int position = holder.getBindingAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        recyclerViewInterface.onItemClick(position,"reacts",react.getReactorId());
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return reacts.size();
    }
    public static class ReactsViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView imageView ;
        TextView textView ;

        public ReactsViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.react_user_image);
            textView = itemView.findViewById(R.id.react_user_name);
        }
    }
}
