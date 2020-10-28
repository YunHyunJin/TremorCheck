package com.bcilab.tremorapp.Data;

public class ResultData {
    private int count  ;
    private Double hz ;
    private Double magnitude ;
    private Double distance ;
    private Double time ;
    private Double speed ;
    private String timestamp ;

    public ResultData(int count, Double hz, Double magnitude, Double distance, Double time, Double speed, String timestamp) {
        this.count = count;
        this.hz = hz;
        this.magnitude = magnitude;
        this.distance = distance;
        this.time = time;
        this.speed = speed;
        this.timestamp = timestamp;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Double getHz() {
        return hz;
    }

    public void setHz(Double hz) {
        this.hz = hz;
    }

    public Double getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(Double magnitude) {
        this.magnitude = magnitude;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Double getTime() {
        return time;
    }

    public void setTime(Double time) {
        this.time = time;
    }

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
