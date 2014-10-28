package com.vkuzub.taximeter.model;

/**
 * Created by Vyacheslav on 28.10.2014.
 */
public class Trip {

    private String date;
    private String sum;
    private String distance;

    public Trip(String date, String sum, String distance) {
        this.date = date;
        this.sum = sum;
        this.distance = distance;
    }

    public String getDate() {
        return date;
    }

    public String getSum() {
        return sum;
    }

    public String getDistance() {
        return distance;
    }

    @Override
    public String toString() {
        return date + " " + sum + " " + distance;
    }
}
