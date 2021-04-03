package com.example.eventsapp;

import android.widget.TextView;

public class Event {

    public String eventName;

    // if we create an empty object of this class
    // we have access to the variables via a
    // default empty constructor.
    public Event(){}

    public Event(String eventName){
        this.eventName = eventName;
    }

}
