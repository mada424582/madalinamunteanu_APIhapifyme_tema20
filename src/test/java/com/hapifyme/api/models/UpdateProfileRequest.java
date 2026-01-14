package com.hapifyme.api.models;

public class UpdateProfileRequest {
    private String first_name;
    private String last_name;
    private String user_id;
    private String email;

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getEmail() {
        return email;
    }

    public UpdateProfileRequest (String first_name, String last_name, String user_id, String email) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.user_id = user_id;
        this.email = email;
    }
}
