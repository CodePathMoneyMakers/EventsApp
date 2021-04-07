package com.example.eventsapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventsapp.Event;
import com.example.eventsapp.EventsAdapter;
import com.example.eventsapp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

public class HomeFragment extends Fragment {
    EditText inputSearch;
    RecyclerView recyclerView;
    FirebaseAuth mAuth;
    String currentUserID;

    FirebaseRecyclerOptions<Event> options;
    FirebaseRecyclerAdapter<Event, EventsAdapter> adapter;
    DatabaseReference DataRef;

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

        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        DataRef =   FirebaseDatabase.getInstance().getReference().child("Events").child(currentUserID);
        inputSearch = view.findViewById(R.id.inputSearch);
        recyclerView = view.findViewById(R.id.recylerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        
        LoadData();
    }

    public void LoadData() {
        options = new FirebaseRecyclerOptions.Builder<Event>().setQuery(DataRef, Event.class).build();
        adapter = new FirebaseRecyclerAdapter<Event, EventsAdapter>(options) {
            @Override
            protected void onBindViewHolder(@NonNull EventsAdapter eventsAdapter, int i, @NonNull Event event) {
                eventsAdapter.eventDate.setText(event.getEventDate());
                Picasso.get().load(event.getEventImage()).into(eventsAdapter.eventImage);
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
