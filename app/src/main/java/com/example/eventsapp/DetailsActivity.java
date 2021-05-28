package com.example.eventsapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.eventsapp.fragments.ComposeFragment;
import com.example.eventsapp.models.User;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Tag;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.squareup.picasso.Picasso;

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
    DatabaseReference reference, EventsRef, UsersRef, rsvpRef;
    String currentUserID, eventOrganizer, EventID, eventTitle;
    private String address;

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
        TextView tvEventTime1 = findViewById(R.id.eventTime1);
        tveventFee2 = findViewById(R.id.eventFee2);
        tvEventGenre = findViewById(R.id.eventGenre);
        ivEventImage = findViewById(R.id.eventImage);
        tvEventDescription = findViewById(R.id.expandable_text);
        tvEventOrganizer = findViewById(R.id.eventOrganizer);
        tvEventOrganization = findViewById(R.id.eventOrganization);
        tvUserBio = findViewById(R.id.userBio);
        ivUserImage = findViewById(R.id.userImage);
        tvEventLocation = findViewById(R.id.eventLocation);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        // sample code snippet to set the text content on the ExpandableTextView
        ExpandableTextView expTv1 = findViewById(R.id.expand_text_view);

//        findViewById(R.id.coordinator).setOnTouchListener(new DetailsActivity.OnSwipeTouchListener(this){
//
//            @SuppressLint("ResourceAsColor")
//            public void onSwipeTop() {
//                Toast.makeText(DetailsActivity.this, "top", Toast.LENGTH_SHORT).show();
//                Button buyTicket = findViewById(R.id.buyTicket);
//                       buyTicket.setTextColor(R.color.white);
//                       buyTicket.setBackgroundColor(R.color.red);
//                       buyTicket.setBackgroundResource(R.drawable.btn_background_red);
//                       //TextView(getApplicationContext(), R.style.Widget_App_ButtonStyle1);
//            }
//
//        });

        mAuth = FirebaseAuth.getInstance();

        reference = FirebaseDatabase.getInstance().getReference().child("Events");
        currentUserID = mAuth.getCurrentUser().getUid();
        EventsRef = FirebaseDatabase.getInstance().getReference().child("Events");
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        rsvpRef = FirebaseDatabase.getInstance().getReference().child("RSVP");

        EventID = getIntent().getStringExtra("EventID");

        reference.child(EventID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String eventTitle = snapshot.child("eventTitle").getValue().toString();
                    String eventFee = snapshot.child("eventFee").getValue().toString();
                    String eventFee2 = snapshot.child("eventFee").getValue().toString();
                    String eventDate = snapshot.child("eventDate").getValue().toString();
                    String eventTime = snapshot.child("eventTimeStart").getValue().toString();
                    String eventTime1 = snapshot.child("eventTimeEnd").getValue().toString();
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
                    tveventFee2.setText(eventFee2);
                    tvEventDate.setText(eventDate);
                    tvEventTime.setText(" • " + eventTime + " ⁃ ");
                    tvEventTime1.setText(eventTime1);
                    tvEventGenre.setText(eventGenre);
                    tvEventOrganization.setText(eventOrganization);
                    //tvEventDescription.setText();
                    tvEventOrganizer.setText(username);
                    tvUserBio.setText(userBio);
                    tvEventLocation.setText(address);

                    // IMPORTANT - call setText on the ExpandableTextView to set the text content to display
                    expTv1.setText(eventDescription);


                    bnBuyTicket.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Map<String, Object> taskMap = new HashMap<>();
                            taskMap.put(currentUserID, currentUserID);

                            EventsRef.child(EventID).child("Attendees").updateChildren(taskMap);

                            // EventsRef.child(EventID).child("Attendees").child("currentUserID").setValue(currentUserID);
                            UsersRef.child(currentUserID).child("Attending").child("EventID").setValue(EventID);
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
                    Event event = snapshot.getValue(Event.class);
                    location = new LatLng(event.latitude, event.longitude);
                    mMap.addMarker(new MarkerOptions().position(location).title(event.getEventTitle()));
                    moveCamera(location, 0, event.getEventTitle());
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
    public class OnSwipeTouchListener implements View.OnTouchListener {

        private final GestureDetector gestureDetector;

        public OnSwipeTouchListener (Context ctx){
            gestureDetector = new GestureDetector(ctx, new OnSwipeTouchListener.GestureListener());
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return gestureDetector.onTouchEvent(event);
        }

        private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

            private static final int SWIPE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                boolean result = false;
                try {
                    float diffY = e2.getY() - e1.getY();
                    float diffX = e2.getX() - e1.getX();
                    if (Math.abs(diffX) > Math.abs(diffY)) {
                        if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                            if (diffX > 0) {
                                onSwipeRight();
                            } else {
                                onSwipeLeft();
                            }
                            result = true;
                        }
                    }
                    else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffY > 0) {
                            onSwipeBottom();
                        } else {
                            onSwipeTop();
                        }
                        result = true;
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return result;
            }
        }

        public void onSwipeRight() {
        }

        public void onSwipeLeft() {
        }

        public void onSwipeTop() {
        }

        public void onSwipeBottom() {
        }
    }
}
