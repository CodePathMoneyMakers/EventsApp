package com.example.eventsapp.models;

import android.os.Message;

import com.example.eventsapp.Event;
import com.example.eventsapp.EventsAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;

import java.util.Date;

public class ChatMessage { // chat

    private String messageText;
    private String messageUser;
    private String messageUserID;
    private long messageTime;

    //private String userImage;

    public ChatMessage(String messageText, String messageUser, String messageUserID) {
        this.messageText = messageText;
        this.messageUser = messageUser;
        this.messageUserID = messageUserID;
        messageTime = new Date().getTime();         // Initialize to current time
        //this.userImage = userImage;
    }

    public ChatMessage(){
    }

//    public String getUserImage() {
//        return userImage;
//    }
//    public void setUserImage(String userImage) {
//        this.userImage = userImage;
//    }

    public String getMessageUserID(){
        return this.messageUserID;
    }
    public void setMessageUserID(String messageUserID) {
        this.messageUserID = messageUserID;
    }

    public String getMessageText() {
        return messageText;
    }
    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUser() {
        return messageUser;
    }
    public void setMessageUser(String messageUser) {
        this.messageUser = messageUser;
    }

    public long getMessageTime() {
        return messageTime;
    }
    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }
}
