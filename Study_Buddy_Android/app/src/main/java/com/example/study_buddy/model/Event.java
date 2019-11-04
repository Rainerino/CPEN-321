package com.example.study_buddy.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;


public class Event {
    @SerializedName("eventName")
    private String eventName;
    @SerializedName("eventDescription")
    private String eventDescription;
    @SerializedName("startTime")
    private Date startTime;
    @SerializedName("endTime")
    private Date endTime;
    @SerializedName("ownerId")
    private String ownerId;
    @SerializedName("repeatType")
    private String repeatType;
    @SerializedName("calendarId")
    private String calendarId;
    @SerializedName("eventType")
    private String eventType;
    @SerializedName("userList")
    private ArrayList<User> userList;
    @SerializedName("groupList")
    private ArrayList<String> groupList;
    @SerializedName("notified")
    private Boolean notified;

    public Event(String eventName, String eventDescription, Date startTime, Date endTime, String ownerId, String repeatType, String eventType, ArrayList<User> userList) {
        this.eventName = eventName;
        this.eventDescription = eventDescription;
        this.startTime = startTime;
        this.endTime = endTime;
        this.ownerId = ownerId;
        this.repeatType = repeatType;
        this.eventType = eventType;
        this.userList = userList;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getRepeatType() {
        return repeatType;
    }

    public void setRepeatType(String repeatType) {
        this.repeatType = repeatType;
    }

    public String getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(String calendarId) {
        this.calendarId = calendarId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public ArrayList<User> getUserList() {
        return userList;
    }

    public void setUserList(ArrayList<User> userList) {
        this.userList = userList;
    }

    public ArrayList<String> getGroupList() {
        return groupList;
    }

    public void setGroupList(ArrayList<String> groupList) {
        this.groupList = groupList;
    }

    public Boolean getNotified() {
        return notified;
    }

    public void setNotified(Boolean notified) {
        this.notified = notified;
    }
}
