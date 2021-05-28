package com.example.eventsapp;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

public class ForYouAdapter extends RecyclerView.ViewHolder{
    public ImageView eventImage;
    public TextView eventTitle;
    public TextView eventGenre;
    public TextView eventFee;
    public TextView eventDay;
    public View view;

    public ForYouAdapter(@NonNull View itemView) {
        super(itemView);

        eventImage = itemView.findViewById(R.id.eventImage);
        eventDay = itemView.findViewById(R.id.eventDate);
        eventTitle = itemView.findViewById(R.id.eventTitle);
        eventGenre = itemView.findViewById(R.id.eventGenre);
        eventFee = itemView.findViewById(R.id.eventFee);

        view = itemView;
    }

}
