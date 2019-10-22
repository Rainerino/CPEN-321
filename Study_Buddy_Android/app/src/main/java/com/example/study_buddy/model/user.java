package com.example.study_buddy.model;

import com.google.gson.annotations.SerializedName;

public class user {
    @SerializedName("firstName")
    private String fristName;
    @SerializedName("lastName")
    private String lastName;
    @SerializedName("email")
    private String email;
    @SerializedName("userName")
    private String userName;
    @SerializedName("password")
    private String password;
    @SerializedName("facebookId")
    private String facebookId;


   public user(String email, String password, String userName){
       this.email = email;
       this.password = password;
       this.userName = userName;
   }

    public String getEmail() {
        return email;
    }

    public String getFristName() {
        return fristName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public void setFristName(String fristName) {
        this.fristName = fristName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}

