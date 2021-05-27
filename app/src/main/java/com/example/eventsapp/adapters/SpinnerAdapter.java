package com.example.eventsapp.adapters;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.eventsapp.R;
import com.example.eventsapp.SpinnerItem;

import java.util.ArrayList;

public class SpinnerAdapter  extends ArrayAdapter<SpinnerItem> {

    public SpinnerAdapter(Context context, ArrayList<SpinnerItem> spinnerItems){
        super(context, 0,  spinnerItems);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent){
        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_layout, parent, false);
        }
        ImageView imageViewIcon = convertView.findViewById(R.id.eventTypeIcon);
        TextView textView = convertView.findViewById(R.id.eventType);

        SpinnerItem spinnerItem = getItem(position);
        if (spinnerItem != null) {
            imageViewIcon.setImageResource(spinnerItem.getEventTypeIcon());
            textView.setText(spinnerItem.getEventType());
        }

        return convertView;
    }
}
