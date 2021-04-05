package com.example.eventsapp;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {
    private Context context;
    private List<Event> events;


    public EventsAdapter(Context context, List<Event> events) {
        this.context = context;
        this.events = events;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_event, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = events.get(position);
        holder.bind(event);
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public void clear() {
        events.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Event> eventList) {
        events.addAll(eventList);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout container;
        private TextView eventName;
        private ImageView eventImage;
        private TextView eventDate;
        private TextView eventGenre;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            eventName = itemView.findViewById(R.id.eventName);
            eventImage = itemView.findViewById(R.id.eventImage);
            eventDate = itemView.findViewById(R.id.eventDate);
            eventGenre = itemView.findViewById(R.id.eventGenre);
            container = itemView.findViewById(R.id.container);
        }

        public void bind(Event event) {
         //   eventName.setText(event.getName());
         //   eventDate.setText(event.getDate());
         //   eventGenre.setText(event.getGenre());

            // 1. Register click listener on the whole row
            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 2. Navigate to a new activity on tap
                 //   Intent i = new Intent(context, .class);
                //    i.putExtra("event", Parcels.wrap(event));
                //    context.startActivity(i);
                }
            });
        }

    }


}
