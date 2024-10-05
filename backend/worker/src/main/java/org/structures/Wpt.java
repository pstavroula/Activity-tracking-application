package org.structures;

import java.io.Serializable;

public class Wpt implements Serializable {
    private double latitude;
    private double lon;
    private double ele;
    private String time;

    public Wpt() {
    }

    public Wpt(double latitude, double lon, double ele, String time) {
        this.latitude = latitude;
        this.lon = lon;
        this.ele = ele;
        this.time = time;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getEle() {
        return ele;
    }

    public void setEle(double ele) {
        this.ele = ele;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "Wpt{" +
                "latitude=" + latitude +
                ", lon=" + lon +
                ", ele=" + ele +
                ", time='" + time + '\'' +
                '}';
    }
}

