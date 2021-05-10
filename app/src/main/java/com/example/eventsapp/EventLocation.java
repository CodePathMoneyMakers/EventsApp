package com.example.eventsapp;

import com.example.eventsapp.models.User;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

public class EventLocation {

    private GeoPoint geoPoint;
    private @ServerTimestamp String timestamp;
    private User user;

    public EventLocation(GeoPoint geoPoint, String timestamp, User user) {
        this.geoPoint = geoPoint;
        this.timestamp = timestamp;
        this.user = user;
    }

    public EventLocation() {}

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "EventLocation{" +
                "geoPoint=" + geoPoint +
                ", timestamp='" + timestamp + '\'' +
                ", user=" + user +
                '}';
    }
}
