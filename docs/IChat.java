package com.edu.chat.model;

import java.util.List;

/**
 * Common interface for chats
 */
public interface IChat {
    String getId();
    String getOwner();
    List<Message> getMessageList();
}
