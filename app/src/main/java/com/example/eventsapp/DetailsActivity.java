package com.example.eventsapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.eventsapp.models.User;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Tag;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DetailsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private ImageView ivEventImage, ivUserImage;
    public LatLng location;
    private FirebaseAuth mAuth;
    TextView tvEventTitle, tvEventGenre, tvEventFee, tvEventTime, tvEventDate,
    tvEventOrganization, tvEventOrganizer, tvEventDescription, tvUserBio, tvEventLocation;
    TextView tveventFee2;
    private GoogleMap mMap;
    private MapView mapView;
    Button bnBuyTicket;
    DatabaseReference reference, EventsRef, UsersRef, rsvpRef, requestRef, CurrentUserReference;
    String currentUserID, eventOrganizer, EventID, eventTitle, specificEmail, specificName, specificImage;
    private String address;

    // chatroom
    private Button joinChatroom;

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
        tvEventDescription = findViewById(R.id.eventDescription);
        tvEventOrganizer = findViewById(R.id.eventOrganizer);
        tvEventOrganization = findViewById(R.id.eventOrganization);
        tvUserBio = findViewById(R.id.userBio);
        ivUserImage = findViewById(R.id.userImage);
        tvEventLocation = findViewById(R.id.eventLocation);


        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);


        mAuth = FirebaseAuth.getInstance();

        reference = FirebaseDatabase.getInstance().getReference().child("Events");
        currentUserID = mAuth.getCurrentUser().getUid();
        EventsRef = FirebaseDatabase.getInstance().getReference().child("Events");
        CurrentUserReference = FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserID);
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        rsvpRef = FirebaseDatabase.getInstance().getReference().child("RSVP");
        requestRef = FirebaseDatabase.getInstance().getReference().child("Requests");

        EventID = getIntent().getStringExtra("EventID");


        // Chatroom
        joinChatroom = (Button)findViewById(R.id.joinChatroomBtn);
        joinChatroom.setOnClickListener(v -> {
            Intent chatroomIntent = new Intent(this, ChatroomActivity.class);
            chatroomIntent.putExtra("EventID", EventID);
            // Can only enter chatroom if user RSVP to event
            FirebaseDatabase.getInstance().getReference()
                    .child("Events")
                    .child(EventID)
                    .child("Attendees")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            for (DataSnapshot snaps : snapshot.getChildren()) {
                                if(currentUserID.equals(snaps.getValue().toString())) {
                                    startActivity(chatroomIntent);
                                    return;
                                }
                            }
                            Toast.makeText(DetailsActivity.this, "You must RSVP to enter the chatroom.", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {
                            Toast.makeText(DetailsActivity.this, "Error getting info from the database.", Toast.LENGTH_SHORT).show();
                        }
                    });
        });


        reference.child(EventID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String eventTitle = snapshot.child("eventTitle").getValue().toString();
                    String eventFee = snapshot.child("eventFee").getValue().toString();
                    String eventFee2 = snapshot.child("eventFee").getValue().toString();
                    String eventDate = snapshot.child("eventDate").getValue().toString();
                    String eventTime = snapshot.child("eventTimeStart").getValue().toString();
                    String eventGenre = snapshot.child("eventGenre").getValue().toString();
                    String imageUrl = snapshot.child("eventImage").getValue().toString();
                    String eventOrganization = snapshot.child("eventOrganization").getValue().toString();
                    String eventDescription = snapshot.child("eventDescription").getValue().toString();
                    String username = snapshot.child("username").getValue().toString();
                    String userBio = snapshot.child("userBio").getValue().toString();
                    String userImage = snapshot.child("userImage").getValue().toString();
                    Double latitude = ((Double) snapshot.child("latitude").getValue());
                    Double longitude = (Double) snapshot.child("longitude").getValue();

                    try{
                        Geocoder geo = new Geocoder(getApplicationContext(), Locale.getDefault());
                        List<Address> addresses = geo.getFromLocation(latitude, longitude, 1);
                        if (addresses.isEmpty()) {
                            tvEventLocation.setText("Location unknown");
                        }
                        else {
                            if (addresses.size() > 0) {
                                 address = addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() + ", " + addresses.get(0).getAdminArea()
                                        + ", " + addresses.get(0).getCountryName();

                            }
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }

                    Picasso.get().load(imageUrl).into(ivEventImage);
                    Picasso.get().load(userImage).into(ivUserImage);
                    tvEventTitle.setText(eventTitle);
                    tvEventFee.setText(eventFee);

                    if(eventFee2.equals("0")) tveventFee2.setText("Free Event!");
                    else tveventFee2.setText("Event Fee: $" + eventFee2);

                    tvEventDate.setText(eventDate);
                    tvEventTime.setText(eventTime);
                    tvEventGenre.setText(eventGenre);
                    tvEventOrganization.setText(eventOrganization);
                    tvEventDescription.setText("Description: " + eventDescription);
                    tvEventOrganizer.setText(username);
                    tvUserBio.setText(userBio);
                    if(snapshot.child("eventPrivacy").getValue().toString().equals("true")){
                        if(snapshot.child("Attendees").hasChild(currentUserID)){
                            tvEventLocation.setText(address);
                        }
                        else{
                            tvEventLocation.setText("Private event: address is hidden");
                        }

                    }
                    else if(snapshot.child("eventPrivacy").getValue().toString().equals("false")){
                        tvEventLocation.setText(address);
                    }
                    else{
                        tvEventLocation.setText(address);
                    }


                    bnBuyTicket.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // EventsRef.child(EventID).child("Attendees").child("currentUserID").setValue(currentUserID);
                           // UsersRef.child(currentUserID).child("Attending").child("EventID").setValue(EventID);
                            // rsvpRef.child(currentUserID).child("username").setValue(EventID);
                          //  rsvpRef.child(EventID).child(currentUserID).setValue(currentUserID);

                            UsersRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                    specificEmail = snapshot.child("email").getValue().toString();
                                    specificName = snapshot.child("fullName").getValue().toString();
                                    specificImage = snapshot.child("userImage").getValue().toString();
                                }

                                @Override
                                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                }
                            });

                            EventsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    Event event;
                                    try {
                                        for (DataSnapshot s : snapshot.getChildren()) {
                                            event = s.getValue(Event.class);
                                            String eventName = event.eventTitle;

                                                if(s.getKey().equals(EventID)){
                                                    if(event.eventPrivacy.equals( "true")) {
                                                        String peanut = event.userID;
                                                        HashMap<String, Object> profileMap = new HashMap<>();
                                                        profileMap.put("name", specificName);
                                                        profileMap.put("email", specificEmail);
                                                        profileMap.put("image", specificImage);
                                                        profileMap.put("event", eventName);
                                                        profileMap.put("eventID", EventID);
                                                        profileMap.put("UID", currentUserID);
                                                        requestRef.child(peanut).push().updateChildren(profileMap);
                                                    }
                                                    else if(event.eventPrivacy.equals("false")){
                                                        rsvpRef.child(EventID).child(currentUserID).setValue(currentUserID);
                                                        EventsRef.child(EventID).child("Attendees").child(currentUserID).setValue(currentUserID);
                                                        UsersRef.child(currentUserID).child("Attending").child(EventID).setValue(EventID);
                                                    }
                                            }

                                        }
                                    } catch (NullPointerException e) {
                                        Toast.makeText(getApplicationContext(), "An event was not able to load.", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                }
                            });

                            Toast.makeText(getApplicationContext(), "You have successfully registered", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
        @Override
        public void onResume() {
            super.onResume();
            mapView.onResume();
        }
       /* bnBuyTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventsRef.child(eventID).push().child("Attendees").setValue(currentUserID);

                // Toast.makeText(getApplicationContext(), "You have successfully register", Toast.LENGTH_LONG).show();

            }
        });    */


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title(eventTitle));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        EventsRef.child(EventID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if(snapshot.child("eventPrivacy").getValue().toString().equals("false")) {
                        Event event = snapshot.getValue(Event.class);
                        location = new LatLng(event.latitude, event.longitude);
                        mMap.addMarker(new MarkerOptions().position(location).title(event.getEventTitle()));
                        moveCamera(location, 0, event.getEventTitle());
                    }
                    else{
                        Event event = snapshot.getValue(Event.class);
                        Circle circle = mMap.addCircle(new CircleOptions()
                                .center(new LatLng(event.latitude, event.longitude))
                                .radius(10000)
                                .strokeColor(Color.RED)
                                .fillColor(Color.BLUE));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        mMap.setMyLocationEnabled(true);
    }
    public void moveCamera(LatLng latLng, float zoom, String title){
        //Log.d(TAG, "moveCamera: moving the camera to: lat: " + latLng.latitude + ", lng: " + latLng.longitude );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));


        // if(!title.equals("My Location")){
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title(title);
        mMap.addMarker(options);
        //   }
    }
}