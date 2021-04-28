package com.example.eventsapp;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class EventsAdapter extends RecyclerView.ViewHolder {
    public ImageView eventImage;
    public TextView eventTitle;
    public TextView eventGenre;
    public TextView eventFee;
    public TextView eventMonth;
    public TextView eventDay;
    public View view;

    public EventsAdapter(@NonNull View itemView) {
        super(itemView);

        eventImage = itemView.findViewById(R.id.eventImage);
        eventMonth = itemView.findViewById(R.id.eventMonth);
        eventDay = itemView.findViewById(R.id.eventDay);
        eventTitle = itemView.findViewById(R.id.eventTitle);
        eventGenre = itemView.findViewById(R.id.eventGenre);
        eventFee = itemView.findViewById(R.id.eventFee);

        view = itemView;
    }
}
  /*  private Context context;
    private FirebaseRecyclerOptions<Event> events;


    /*public EventsAdapter(Context context, FirebaseRecyclerOptions<Event> events) {
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
        holder.bind(events);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

   /* @Override
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
    } */

   /* class ViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout container;
        private TextView eventTitle;
        private ImageView eventImage;
        private TextView eventDate;
        private TextView eventGenre;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            eventTitle = itemView.findViewById(R.id.eventTitle);
            eventImage = itemView.findViewById(R.id.eventImage);
            eventDate = itemView.findViewById(R.id.eventDate);
            eventGenre = itemView.findViewById(R.id.eventGenre);
            container = itemView.findViewById(R.id.container);
        }

        public void bind(FirebaseRecyclerOptions<Event> event) {
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

    } /*


} */
