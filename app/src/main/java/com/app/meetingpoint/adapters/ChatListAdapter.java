package com.app.meetingpoint.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.meetingpoint.R;
import com.app.meetingpoint.activities.Chat;
import com.app.meetingpoint.helpers.Constants;
import com.app.meetingpoint.helpers.PrefrencesManager;
import com.app.meetingpoint.interfaces.RecyclerViewInterface;
import com.app.meetingpoint.models.Conversation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder> {
    private Context context ;
    private ArrayList<Conversation> conversations ;
    private RecyclerViewInterface recyclerViewInterface ;

    public ChatListAdapter(Context context, ArrayList<Conversation> conversations, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.conversations = conversations;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public ChatListAdapter.ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        System.out.println("view created");
        View view= LayoutInflater.from(context).inflate(R.layout.recent_convo_item,parent,false);
        return new ChatListAdapter.ChatListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatListAdapter.ChatListViewHolder holder, int position) {
        System.out.println("view binding successful");
        PrefrencesManager prefrencesManager = new PrefrencesManager(context);

        Conversation conversation = conversations.get(position);
        String userId ;
        if(prefrencesManager.getString(Constants.ATTRIBUTE_USERS_ID_KEY).equals(conversation.getFirstParty())){
            userId = conversation.getSecondParty();
        }
        else {
            userId = conversation.getFirstParty();
        }
        CollectionReference collection= FirebaseFirestore.getInstance().collection(Constants.COLLECTION_USERS_KEY);

            collection.document(userId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                String image = task.getResult().getString(Constants.ATTRIBUTE_USERS_IMAGE_KEY);
                                String username = task.getResult().getString(Constants.ATTRIBUTE_USERS_USERNAME_KEY);
                                byte[] bytes = Base64.decode(image,Base64.DEFAULT);
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0, bytes.length);
                                holder.imageView.setImageBitmap(bitmap);
                                holder.username.setText(username);
                                String toShow = "";
                                if(conversation.getLastMessageSender()
                                        .equals(prefrencesManager.getString(Constants.ATTRIBUTE_USERS_ID_KEY))){
                                    toShow = "You: ";
                                    if(conversation.getLastMessage().trim().length()<15){
                                        toShow = toShow+conversation.getLastMessage().trim();
                                    }
                                    else{
                                        toShow+= conversation.getLastMessage().trim().substring(0,16)+"...";
                                    }
                                }
                                else {
                                    if(!conversation.isSeen()){
                                        holder.text.setTextColor(Color.parseColor("#000000"));
                                    }
                                    if(conversation.getLastMessage().trim().length()<15){
                                        toShow = toShow+conversation.getLastMessage().trim();
                                    }
                                    else{
                                        toShow+= conversation.getLastMessage().trim().substring(0,16)+"...";
                                    }

                                }
                                holder.text.setText(toShow);
                                holder.datetime.setText(conversation.getDate());

                            }


                        }
                    });
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    db.collection(Constants.COLLECTION_CONVERSATIONS_KEY)
                            .document(conversation.getConversationId())
                            .update(Constants.ATTRIBUTE_CONVERSATIONS_SEEN_KEY,true)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Intent intent = new Intent(context, Chat.class);
                                    intent.putExtra("conversation",conversation);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                                    context.startActivity(intent);
                                }
                            });



                }
            });


    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }
    public static class ChatListViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView imageView ;
        TextView username ,text , datetime;
        public ChatListViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.recent_convo_item_img);
            username = itemView.findViewById(R.id.recent_convo_item_username);
            text = itemView.findViewById(R.id.recent_convo_item_message);
            datetime = itemView.findViewById(R.id.recent_convo_item_datetime);
        }
    }
}
