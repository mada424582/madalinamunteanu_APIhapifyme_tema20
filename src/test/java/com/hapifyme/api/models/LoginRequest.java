package com.hapifyme.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LoginRequest {

    @JsonProperty("username")
    private String userName;

    @JsonProperty("password")
    private String password;

    // Constructor
    public LoginRequest(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    // Getters & Setters
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
