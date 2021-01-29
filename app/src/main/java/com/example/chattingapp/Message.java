package com.example.chattingapp;

public class Message {
    public String message;
    public String created;
    public String user;

    public Message(String message, String created, String user) {
        this.message = message;
        this.created = created;
        this.user = user;
    }

    public Message() {
    }
}