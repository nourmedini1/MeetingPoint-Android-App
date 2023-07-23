package com.app.meetingpoint.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.meetingpoint.R;
import com.app.meetingpoint.interfaces.RecyclerViewInterface;
import com.app.meetingpoint.models.Group;

import java.util.ArrayList;

public class DrawerGroupsAdapter extends RecyclerView.Adapter<DrawerGroupsAdapter.RecyclerViewHolder> {
    private final RecyclerViewInterface recyclerViewInterface ;
    private ArrayList<Group> groups ;
    public DrawerGroupsAdapter(ArrayList<Group> arrayList , RecyclerViewInterface recyclerViewInterface){
        this.groups = arrayList ;
        this.recyclerViewInterface = recyclerViewInterface ;

    }
    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_group_item,parent,false);
        return new RecyclerViewHolder(view,recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        holder.textView.setText(groups.get(position).getGroupName());
        byte[] bytes  = Base64.decode(groups.get(position).getGroupImage(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        holder.imageView.setImageBitmap(decodedByte);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recyclerViewInterface != null){
                    int position = holder.getBindingAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        recyclerViewInterface.onItemClick(position,"group",groups.get(position).getId());
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }
    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView ;
        TextView textView ;
        public RecyclerViewHolder(@NonNull View itemView,RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_drawer_group);
            textView = itemView.findViewById(R.id.txt_drawer_group);


        }
    }
}
