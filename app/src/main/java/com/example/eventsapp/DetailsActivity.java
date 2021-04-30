package com.example.eventsapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.eventsapp.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Tag;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class DetailsActivity extends AppCompatActivity {
    private ImageView ivEventImage, ivUserImage;
    private FirebaseAuth mAuth;
    TextView tvEventTitle, tvEventGenre, tvEventFee, tvEventTime, tvEventDate,
    tvEventOrganization, tvEventOrganizer, tvEventDescription, tvUserBio;
    Button bnBuyTicket;
    DatabaseReference reference, EventsRef, UsersRef, rsvpRef;
    String currentUserID, eventOrganizer;
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
        tvEventGenre = findViewById(R.id.eventGenre);
        ivEventImage = findViewById(R.id.eventImage);
        tvEventDescription = findViewById(R.id.eventDescription);
        tvEventOrganizer = findViewById(R.id.eventOrganizer);
        tvEventOrganization = findViewById(R.id.eventOrganization);
        tvUserBio = findViewById(R.id.userBio);
        ivUserImage = findViewById(R.id.userImage);

        mAuth = FirebaseAuth.getInstance();

        reference = FirebaseDatabase.getInstance().getReference().child("Events");
        currentUserID = mAuth.getCurrentUser().getUid();
        EventsRef = FirebaseDatabase.getInstance().getReference().child("Events");
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        rsvpRef = FirebaseDatabase.getInstance().getReference().child("RSVP");

        String EventID = getIntent().getStringExtra("EventID");


        reference.child(EventID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    String eventTitle = snapshot.child("eventTitle").getValue().toString();
                    String eventFee = snapshot.child("eventFee").getValue().toString();
                    String eventDate = snapshot.child("eventDate").getValue().toString();
                    String eventTime = snapshot.child("eventTimeStart").getValue().toString();
                    String eventGenre = snapshot.child("eventGenre").getValue().toString();
                    String imageUrl = snapshot.child("eventImage").getValue().toString();
                    String eventOrganization = snapshot.child("eventOrganization").getValue().toString();
                    String eventDescription = snapshot.child("eventDescription").getValue().toString();
                    String username = snapshot.child("username").getValue().toString();
                    String userBio = snapshot.child("userBio").getValue().toString();
                    String userImage = snapshot.child("userImage").getValue().toString();

                    Picasso.get().load(imageUrl).into(ivEventImage);
                    Picasso.get().load(userImage).into(ivUserImage);
                    tvEventTitle.setText(eventTitle);
                    tvEventFee.setText(eventFee);
                    tvEventDate.setText(eventDate);
                    tvEventTime.setText(eventTime);
                    tvEventGenre.setText(eventGenre);
                    tvEventOrganization.setText(eventOrganization);
                    tvEventDescription.setText(eventDescription);
                    tvEventOrganizer.setText(username);
                    tvUserBio.setText(userBio);


                    bnBuyTicket.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Map<String,Object> taskMap = new HashMap<>();
                            taskMap.put(currentUserID, currentUserID);

                           // EventsRef.child(EventID).child("Attendees").updateChildren(taskMap);

                           // EventsRef.child(EventID).child("Attendees").child("currentUserID").setValue(currentUserID);
                          //  UsersRef.child(currentUserID).child("Attending").child("EventID").setValue(EventID);
                            rsvpRef.child(currentUserID).child("username").setValue(EventID);

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