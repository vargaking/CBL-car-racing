package org.example;

public class RaceResult {
    private String carName;
    private double totalTime;
    private double fastestLap;

    public RaceResult(String carName, double totalTime, double fastestLap) {
        this.carName = carName;
        this.totalTime = totalTime;
        this.fastestLap = fastestLap;
    }

    public String getCarName() {
        return carName;
    }

    public double getTotalTime() {
        return totalTime;
    }

    public double getFastestLap() {
        return fastestLap;
    }
}