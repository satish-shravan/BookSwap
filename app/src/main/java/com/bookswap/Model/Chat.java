package com.bookswap.Model;

public class Chat {

    private String sender;
    private String receiver;
    private String message;
    private String dateTime;


    public Chat()
    {

    }

    public Chat(String sender, String receiver, String message,String dateTime) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.dateTime = dateTime;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
