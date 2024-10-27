package org.example;

public class RaceResult {
    private String carName;
    private double lapsCompleted;
    private double fastestLap;

    public RaceResult(String carName, double lapsCompleted, double fastestLap) {
        this.carName = carName;
        this.lapsCompleted = lapsCompleted;
        this.fastestLap = fastestLap;
    }

    public String getCarName() {
        return carName;
    }

    public double getLapsCompleted() {
        return lapsCompleted;
    }

    public double getFastestLap() {
        return fastestLap;
    }
}