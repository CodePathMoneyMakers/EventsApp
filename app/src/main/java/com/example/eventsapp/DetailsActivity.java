package com.example.eventsapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class DetailsActivity extends AppCompatActivity {
    private ImageView ivEventImage;
    private FirebaseAuth mAuth;
    TextView tvEventTitle, tvEventGenre, tvEventFee, tvEventTime, tvEventDate, tveventFee2;
    Button bnBuyTicket;
    DatabaseReference reference, EventsRef, UsersRef;
    String currentUserID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        bnBuyTicket = findViewById(R.id.buyTicket);
        tvEventTitle = findViewById(R.id.eventTitle);
        ivEventImage = findViewById(R.id.eventImage);
        tvEventDate = findViewById(R.id.eventDate);
        tvEventFee = findViewById(R.id.eventFee);
        tvEventTime = findViewById(R.id.eventTime);
        tveventFee2 = findViewById(R.id.eventFee2);
        tvEventGenre = findViewById(R.id.eventGenre);
        ivEventImage = findViewById(R.id.eventImage);

        mAuth = FirebaseAuth.getInstance();

        reference = FirebaseDatabase.getInstance().getReference().child("Events");
        currentUserID = mAuth.getCurrentUser().getUid();
        EventsRef = FirebaseDatabase.getInstance().getReference().child("Events");
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        String EventID = getIntent().getStringExtra("EventID");

        reference.child(EventID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String eventTitle = snapshot.child("eventTitle").getValue().toString();
                    String eventFee = snapshot.child("eventFee").getValue().toString();
                    String eventFee2 = snapshot.child("eventFee").getValue().toString();
                    String eventDate = snapshot.child("eventDate").getValue().toString();
                    String eventTime = snapshot.child("eventTimeStart").getValue().toString();
                    String eventGenre = snapshot.child("eventMusic").getValue().toString();
                    String imageUrl = snapshot.child("eventImage").getValue().toString();

                    Picasso.get().load(imageUrl).into(ivEventImage);
                    tvEventTitle.setText(eventTitle);
                    tvEventFee.setText(eventFee);
                    tveventFee2.setText(eventFee2);
                    tvEventDate.setText(eventDate);
                    tvEventTime.setText(eventTime);
                    tvEventGenre.setText(eventGenre);

                    bnBuyTicket.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EventsRef.child(EventID).child("Attendees").child("currentUserID").setValue(currentUserID);
                            UsersRef.child(currentUserID).child("Attending").child("EventID").setValue(EventID);

                            Toast.makeText(getApplicationContext(), "You have successfully registered", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

       /* bnBuyTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventsRef.child(eventID).push().child("Attendees").setValue(currentUserID);

                // Toast.makeText(getApplicationContext(), "You have successfully register", Toast.LENGTH_LONG).show();

            }
        });    */
    }
}