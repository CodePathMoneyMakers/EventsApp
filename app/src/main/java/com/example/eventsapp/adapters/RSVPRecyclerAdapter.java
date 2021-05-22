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

import java.util.ArrayList;

public class RSVPRecyclerAdapter extends RecyclerView.Adapter<RSVPRecyclerAdapter.ViewHolder> {

    private ArrayList<User> mUsers = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.rsvp_recycler_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvEmail.setText("email");
        holder.tvFullName.setText("Name");
        holder.tvEventTitle.setText("title");
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView profileImage;
        TextView tvEmail, tvEventTitle, tvFullName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.ivProfileImage);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvEventTitle = itemView.findViewById(R.id.eventTitle);
            tvFullName = itemView.findViewById(R.id.tvFullName);


        }
    }
}
