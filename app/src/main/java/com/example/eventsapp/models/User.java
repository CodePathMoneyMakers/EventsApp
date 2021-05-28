package com.example.eventsapp.models;

import android.os.Parcel;
import android.os.Parcelable;

public class User {

    public String fullName, age, email, userImage, bio;

    private String avatar;

    // Default empty constructor
    public User(){}

    public User(String fullName, String age, String email, String userImage, String bio){
        this.fullName = fullName;
        this.age = age;
        this.email = email;
        this.userImage = userImage;
        this.bio = bio;
    }

    protected User(Parcel in) {
        fullName = in.readString();
        age = in.readString();
        email = in.readString();
        avatar = in.readString();
        bio = in.readString();
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