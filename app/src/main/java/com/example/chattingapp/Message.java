package com.example.chattingapp;

public class Message {
    public String message;
    public String created;
    public String user;
    public String chatRoom;

    public Message(String message, String created, String user, String chatRoom) {
        this.message = message;
        this.created = created;
        this.user = user;
        this.chatRoom = chatRoom;
    }

    public Message() {
    }
}