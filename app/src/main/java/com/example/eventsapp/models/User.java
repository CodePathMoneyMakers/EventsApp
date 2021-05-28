package com.example.eventsapp.models;

import android.os.Parcel;
import android.os.Parcelable;

public class User {

    public String fullName, age, email, userImage;

    private String user_id = "user_id";
    private String username = "username";
    private String avatar;

    // Default empty constructor
    public User(){}

    public User(String fullName, String age, String email, String userImage){
        this.fullName = fullName;
        this.age = age;
        this.email = email;
        this.userImage = userImage;
    }

    protected User(Parcel in) {
        fullName = in.readString();
        age = in.readString();
        email = in.readString();
        user_id = in.readString();
        username = in.readString();
        avatar = in.readString();
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

    public String getFullName(){
        return fullName;
    }

    public String getUserImage(){
        return userImage;
    }



}
