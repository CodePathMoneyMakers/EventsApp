package com.example.eventsapp;

import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class ChatroomActivity extends AppCompatActivity {

    private static final String TAG = "ChatroomActivity";

    private Button sendBtn;
    String currentUserID, EventID, Username;
    private FirebaseListAdapter<ChatMessage> adapter;
    private FirebaseListOptions<ChatMessage> options;
    private DatabaseReference CurrentUserReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chatroom);
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        EventID = getIntent().getStringExtra("EventID");

        CurrentUserReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        CurrentUserReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                Username = snapshot.child("fullName").getValue().toString();
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
                    .setValue(new ChatMessage(inputMessage.getText().toString(), Username));

            inputMessage.setText("");   // Clear the input
        });

        Toast.makeText(this, "Welcome " + Username, Toast.LENGTH_LONG).show();

        // Load chat room contents
        displayChatMessages();
    }

    private void displayChatMessages() {
        Toast.makeText(ChatroomActivity.this, "displayChatMessages()", Toast.LENGTH_SHORT).show();

        ListView listOfMessages = (ListView)findViewById(R.id.list_of_messages);
        Query query = FirebaseDatabase.getInstance().getReference().child("Events").child(EventID).child("Chat");

        options = new FirebaseListOptions.Builder<ChatMessage>()
                .setQuery(query, ChatMessage.class)
                .setLayout(R.layout.message)
                .build();

        adapter = new FirebaseListAdapter<ChatMessage>(options) {
            @Override
            protected void populateView(View v, ChatMessage chatModel, int position) {
                final String chatID = getRef(position).getKey();
                Log.d(TAG, "Look here: " + chatID);

                TextView messageText = (TextView)v.findViewById(R.id.message_text);
                TextView messageUser = (TextView)v.findViewById(R.id.message_user);
                TextView messageTime = (TextView)v.findViewById(R.id.message_time);

                // Set their text
                messageText.setText(chatModel.getMessageText());
                messageUser.setText(chatModel.getMessageUser());

                // Format the date before showing it
                messageTime.setText(DateFormat.format("MM-dd-yyyy (HH:mm:ss)",
                        chatModel.getMessageTime()));

            }
        };
        listOfMessages.setAdapter(adapter);
        adapter.startListening();
    }
}
