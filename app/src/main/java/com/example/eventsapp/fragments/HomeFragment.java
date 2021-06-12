package com.example.eventsapp.fragments;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventsapp.DetailsActivity;
import com.example.eventsapp.Event;
import com.example.eventsapp.EventsAdapter;
import com.example.eventsapp.ForYouAdapter;
import com.example.eventsapp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {
    SearchView inputSearch;
    RecyclerView recyclerView , horizontalView;
    FirebaseAuth mAuth;
    String currentUserID;
    TextView emptyView;
    ImageView Home;
    double currentLat;
    double currentLong;
    private boolean state = true;
    FirebaseRecyclerOptions<Event> options;
    FirebaseRecyclerAdapter<Event, EventsAdapter> adapter;
    FirebaseRecyclerAdapter<Event, ForYouAdapter> adapter2;
    DatabaseReference DataRef, rsvpRef;
    ArrayList<String> checkEmpty;

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

        if(FirebaseAuth.getInstance() == null){
            Log.e(getTag(), "instance null");
        }else{
            mAuth = FirebaseAuth.getInstance();
        }

        if(mAuth.getCurrentUser().getUid() == null){
            Toast.makeText(getContext(), "User ID cannot be null", Toast.LENGTH_SHORT).show();
        }else{
            currentUserID = mAuth.getCurrentUser().getUid();
        }

        DataRef =   FirebaseDatabase.getInstance().getReference().child("Events");
        rsvpRef = FirebaseDatabase.getInstance().getReference().child("RSVP");
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        horizontalView = view.findViewById(R.id.horizontalView);
        emptyView = view.findViewById(R.id.empty_view);
        horizontalView.setLayoutManager(new LinearLayoutManager(getContext()));
        horizontalView.setHasFixedSize(true);
        inputSearch = view.findViewById(R.id.inputSearch);
        location1 = view.findViewById(R.id.Location);
        Home = view.findViewById(R.id.home);
        checkEmpty = new ArrayList<>();
        LoadData("");

       // LoadRsvpdEvents();
        location1.setText("Miami");
        rsvpRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.hasChild(currentUserID)){
                    horizontalView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.INVISIBLE);
                    LoadRsvpdEvents();
                }
                else{
                    horizontalView.setVisibility(View.INVISIBLE);
                    emptyView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });

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

    private void LoadRsvpdEvents() {
        Query query = rsvpRef.orderByChild(currentUserID).equalTo(currentUserID);

        options = new FirebaseRecyclerOptions.Builder<Event>().setQuery(query, Event.class).build();
        adapter2 = new FirebaseRecyclerAdapter<Event, ForYouAdapter>(options) {
            @Override
            protected void onBindViewHolder(@NonNull @NotNull ForYouAdapter forYouAdapter, int i, @NonNull @NotNull Event event) {
                 String eventIds = getRef(i).getKey();
                checkEmpty.add(eventIds);
                Log.d(TAG, "CHecking if work: " + eventIds);
                DataRef.child(eventIds).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String day = snapshot.child("eventDate").getValue().toString();
                        String month = snapshot.child("eventMonth").getValue().toString();
                        String fee = snapshot.child("eventFee").getValue().toString();
                        String image = snapshot.child("eventImage").getValue().toString();
                        String genre = snapshot.child("eventGenre").getValue().toString();
                        String title = snapshot.child("eventTitle").getValue().toString();
                        forYouAdapter.eventDay.setText(day);
                        forYouAdapter.eventGenre.setText(genre);
                        forYouAdapter.eventFee.setText(fee);
                        forYouAdapter.eventTitle.setText(title);
                        Picasso.get().load(image).into(forYouAdapter.eventImage);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @NonNull
            @Override
            public ForYouAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.for_you_layout, parent, false);
                return new ForYouAdapter(v);
            }
        };

        adapter2.startListening();
        horizontalView.setAdapter(adapter2);
        horizontalView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    public void LoadData(String data) {
        Query query = DataRef.orderByChild("eventTitle").startAt(data).endAt(data + "\uf8ff");

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
