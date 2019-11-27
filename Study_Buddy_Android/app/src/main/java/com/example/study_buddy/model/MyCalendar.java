package com.example.study_buddy.model;

import java.util.List;

public class MyCalendar {

    private List<Event> mEvents;

    public MyCalendar(List<Event> mEvents) {
        this.mEvents = mEvents;
    }

    public List<Event> getmEvents() {
        return mEvents;
    }

    public void setmEvents(List<Event> mEvents) {
        this.mEvents = mEvents;
    }

}
