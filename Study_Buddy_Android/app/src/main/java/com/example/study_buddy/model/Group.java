package com.example.study_buddy.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Group {
    @SerializedName("groupName")
    private String groupName;
    @SerializedName("groupDescription")
    private String groupDescription;
    @SerializedName("location")
    private Location location;
    @SerializedName("calendarId")
    private String calendarId;
    @SerializedName("userList")
    private List<String> userList;

    public Group(String groupName, String groupDescription, Location location, String calendarId, List<String> userList) {
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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(String calendarId) {
        this.calendarId = calendarId;
    }

    public List<String> getUserList() {
        return userList;
    }

    public void setUserList(List<String> userList) {
        this.userList = userList;
    }
}
