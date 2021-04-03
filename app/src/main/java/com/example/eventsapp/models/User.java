package com.example.eventsapp.models;

public class User {

    public String fullName, age, email;

    // if we create an empty object of this class
    // we have access to the variables via a
    // default empty constructor.
    public User(){}

    public User(String fullName, String age, String email){
        this.fullName = fullName;
        this.age = age;
        this.email = email;
    }
}
