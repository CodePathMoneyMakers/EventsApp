package com.example.eventsapp.models;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {

    public String fullName, age, email;

    private String user_id = "user_id";
    private String username = "username";
    private String avatar;

    // Default empty constructor
    public User(){}

    public User(String fullName, String age, String email){
        this.fullName = fullName;
        this.age = age;
        this.email = email;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(email);
        dest.writeString(user_id);
        dest.writeString(username);
        dest.writeString(avatar);
    }
}
