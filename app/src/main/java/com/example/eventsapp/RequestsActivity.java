package com.example.eventsapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.eventsapp.models.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Tag;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

public class RequestsActivity extends AppCompatActivity {
    private static final String TAG = "RequestsActivity";
    FirebaseRecyclerOptions<User> options;
    FirebaseRecyclerAdapter<User, RequestsAdapter> adapter;
    RecyclerView recyclerView;
    DatabaseReference requestsRef, usersRef;
    String EventID;
    Integer i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        recyclerView = findViewById(R.id.requests_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setHasFixedSize(true);

        requestsRef = FirebaseDatabase.getInstance().getReference().child("Requests");
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        EventID = getIntent().getStringExtra("EventID");

        loadData();
    }

    public void loadData(){
            Query query = requestsRef.orderByChild(EventID);

            options = new FirebaseRecyclerOptions.Builder<User>().setQuery(query, User.class).build();
            adapter = new FirebaseRecyclerAdapter<User, RequestsAdapter>(options) {
                @Override
                protected void onBindViewHolder(@NonNull RequestsAdapter requestsAdapter, int i, @NonNull User user) {
                    final String eventID = getRef(i).getKey();
                    Log.d(TAG, "Testing: " + eventID);
                    requestsRef.child(eventID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            String userID = snapshot.getValue().toString();
                            Log.d(TAG, "User IDs: " + userID);

                            usersRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                    requestsAdapter.userName.setText(user.getFullName());
                                    Picasso.get().load(user.getUserImage()).into(requestsAdapter.userImage);
                                }

                                @Override
                                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                }
                            });


                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                        }
                    });

                }

                @NonNull
                @Override
                public RequestsAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
                    return new RequestsAdapter(v);
                }
            };

            adapter.startListening();
            recyclerView.setAdapter(adapter);
    }
}