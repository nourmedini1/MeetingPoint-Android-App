package com.app.meetingpoint.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;

import com.app.meetingpoint.R;
import com.app.meetingpoint.adapters.ChatListAdapter;
import com.app.meetingpoint.adapters.DrawerGroupsAdapter;
import com.app.meetingpoint.databinding.ActivityChatListBinding;
import com.app.meetingpoint.helpers.Constants;
import com.app.meetingpoint.helpers.PrefrencesManager;
import com.app.meetingpoint.interfaces.RecyclerViewInterface;
import com.app.meetingpoint.models.Conversation;
import com.app.meetingpoint.models.Group;
import com.app.meetingpoint.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ChatList extends BaseActivity implements RecyclerViewInterface {
    ActivityChatListBinding binding ;
    ArrayList<Conversation> myConversations ;
    ChatListAdapter chatListAdapter ;
    int i=0 ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        PrefrencesManager prefrencesManager = new PrefrencesManager(getApplicationContext());
        myConversations = new ArrayList<>();
        chatListAdapter = new ChatListAdapter(getApplicationContext(),myConversations,ChatList.this) ;
        binding.chatListRecentConvo.setAdapter(chatListAdapter);
        setupToolBar(prefrencesManager);
        setupDrawer(prefrencesManager);
        listenConversations(prefrencesManager);

    }
    private void setupDrawer(PrefrencesManager prefrencesManager) {
        ArrayList<Group> groups = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        CollectionReference groupCollection = database.collection(Constants.COLLECTION_GROUP_KEY);
        List<String> myGroups =
                Arrays.asList(prefrencesManager.getString(Constants.ATTRIBUTE_USERS_GROUPS_KEY).split(","));
        int end = myGroups.size() - 1;
        for (String id : myGroups) {
            groupCollection.document(id).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                Group group = task.getResult().toObject(Group.class);
                                assert group != null;
                                group.setId(task.getResult().getId());
                                groups.add(group);
                                if (i == end) {

                                    binding.groupChatsRecycler.setHasFixedSize(true);
                                    DrawerGroupsAdapter drawerGroupsAdapter = new
                                            DrawerGroupsAdapter(groups, ChatList.this);
                                    binding.groupChatsRecycler.setAdapter(drawerGroupsAdapter);


                                } else {
                                    i++;
                                }
                            }


                        }
                    });
        }


    }


    private void setupToolBar(PrefrencesManager prefrencesManager) {

        byte[] bytes = Base64.decode(prefrencesManager.getString(Constants.ATTRIBUTE_USERS_IMAGE_KEY), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.toolbar.toolbarImg.setImageBitmap(decodedByte);
        binding.toolbar.toolbarHome.setTitle("My messages");
        setSupportActionBar(binding.toolbar.toolbarHome);
    }

    private final EventListener<QuerySnapshot> eventListener = (value, error) -> {
        if(error != null){
            return ;
        }
        if(value != null){
            int count = myConversations.size();
            for(DocumentChange doc : value.getDocumentChanges()){
                switch (doc.getType()){
                    case ADDED:
                        System.out.println("im here in case added");
                        Conversation conversation = doc.getDocument().toObject(Conversation.class);
                        conversation.setConversationId(doc.getDocument().getId());
                        myConversations.add(0,conversation);
                        chatListAdapter.notifyItemRangeInserted(0,myConversations.size());
                        break;
                    case MODIFIED:
                        int position = -1 ;
                        Conversation conversation1 = doc.getDocument().toObject(Conversation.class);
                        conversation1.setConversationId(doc.getDocument().getId());
                        for(Conversation cv : myConversations){
                            if(cv.getConversationId().equals(conversation1.getConversationId())){
                                position = myConversations.indexOf(cv);
                                break ;
                            }
                        }
                        myConversations.set(position,conversation1);
                        chatListAdapter.notifyItemChanged(position);

                }
            }
            myConversations.sort(((o1, o2) -> o1.getTimestamp().compareTo(o2.getTimestamp())));
            Collections.reverse(myConversations);
            if(count == 0){
                System.out.println("the count is zero");
                chatListAdapter.notifyDataSetChanged();
            }
            else {
                chatListAdapter.notifyItemRangeInserted(myConversations.size(),myConversations.size());
            }


        }
    };


    private void listenConversations(PrefrencesManager prefrencesManager) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        CollectionReference conversations = database.collection(Constants.COLLECTION_CONVERSATIONS_KEY);
        String userId = prefrencesManager.getString(Constants.ATTRIBUTE_USERS_ID_KEY);
        conversations.where(Filter.or(
                        Filter.equalTo(Constants.ATTRIBUTE_CONVERSATIONS_FIRST_PARTY_KEY, userId),
                        Filter.equalTo(Constants.ATTRIBUTE_CONVERSATIONS_SECOND_PARTY_KEY, userId)
                ))
                .addSnapshotListener(eventListener);
    }





















    @Override
    public void onItemClick(int position, String item, String extras) {
        if(item.equals("group")){
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection(Constants.COLLECTION_GROUP_KEY)
                    .document(extras)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                Group group = task.getResult().toObject(Group.class);
                                assert group != null;
                                group.setId(task.getResult().getId());
                                Conversation conversation = new Conversation();
                                conversation.setConversationId(group.getConversationId());
                                conversation.setGroup(group.getId());
                                Intent intent = new Intent(getApplicationContext(),Chat.class);
                                intent.putExtra("conversation",conversation);
                                intent.putExtra("group",group);
                                startActivity(intent);
                            }
                        }
                    });
        }

    }
}