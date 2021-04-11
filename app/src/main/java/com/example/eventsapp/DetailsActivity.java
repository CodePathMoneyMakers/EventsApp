package com.example.eventsapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class DetailsActivity extends AppCompatActivity {
    private ImageView ivEventImage;
    TextView tvEventTitle;
    Button bnBuyTicket;
    DatabaseReference reference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        bnBuyTicket = findViewById(R.id.buyTicket);
        tvEventTitle = findViewById(R.id.eventTitle);
        ivEventImage = findViewById(R.id.eventImage);

        reference = FirebaseDatabase.getInstance().getReference().child("EventID");

        String EventID = getIntent().getStringExtra("EventID");

        reference.child(EventID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String eventTitle = snapshot.child("eventTitle").getValue().toString();
                    String imageUrl = snapshot.child("eventImage").getValue().toString();

                    Picasso.get().load(imageUrl).into(ivEventImage);
                    tvEventTitle.setText(eventTitle);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}