package com.example.eventsapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
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

public class HomeFragment extends Fragment {
    EditText inputSearch;
    RecyclerView recyclerView, horizontalView;
    TextView emptyView;
    FirebaseAuth mAuth;
    String currentUserID;

    FirebaseRecyclerOptions<Event> options;
    FirebaseRecyclerAdapter<Event, EventsAdapter> adapter;
    FirebaseRecyclerAdapter<Event, ForYouAdapter> adapter2;
    DatabaseReference DataRef, rsvpRef;
    ArrayList<String> checkEmpty;

    public static final String TAG = "HomeFragment";

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
        recyclerView = view.findViewById(R.id.recylerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        horizontalView = view.findViewById(R.id.horizontalView);
        emptyView = view.findViewById(R.id.empty_view);
        horizontalView.setLayoutManager(new LinearLayoutManager(getContext()));
        horizontalView.setHasFixedSize(true);
        inputSearch = view.findViewById(R.id.inputSearch);
        checkEmpty = new ArrayList<>();

        LoadData("");

        LoadRsvpdEvents();

        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString() != null){
                    LoadData(s.toString());
                }
                else{
                    LoadData(" ");
                }
            }
        });
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

        if(checkEmpty.isEmpty()){
            horizontalView.setVisibility(View.INVISIBLE);
            emptyView.setVisibility(View.VISIBLE);
        }
        else {
            horizontalView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.VISIBLE);
        }

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
