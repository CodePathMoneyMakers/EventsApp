package com.example.eventsapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eventsapp.R;
import com.example.eventsapp.models.User;

import org.jetbrains.annotations.NotNull;

import java.text.BreakIterator;
import java.util.ArrayList;

public class RSVPRecyclerAdapter extends RecyclerView.ViewHolder {


   public ImageView profileImage;
   public     TextView tvEmail, tvEventTitle, tvFullName;
    public View view;

        public RSVPRecyclerAdapter(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.ivProfileImage);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvEventTitle = itemView.findViewById(R.id.eventTitle);
            tvFullName = itemView.findViewById(R.id.tvFullName);

            view = itemView;
        }

}
