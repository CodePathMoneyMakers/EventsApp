package com.example.eventsapp.adapters;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventsapp.R;

public class MessageAdapter extends RecyclerView.ViewHolder {
    public ImageView userProfileImage;

    public MessageAdapter(@NonNull View itemView) {
        super(itemView);
        userProfileImage = itemView.findViewById(R.id.message_profile_image);
    }
}
