package com.edu.chat.model;

/**
 * Enum to hold available Account statuses
 */
public enum AccountStatus {
    OFFLINE("offline"), ONLINE("online");

    String value;

    AccountStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
