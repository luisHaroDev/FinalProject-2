package com.edu.chat.model;

/**
 * Class to hold data of user: user name, password, offline (is set from start of application and after logout) or
 * online status (is set only if user logged in).
 */
public class Account {
    private String username;
    private String password;
    private AccountStatus status;

    public Account(String username, String password, AccountStatus status) {
        this.username = username;
        this.password = password;
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    /**
     * @return the string that have a view as in database (all properties separated by ;)
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(username + ";" + password + ";" + status.getValue() + "\n");
        return sb.toString();
    }
}
