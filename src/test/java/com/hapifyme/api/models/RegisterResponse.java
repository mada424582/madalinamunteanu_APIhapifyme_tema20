package com.hapifyme.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public class RegisterResponse {
        private String api_key;
        private String user_id;
        private String confirmation_token;
        private String username;

        // Getters & setters
        public String getApi_key() {
            return api_key;
        }
        public void setApi_key(String api_key) {
            this.api_key = api_key;
        }

        public String getUser_id() {
            return user_id;
        }
        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }
        public String getConfirmation_token() {
            return confirmation_token;
        }
        public String getUsername() {
            return username;
        }
        public void setUsername(String username) {
            this.username = username;
        }
    }

