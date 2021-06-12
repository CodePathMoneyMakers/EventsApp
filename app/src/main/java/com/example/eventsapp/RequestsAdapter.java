package com.example.eventsapp;

import android.media.Image;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

public class RequestsAdapter extends RecyclerView.ViewHolder {

   public ImageView userImage;
   public TextView userName;

    public RequestsAdapter( @NotNull View itemView) {
        super(itemView);
        userImage = itemView.findViewById(R.id.userImage);
        userName = itemView.findViewById(R.id.userName);
        
    }
}
