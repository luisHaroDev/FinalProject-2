package com.edu.chat.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Class to hold all data related for Chat room (in database separated by ;)
 */
public class ChatRoom implements IChat {

    private String id;
    private String owner;
    private List<Message> messageList = new ArrayList<>();

    public ChatRoom(String id, String owner) {
        this.id = id;
        this.owner = owner;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    @Override
    public List<Message> getMessageList() {
        return messageList;
    }

    /**
     * @return the string that have a view as in database (all properties separated by ;)
     */
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(id + ";");
        sb.append(owner + ";");
        for (Message message : messageList) {
            sb.append(message.getWriterUsername() + ";");
            sb.append(message.getMessage() + ";");
        }
        sb.append("\n");
        return sb.toString();
    }
}
