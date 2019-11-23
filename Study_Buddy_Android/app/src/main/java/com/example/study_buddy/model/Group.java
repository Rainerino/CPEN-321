package com.example.study_buddy.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Group {
    @SerializedName("groupName")
    private String groupName;
    @SerializedName("groupDescription")
    private String groupDescription;
    @SerializedName("location")
    private String location;
    @SerializedName("calendarId")
    private String calendarId;
    @SerializedName("userList")
    private List<User> userList;

    public Group(String groupName, String groupDescription, String location, String calendarId, List<User> userList) {
        this.groupName = groupName;
        this.groupDescription = groupDescription;
        this.location = location;
        this.calendarId = calendarId;
        this.userList = userList;
    }

    public Group(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupDescription() {
        return groupDescription;
    }

    public void setGroupDescription(String groupDescription) {
        this.groupDescription = groupDescription;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(String calendarId) {
        this.calendarId = calendarId;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }
}
