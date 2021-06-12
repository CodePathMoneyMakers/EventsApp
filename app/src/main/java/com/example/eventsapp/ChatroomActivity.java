package com.example.eventsapp;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventsapp.adapters.MessageAdapter;
import com.example.eventsapp.models.ChatMessage;
import com.example.eventsapp.models.User;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class ChatroomActivity extends AppCompatActivity {

    private static final String TAG = "ChatroomActivity";

    private Button sendBtn;
    private String currentUserID, EventID, EventTitle, Username;
    private String username, textMessage;
    private FirebaseListAdapter<ChatMessage> adapter;
    private FirebaseListOptions<ChatMessage> options;
    private DatabaseReference CurrentUserReference, CurrentEventReference, ChatsRef;
    private TextView chatTitle, messageText, messageUser, messageTime;
    //private String userProfileImage;

    List<ChatMessage> mChat;
    RecyclerView recyclerView;
    MessageAdapter messageAdapter;
    //FirebaseRecyclerAdapter<ChatMessage, MessageAdapter> adapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatroom);

        recyclerView = findViewById(R.id.list_of_messages);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

         messageText = (TextView)findViewById(R.id.chat_message);
         messageUser = (TextView)findViewById(R.id.chat_user);
         messageTime = (TextView)findViewById(R.id.chat_message_time);

        sendBtn = (Button)findViewById(R.id.sendButton);

//        sendBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String msg = messageText.getText().toString();
//                if(!msg.equals("")){
//                    sendMessage();
//                }
//                else{
//                    Toast.makeText(ChatroomActivity.this, "You can't send empty texts", Toast.LENGTH_LONG).show();
//                }
//                messageText.setText("");
//            }
//        });
//        sendBtn = (Button)findViewById(R.id.sendButton);
//        sendBtn.setOnClickListener(v -> {
//            EditText inputMessage = (EditText)findViewById(R.id.input);
//            // Push a Chat to the Firebase
//            FirebaseDatabase.getInstance()
//                    .getReference()
//                    .child("Events")
//                    .child(EventID)
//                    .child("Chat")
//                    .push()
//                    .setValue(new ChatMessage(inputMessage.getText().toString(), Username));
//
//            inputMessage.setText("");   // Clear the input
//        });


        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        CurrentUserReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        EventID = getIntent().getStringExtra("EventID");
//
        chatTitle = findViewById(R.id.chatView_title);
//
        CurrentEventReference = FirebaseDatabase.getInstance().getReference().child("Events").child(EventID);
        CurrentEventReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                EventTitle = snapshot.child("eventTitle").getValue().toString();
                chatTitle.setText(EventTitle);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                chatTitle.setText("Welcome to the Chat");
            }
        });
//

        readMessage();
        CurrentUserReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                Username = snapshot.child("fullName").getValue().toString();
                //userProfileImage = snapshot.child("userImage").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Username = "Unidentified User";
            }
        });

        sendBtn = (Button)findViewById(R.id.sendButton);
        sendBtn.setOnClickListener(v -> {
            EditText inputMessage = (EditText)findViewById(R.id.input);
            // Push a Chat to the Firebase
            FirebaseDatabase.getInstance()
                    .getReference()
                    .child("Events")
                    .child(EventID)
                    .child("Chat")
                    .push()
                    .setValue(new ChatMessage(inputMessage.getText().toString(), Username, currentUserID));

            inputMessage.setText("");   // Clear the input
        });

//        //Toast.makeText(this, "Welcome " + Username, Toast.LENGTH_LONG).show();
//
//        // Load chat room contents
//        displayChatMessages();
//    }
//
//    private void displayChatMessages() {
//        //Toast.makeText(ChatroomActivity.this, "displayChatMessages()", Toast.LENGTH_SHORT).show();
//
//        ListView listOfMessages = (ListView)findViewById(R.id.list_of_messages);
//        Query query = FirebaseDatabase.getInstance().getReference().child("Events").child(EventID).child("Chat");
//
//        options = new FirebaseListOptions.Builder<ChatMessage>()
//                .setQuery(query, ChatMessage.class)
//                .setLayout(R.layout.message)
//                .build();
//
//        adapter = new FirebaseListAdapter<ChatMessage>(options) {
//            @Override
//            protected void populateView(View v, ChatMessage chatModel, int position) {
//                final String chatID = getRef(position).getKey();
//                Log.i(TAG, "Chat ID: " + chatID);
//
//                TextView messageText = (TextView)v.findViewById(R.id.chat_message);
//                TextView messageUser = (TextView)v.findViewById(R.id.chat_user);
//                TextView messageTime = (TextView)v.findViewById(R.id.chat_message_time);
//                //ImageView messageImage = (ImageView)v.findViewById(R.id.message_profile_image);
//
//                // Set their text
//                messageText.setText(chatModel.getMessageText());
//                messageUser.setText(chatModel.getMessageUser());
//
//                // Format the date before showing it
//                messageTime.setText(DateFormat.format("h:mm a",
//                        chatModel.getMessageTime()));
//            }
//        };
//        listOfMessages.setAdapter(adapter);
//        adapter.startListening();
    }

    private void sendMessage(){
        sendBtn.setOnClickListener(v -> {
            EditText inputMessage = (EditText)findViewById(R.id.input);
            // Push a Chat to the Firebase
            FirebaseDatabase.getInstance()
                    .getReference()
                    .child("Events")
                    .child(EventID)
                    .child("Chat")
                    .push()
                    .setValue(new ChatMessage(inputMessage.getText().toString(), Username, currentUserID));

            inputMessage.setText("");   // Clear the input
        });
    }

    private void readMessage(){
        mChat = new ArrayList<>();

        ChatsRef = FirebaseDatabase.getInstance().getReference().child("Events").child(EventID).child("Chat");
        ChatsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                mChat.clear();
                for(DataSnapshot snapshot1: snapshot.getChildren()){
                    ChatMessage chat = snapshot1.getValue(ChatMessage.class);
                    mChat.add(chat);
                }

                messageAdapter = new MessageAdapter(ChatroomActivity.this, mChat);
                recyclerView.setAdapter(messageAdapter);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}
