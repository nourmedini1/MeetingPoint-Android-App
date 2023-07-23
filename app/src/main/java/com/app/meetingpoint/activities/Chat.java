package com.app.meetingpoint.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;

import com.app.meetingpoint.R;
import com.app.meetingpoint.adapters.ChatMessageAdapter;
import com.app.meetingpoint.databinding.ActivityChatBinding;
import com.app.meetingpoint.helpers.Constants;
import com.app.meetingpoint.helpers.PrefrencesManager;
import com.app.meetingpoint.models.ChatMessage;
import com.app.meetingpoint.models.Conversation;
import com.app.meetingpoint.models.Group;
import com.app.meetingpoint.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.prefs.PreferenceChangeEvent;

public class Chat extends BaseActivity {
    ActivityChatBinding binding ;
    String conversationId ;
    Conversation conversation ;
    ArrayList<ChatMessage> chatMessages ;
    ChatMessageAdapter chatMessageAdapter ;
    boolean isAvailable,doo=false ;
    String firstParty , secondParty ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        PrefrencesManager prefrencesManager = new PrefrencesManager(getApplicationContext());
        chatMessages = new ArrayList<>();
        chatMessageAdapter = new ChatMessageAdapter(getApplicationContext(),chatMessages);
        binding.chatRecycler.setAdapter(chatMessageAdapter);
        conversation = (Conversation) getIntent().getSerializableExtra("conversation");
        firstParty = conversation.getFirstParty();
        secondParty = conversation.getSecondParty();
        if(prefrencesManager.getString(Constants.ATTRIBUTE_USERS_ID_KEY).equals(firstParty)){
            System.out.println("oncreate firstparty");
            listenAvailability(secondParty);
        }
        else if (prefrencesManager.getString(Constants.ATTRIBUTE_USERS_ID_KEY).equals(secondParty)){
            System.out.println("on create firstParty");
            listenAvailability(firstParty);
        }

        doo = true;
        conversationId = conversation.getConversationId();
        Group group = null ;
        group = (Group) getIntent().getSerializableExtra("group");
        loadConversationData(prefrencesManager,conversation,group);


        listenMessages();
        binding.chatSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(prefrencesManager);
            }
        });
        binding.chatBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),ChatList.class);
                startActivity(intent);
                finish();
            }
        });

    }
    private void listenAvailability(String user){
        if(user != null){

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection(Constants.COLLECTION_USERS_KEY)
                    .document(user)
                    .addSnapshotListener(Chat.this,(value, error) -> {
                        if(error != null){
                            return ;
                        }
                        if(value != null){
                            if(value.getBoolean(Constants.ATTRIBUTR_USERS_AVAILABLE_KEY) != null){
                                isAvailable = Objects.requireNonNull(value.getBoolean(Constants.ATTRIBUTR_USERS_AVAILABLE_KEY));
                            }
                        }
                        if(isAvailable){
                            System.out.println("i am available");
                            binding.availability.setVisibility(View.VISIBLE);
                        }
                        else {
                            binding.availability.setVisibility(View.GONE);
                        }
                    });
        }


    }
    private void listenMessages(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.COLLECTION_CHAT_MESSAGE_KEY)
                .whereEqualTo(Constants.ATTRIBUTE_CHAT_MESSAGE_CONVERSATION_ID_KEY,conversationId)
                .addSnapshotListener(eventListener);
    }
    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if(error != null){
            return ;
        }
        if(value != null){
            int count = chatMessages.size();
            for(DocumentChange documentChange : value.getDocumentChanges()){
                if(documentChange.getType() == DocumentChange.Type.ADDED){

                    ChatMessage chatMessage = documentChange.getDocument().toObject(ChatMessage.class);
                    chatMessage.setMessageId(documentChange.getDocument().getId());

                    chatMessages.add(chatMessage);
                }

            }
            chatMessages.sort(((o1, o2) -> o1.getTimestamp().compareTo(o2.getTimestamp())));
            if(count == 0){

                chatMessageAdapter.notifyDataSetChanged();

            }
            else{

                chatMessageAdapter.notifyItemRangeInserted(chatMessages.size(),chatMessages.size());
                binding.chatRecycler.smoothScrollToPosition(chatMessages.size()-1);


            }
            binding.chatRecycler.setVisibility(View.VISIBLE);

        }
    };
    private void loadConversationData(PrefrencesManager prefrencesManager,Conversation conversation,Group group){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        if(conversation.getGroup().equals("")){
            if(conversation.getFirstParty().equals(prefrencesManager.getString(Constants.ATTRIBUTE_USERS_ID_KEY))){
                db.collection(Constants.COLLECTION_USERS_KEY)
                        .document(conversation.getSecondParty())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()){
                                    User user = task.getResult().toObject(User.class);
                                    assert user != null;
                                    binding.chatUsername.setText(user.getUsername());
                                    byte[] bytes = Base64.decode(user.getImage(),Base64.DEFAULT);
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0, bytes.length);
                                    binding.userImg.setImageBitmap(bitmap);
                                }
                            }
                        });
            }
            else{
                db.collection(Constants.COLLECTION_USERS_KEY)
                        .document(conversation.getFirstParty())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()){
                                    User user = task.getResult().toObject(User.class);
                                    assert user != null;
                                    binding.chatUsername.setText(user.getUsername());

                                    byte[] bytes = Base64.decode(user.getImage(),Base64.DEFAULT);
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0, bytes.length);
                                    binding.userImg.setImageBitmap(bitmap);
                                }
                            }
                        });
            }
        }
        else {
            byte[] bytes = Base64.decode(group.getGroupImage(),Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0, bytes.length);
            binding.userImg.setImageBitmap(bitmap);
            binding.chatUsername.setText(group.getGroupName());
        }

    }
    private void sendMessage(PrefrencesManager prefrencesManager){
        HashMap<String,Object> message = new HashMap<>();
        Date date = new Date();
        message.put(Constants.ATTRIBUTE_CHAT_MESSAGE_SENDER_ID_KEY,prefrencesManager.getString(Constants.ATTRIBUTE_USERS_ID_KEY));
        message.put(Constants.ATTRIBUTE_CHAT_MESSAGE_SENDER_NAME_KEY,prefrencesManager.getString(Constants.ATTRIBUTE_USERS_USERNAME_KEY));
        message.put(Constants.ATTRIBUTE_CHAT_MESSAGE_CONVERSATION_ID_KEY,conversationId);
        message.put(Constants.ATTRIBUTE_CHAT_MESSAGE_IMAGE_KEY,"");
        message.put(Constants.ATTRIBUTE_CHAT_MESSAGE_MESSAGE_BODY_KEY,binding.msgEdittext.getText().toString().trim());
        message.put(Constants.ATTRIBUTE_CHAT_MESSAGE_TIMESTAMP_KEY,date);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.COLLECTION_CHAT_MESSAGE_KEY).add(message);
        HashMap<String,Object> conversation = new HashMap<>();
        conversation.put(Constants.ATTRIBUTE_CONVERSATIONS_LAST_MESSAGE_SENDER_KEY,
                prefrencesManager.getString(Constants.ATTRIBUTE_USERS_ID_KEY));
        conversation.put(Constants.ATTRIBUTE_CONVERSATIONS_LAST_MESSAGE_KEY,binding.msgEdittext.getText().toString().trim());
        conversation.put(Constants.ATTRIBUTE_CONVERSATIONS_TIMESTAMP_KEY,date);
        conversation.put(Constants.ATTRIBUTE_CONVERSATIONS_SEEN_KEY,false);
        db.collection(Constants.COLLECTION_CONVERSATIONS_KEY).document(conversationId)
                        .update(conversation);
        binding.msgEdittext.setText(null);



    }

    @Override
    protected void onResume() {
        super.onResume();
        if(doo){
            PrefrencesManager prefrencesManager = new PrefrencesManager(getApplicationContext());
            if(prefrencesManager.getString(Constants.ATTRIBUTE_USERS_ID_KEY).equals(firstParty)){
                System.out.println("on resume first party");
                listenAvailability(secondParty);
            }
            else if(prefrencesManager.getString(Constants.ATTRIBUTE_USERS_ID_KEY).equals(secondParty)){
                System.out.println("on resume second party");
                listenAvailability(firstParty);
            }
        }

        }

}