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
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.meetingpoint.R;
import com.app.meetingpoint.helpers.Constants;
import com.app.meetingpoint.helpers.PrefrencesManager;
import com.app.meetingpoint.models.ChatMessage;

import java.util.ArrayList;
import java.util.Objects;

public class ChatMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context ;
    private ArrayList<ChatMessage> chatMessages ;
    private final int RECEIVED_MESSAGE_VIEW_TYPE = 2 ;
    private final int SENT_MESSAGE_VIEW_TYPE = 1 ;


    public ChatMessageAdapter(Context context, ArrayList<ChatMessage> chatMessages) {
        this.context = context;
        this.chatMessages = chatMessages;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == SENT_MESSAGE_VIEW_TYPE){

            View view= LayoutInflater.from(context).inflate(R.layout.sended_message_item,parent,false);
            return new ChatMessageAdapter.SentMessageViewHolder(view);
        }
        else {

            View view= LayoutInflater.from(context).inflate(R.layout.received_message_item,parent,false);
            return new ChatMessageAdapter.ReceivedMessageViewHolder(view);

        }
    }

    @Override
    public int getItemViewType(int position) {
        PrefrencesManager prefrencesManager = new PrefrencesManager(context);
        ChatMessage chatMessage = chatMessages.get(position);

        if(Objects.equals(chatMessage.getSenderId(), prefrencesManager.getString(Constants.ATTRIBUTE_USERS_ID_KEY))){
            return SENT_MESSAGE_VIEW_TYPE;
        }
        else{
            return RECEIVED_MESSAGE_VIEW_TYPE ;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage chatMessage = chatMessages.get(position);
        if(getItemViewType(position) == SENT_MESSAGE_VIEW_TYPE){
            if(chatMessage.getImage().equals("")){
                ((SentMessageViewHolder) holder).message.setText(chatMessage.getMessageBody());
            }
            else {
                byte[] bytes = Base64.decode(chatMessage.getImage(),Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0, bytes.length);
                ((SentMessageViewHolder) holder).imageView.setImageBitmap(bitmap);
            }

            ((SentMessageViewHolder) holder).timestamp.setText(chatMessage.getDate());
        }
        else{
            if(chatMessage.getImage().equals("")){
                ((ReceivedMessageViewHolder) holder).message.setText(chatMessage.getMessageBody());
            }
            else {
                byte[] bytes = Base64.decode(chatMessage.getImage(),Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0, bytes.length);
                ((ReceivedMessageViewHolder) holder).imageView.setImageBitmap(bitmap);
            }

            ((ReceivedMessageViewHolder) holder).timestamp.setText(chatMessage.getDate());
            ((ReceivedMessageViewHolder) holder).username.setText(chatMessage.getSenderName());
        }

    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }
    public static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        TextView message,timestamp;
        AppCompatImageView imageView ;

        public SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.sent_text_message_text);
            timestamp = itemView.findViewById(R.id.sent_text_message_timestamp);
            imageView = itemView.findViewById(R.id.sent_text_message_img);
        }
    }
    public static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder{
        TextView username,message,timestamp ;
        AppCompatImageView imageView ;

        public ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.received_message_text);
            timestamp = itemView.findViewById(R.id.received_message_timestamp);
            username = itemView.findViewById(R.id.received_message_receiver);
            imageView = itemView.findViewById(R.id.received_message_img);

        }
    }
}
