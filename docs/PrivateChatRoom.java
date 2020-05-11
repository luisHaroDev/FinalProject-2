package com.edu.chat.model;

import java.util.List;

/**
 * Class to hold all data related for Private Chat room (in database separated by ;)
 */
public class PrivateChatRoom implements IChat {
    private String id;
    private String owner;
    private String companion;
    private List<Message> messageList;

    public PrivateChatRoom(String id, String owner, String companion, List<Message> messageList) {
        this.id = id;
        this.owner = owner;
        this.companion = companion;
        this.messageList = messageList;
    }

    @Override
    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getCompanion() {
        return companion;
    }

    public void setCompanion(String companion) {
        this.companion = companion;
    }

    @Override
    public List<Message> getMessageList() {
        return messageList;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the string that have a view as in database (all properties separated by ;)
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(id + ";");
        sb.append(owner + ";");
        sb.append(companion + ";");
        for (Message message : messageList) {
            sb.append(message.getWriterUsername() + ";");
            sb.append(message.getMessage() + ";");
        }
        sb.append("\n");
        return sb.toString();
    }
}
