package com.example.eventsapp.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Chatroom implements Parcelable {

    private String title;
    private String chatroom_id;


    public Chatroom(String title, String chatroom_id) {
        this.title = title;
        this.chatroom_id = chatroom_id;
    }

    public Chatroom() {

    }

    protected Chatroom(Parcel in) {
        title = in.readString();
        chatroom_id = in.readString();
    }

    public static final Creator<Chatroom> CREATOR = new Creator<Chatroom>() {
        @Override
        public com.example.eventsapp.models.Chatroom createFromParcel(Parcel in) {
            return new com.example.eventsapp.models.Chatroom(in);
        }

        @Override
        public com.example.eventsapp.models.Chatroom[] newArray(int size) {
            return new com.example.eventsapp.models.Chatroom[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getChatroom_id() {
        return chatroom_id;
    }

    public void setChatroom_id(String chatroom_id) {
        this.chatroom_id = chatroom_id;
    }

    @Override
    public String toString() {
        return "Chatroom{" +
                "title='" + title + '\'' +
                ", chatroom_id='" + chatroom_id + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(chatroom_id);
    }
}
