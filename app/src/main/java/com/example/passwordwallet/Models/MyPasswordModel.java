package com.example.passwordwallet.Models;

public class MyPasswordModel {

    private String id, password, userId, webAddress, description, login;

    public MyPasswordModel(String id, String password, String userId, String webAddress, String description, String login) {
        this.id = id;
        this.password = password;
        this.userId = userId;
        this.webAddress = webAddress;
        this.description = description;
        this.login = login;
    }

    public String getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }

    public String getUserId() {
        return userId;
    }

    public String getWebAddress() {
        return webAddress;
    }

    public String getDescription() {
        return description;
    }

    public String getLogin() {
        return login;
    }
}
