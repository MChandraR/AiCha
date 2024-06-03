package com.mcr.aimcr.models;

public class MessageModel {
    String message;
    String date;

    String sender;

    public MessageModel(String msg,String dat,String sndr){
        message = msg;
        date = dat;
        sender = sndr;
    }

    public String getMessage() {
        return message;
    }

    public String getSender() {
        return sender;
    }

    public String getDate() {
        return date;
    }
}
