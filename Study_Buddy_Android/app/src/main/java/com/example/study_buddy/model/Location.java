package com.example.study_buddy.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Location {
    @SerializedName("coordinate")
    List<Integer> coordinate;
    @SerializedName("type")
    String type;

    public Location(List<Integer> coordinate, String type) {
        this.coordinate = coordinate;
        this.type = type;
    }

    public List<Integer> getCoordinate() {
        return coordinate;
    }

    public void setCoordinate(List<Integer> coordinate) {
        this.coordinate = coordinate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
