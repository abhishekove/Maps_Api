package com.dev.maps;

public class parking {
    public parking() {
    }

    private String fCapacity;
    private String tCapacity;
    private String time;
    private String name;

    public parking(String fCapacity, String tCapacity, String time, String name, String lng, String lat, String owner) {
        this.fCapacity = fCapacity;
        this.tCapacity = tCapacity;
        this.time = time;
        this.name = name;
        this.lng = lng;
        this.lat = lat;
        Owner = owner;
    }

    private String lng,lat;

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public parking(String fCapacity, String tCapacity, String time, String lng, String lat, String owner) {
        this.fCapacity = fCapacity;
        this.tCapacity = tCapacity;
        this.time = time;
        this.lng = lng;
        this.lat = lat;
        Owner = owner;
    }

    public String getfCapacity() {
        return fCapacity;
    }

    public void setfCapacity(String fCapacity) {
        this.fCapacity = fCapacity;
    }

    public String gettCapacity() {
        return tCapacity;
    }

    public void settCapacity(String tCapacity) {
        this.tCapacity = tCapacity;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getOwner() {
        return Owner;
    }

    public void setOwner(String owner) {
        Owner = owner;
    }

    public parking(String fCapacity, String tCapacity, String time, String owner) {
        this.fCapacity = fCapacity;
        this.tCapacity = tCapacity;
        this.time = time;
        Owner = owner;
    }

    private String Owner;
}
