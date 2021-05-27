package com.example.eventsapp;

public class SpinnerItem {
    private String eventType;
    private int eventTypeIcon;

    public SpinnerItem(String eventType, int eventTypeIcon) {
        this.eventType = eventType;
        this.eventTypeIcon = eventTypeIcon;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public int getEventTypeIcon() {
        return eventTypeIcon;
    }

    public void setEventTypeIcon(int eventTypeIcon) {
        this.eventTypeIcon = eventTypeIcon;
    }
}
