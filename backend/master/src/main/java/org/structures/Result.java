package org.structures;

import java.io.Serializable;
import java.util.ArrayList;

public class Result implements Serializable {
    private double totalDistance;
    private double totalElevation;
    private double totalTime;

    public Result() {
    }

    public Result(double totalDistance, double totalElevation, double totalTime) {
        this.totalDistance = totalDistance;
        this.totalElevation = totalElevation;
        this.totalTime = totalTime;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(double totalDistance) {
        this.totalDistance = totalDistance;
    }

    public double getTotalElevation() {
        return totalElevation;
    }

    public void setTotalElevation(double totalElevation) {
        this.totalElevation = totalElevation;
    }

    public double getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(double totalTime) {
        this.totalTime = totalTime;
    }

    @Override
    public String toString() {
        return "Result{" +
                "totalDistance=" + totalDistance +
                ", totalElevation=" + totalElevation +
                ", totalTime=" + totalTime +
                ", averageSpeed=" + (totalDistance/totalTime) +
                '}';
    }
}
