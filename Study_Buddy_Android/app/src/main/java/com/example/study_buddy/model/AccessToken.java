package com.example.study_buddy.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AccessToken {
    @SerializedName("access_token")
    private String accessToken;
    @SerializedName("scope")
    private String scope;
    @SerializedName("expires_in")
    private int expiresIn;
    @SerializedName("token_type")
    private String tokenType;
    @SerializedName("id_token")
    private String idToken;
    @SerializedName("refresh_token")
    private String refreshToken;

    private String firstName;

    private String LastName;

    private String email;



    public AccessToken(String accessToken, String scope, int expiresIn, String tokenType, String idToken, String refreshToken) {
        this.accessToken = accessToken;
        this.scope = scope;
        this.expiresIn = expiresIn;
        this.tokenType = tokenType;
        this.idToken = idToken;
        this.refreshToken = refreshToken;
    }

    public AccessToken(AccessToken a) {
        this.accessToken = a.getAccessToken();
        this.scope = a.getScope();
        this.expiresIn = a.getExpiresIn();
        this.tokenType = a.getTokenType();
        this.idToken =  a.getIdToken();
        this.refreshToken = a.getRefreshToken();
        this.firstName = a.getFirstName();
        this.LastName = a.getLastName();
        this.email = a.getEmail();
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public int getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(int expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getFirstName() { return firstName; }

    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return LastName; }

    public void setLastName(String lastName) { LastName = lastName; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }
}
