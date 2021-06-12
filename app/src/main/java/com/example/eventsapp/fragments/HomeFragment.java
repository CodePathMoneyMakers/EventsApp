package com.example.eventsapp.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventsapp.DetailsActivity;
import com.example.eventsapp.Event;
import com.example.eventsapp.EventsAdapter;
import com.example.eventsapp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {
    SearchView inputSearch;
    RecyclerView recyclerView;
    FirebaseAuth mAuth;
    String currentUserID;
    double currentLat;
    double currentLong;
    private boolean state = true;
    FirebaseRecyclerOptions<Event> options;
    FirebaseRecyclerAdapter<Event, EventsAdapter> adapter;
    DatabaseReference DataRef;

    public static final String TAG = "HomeFragment";
    private TextView location1;

    public HomeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragments_home, container, false);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        if (FirebaseAuth.getInstance() == null) {
            Log.e(getTag(), "instance null");
        } else {
            mAuth = FirebaseAuth.getInstance();
        }

        if (mAuth.getCurrentUser().getUid() == null) {
            Toast.makeText(getContext(), "User ID cannot be null", Toast.LENGTH_SHORT).show();
        } else {
            currentUserID = mAuth.getCurrentUser().getUid();
        }

        DataRef = FirebaseDatabase.getInstance().getReference().child("Events");
        recyclerView = view.findViewById(R.id.recylerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        inputSearch = view.findViewById(R.id.inputSearch);
        location1 = view.findViewById(R.id.Location);
        LoadData("");

        try{
            FusedLocationProviderClient mFusedLocationProviderClient = new FusedLocationProviderClient(getContext());
            Task lastLocation = mFusedLocationProviderClient.getLastLocation();
            lastLocation.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    // GPS location can be null if GPS is switched off
                    if (location != null) {
                        currentLat = location.getLatitude();
                        currentLong = location.getLongitude();
                        location1.setText(getRegionName(currentLat, currentLong));
                        Log.i("HomeFragment", getRegionName(currentLat, currentLong));
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("HomeFragment", "Error trying to get last GPS location");
                    e.printStackTrace();
                }
            });

        }catch (SecurityException | NullPointerException e){
            Log.e(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }

//        Geocoder geoCoder = new Geocoder(getContext(), Locale.getDefault());
//        try {
//            List<Address> addresses = new List<Address>();
//            addresses = geoCoder.getFromLocation(currentLat, currentLong, 1);
//
//            if (addresses != null && addresses.size() > 0)
//            {
//                location.setText(addresses.get(0).getLocality());
//                Log.i("HomeFragment", String.valueOf(addresses.size()));
//            }
//
//        }
//        catch (IOException | SecurityException e1) {
//            e1.printStackTrace();
//        }

//        Geocoder geocoder = new Geocoder(getContext());
//
//        try {
//            List<Address>addresses = geocoder.getFromLocation(currentLat,currentLong,1);
//            if (geocoder.isPresent()) {
//                StringBuilder stringBuilder = new StringBuilder();
//                if (addresses.size()>0) {
//                    Address returnAddress = addresses.get(0);
//
//                    String localityString = returnAddress.getLocality();
//                    String name = returnAddress.getFeatureName();
//                    String subLocality = returnAddress.getSubLocality();
//                    String country = returnAddress.getCountryName();
//                    String region_code = returnAddress.getCountryCode();
//                    String zipcode = returnAddress.getPostalCode();
//                    String state = returnAddress.getAdminArea();
//
//                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        TextView Home = view.findViewById(R.id.Home);
        inputSearch.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Home.setVisibility(View.INVISIBLE);

            }
        });

        inputSearch.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Home.setVisibility(View.VISIBLE);
                LoadData("");
                return false;
            }
        });

        inputSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                LoadData(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                LoadData(newText);
                return false;
            }
        });
//        .addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if(s.toString() != null){
//                    LoadData(s.toString());
//                }
//                else{
//                    LoadData(" ");
//                }
//            }
//        });
    }
    private String getRegionName(double lati, double longi) {
        String regioName = "";
        Geocoder gcd = new Geocoder(getContext(), Locale.getDefault());
        try {
            List<Address> addresses = gcd.getFromLocation(lati, longi, 1);
            if (addresses.isEmpty()) {
                regioName = "Location unknown";
            }
            else if (addresses.size() > 0) {
                regioName = addresses.get(0).getLocality();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return regioName;
    }

    public void LoadData(String data) {
        Query query = DataRef.orderByChild("eventTitle").startAt(data).endAt(data + "\uf8ff");
        //
        options = new FirebaseRecyclerOptions.Builder<Event>().setQuery(query, Event.class).build();
        adapter = new FirebaseRecyclerAdapter<Event, EventsAdapter>(options) {

            @Override
            protected void onBindViewHolder(@NonNull EventsAdapter eventsAdapter, int i, @NonNull Event event) {
                eventsAdapter.eventTitle.setText(event.getEventTitle());
                eventsAdapter.eventGenre.setText(event.getEventGenre());
                eventsAdapter.eventFee.setText(event.getEventFee());
                eventsAdapter.eventDay.setText(event.getEventDay());
                eventsAdapter.eventMonth.setText(event.getEventMonth());
                Picasso.get().load(event.getEventImage()).into(eventsAdapter.eventImage);

                eventsAdapter.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent (getContext(), DetailsActivity.class);
                        intent.putExtra("EventID", getRef(i).getKey());
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public EventsAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
                return new EventsAdapter(v);
            }
        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }
}
