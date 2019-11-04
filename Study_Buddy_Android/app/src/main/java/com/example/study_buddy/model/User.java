package com.example.study_buddy.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class User {
    @SerializedName("calendarList")
    private List<String> calendarList;
    @SerializedName("groupList")
    private List<String> groupList;
    @SerializedName("friendList")
    private List<String> friendList;
    @SerializedName("suggestedFriendList")
    private List<String> suggestedFriendList;
    @SerializedName("_id")
    private String id;
    @SerializedName("firstName")
    private String firstName;
    @SerializedName("lastName")
    private String lastName;
    @SerializedName("email")
    private String email;
    @SerializedName("password")
    private String password;
    @SerializedName("createdAt")
    private String createAt;
    @SerializedName("updatedAt")
    private String updateAt;
    @SerializedName("__v")
    private int v;

    public User(List<String> calendarList, List<String> groupList, List<String> friendList,
                List<String> suggestedFriendList, String id, String firstName, String lastName,
                String email, String password, String createAt, String updateAt, int v) {
        this.calendarList = calendarList;
        this.groupList = groupList;
        this.friendList = friendList;
        this.suggestedFriendList = suggestedFriendList;
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.createAt = createAt;
        this.updateAt = updateAt;
        this.v = v;
    }

    public User(String email, String password, String id) {
        this.email = email;
        this.id = id;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }


    public String getPassword() {
        return password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setFristName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getid() {
        return id;
    }

    public void setid(String _id) {
        this.id = _id;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(String updateAt) {
        this.updateAt = updateAt;
    }

    public List<String> getCalendarList() {
        return calendarList;
    }

    public void setCalendarList(List<String> calendarList) {
        this.calendarList = calendarList;
    }

    public List<String> getGroupList() {
        return groupList;
    }

    public void setGroupList(List<String> groupList) {
        this.groupList = groupList;
    }

    public List<String> getFriendList() {
        return friendList;
    }

    public void setFriendList(List<String> friendList) {
        this.friendList = friendList;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public int getv() {
        return v;
    }

    public void setv(int v) {
        this.v = v;
    }

    public List<String> getSuggestedFriendList() {
        return suggestedFriendList;
    }

    public void setSuggestedFriendList(List<String> suggestedFriendList) {
        this.suggestedFriendList = suggestedFriendList;
    }
}

