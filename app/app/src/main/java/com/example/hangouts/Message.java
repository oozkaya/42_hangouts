package com.example.hangouts;

import java.io.Serializable;

public class Message implements Serializable {
    public enum Types {
        SENT, RECEIVED
    }

    String message, date;
    Types type;

    public Message(String message, String date, Types type) {
        super();
        this.message = message;
        this.date = date;
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
