package com.example.meet;

public class EventModel {
    private String eventName;
    private String time;
    private String date;
    private String type;

    private double lat;
    private double lng;

    public EventModel()
    {

    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public EventModel(String name, String time, String date, String type, double lat, double lng)
    {
        this.eventName = name;
        this.time = time;
        this.date = date;
        this.type = type;
        this.lat = lat;
        this.lng = lng;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEventName() {
        return eventName;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public String getType() {
        return type;
    }
}
