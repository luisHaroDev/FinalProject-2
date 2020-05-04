package com.edu.chat.model;

/**
 * Class to hold all data of message
 */
public class Message {
    private String writerUsername;
    private String message;

    public Message(String writerUsername, String message) {
        this.writerUsername = writerUsername;
        this.message = message;
    }

    public String getWriterUsername() {
        return writerUsername;
    }

    public void setWriterUsername(String writerUsername) {
        this.writerUsername = writerUsername;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
