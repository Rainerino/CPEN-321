package com.example.study_buddy.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class user {
    @SerializedName("calendarList")
    private List<String> calendarList;
    @SerializedName("groupList")
    private List<String> groupList;
    @SerializedName("friendList")
    private List<String> friendList;
    @SerializedName("suggestedFriendList")
    private List<String> suggestedFriendList;
    @SerializedName("_id")
    private String _id;
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
    private int __v;

    public user(List<String> calendarList, List<String> groupList, List<String> friendList,
                List<String> suggestedFriendList, String _id, String firstName, String lastName,
                String email, String password, String createAt, String updateAt, int __v) {
        this.calendarList = calendarList;
        this.groupList = groupList;
        this.friendList = friendList;
        this.suggestedFriendList = suggestedFriendList;
        this._id = _id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.createAt = createAt;
        this.updateAt = updateAt;
        this.__v = __v;
    }

    public user(String email, String password, String _id) {
        this.email = email;
        this._id = _id;
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

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
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

    public int get__v() {
        return __v;
    }

    public void set__v(int __v) {
        this.__v = __v;
    }

    public List<String> getSuggestedFriendList() {
        return suggestedFriendList;
    }

    public void setSuggestedFriendList(List<String> suggestedFriendList) {
        this.suggestedFriendList = suggestedFriendList;
    }
}

