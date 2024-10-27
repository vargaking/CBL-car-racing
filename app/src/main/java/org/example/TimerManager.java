package org.example;

public class TimerManager {
    private long startTime; // Start time for the race
    private long lapStartTime; // Start time for the current lap
    private int lapsCompleted; // Number of laps completed
    private double totalRaceTime; // Total time for the race in seconds
    public int fastestLap; // Fastest lap time in seconds

    public TimerManager() {
        startTime = System.currentTimeMillis();
        lapStartTime = startTime; // Initialize lap start time
        lapsCompleted = 0;
        totalRaceTime = 0.0; // Initialize total race time
        fastestLap = Integer.MAX_VALUE; // Initialize fastest lap time to maximum value
    }

    public void lapCompleted() {
        // Increment completed laps and calculate lap time
        lapsCompleted++;
        long lapTime = System.currentTimeMillis() - lapStartTime; // Calculate lap time in milliseconds
        totalRaceTime += lapTime / 1000.0; // Update total race time in seconds
        lapStartTime = System.currentTimeMillis(); // Reset lap start time for the next lap

        if (lapTime < fastestLap) {
            fastestLap = (int) (lapTime / 1000); // Update fastest lap time in seconds
        }
    }

    public double getCurrentLapTime() {
        // Calculate current lap time since the last lap started
        return (System.currentTimeMillis() - lapStartTime) / 1000.0; // Return current lap time in seconds
    }

    public double getTotalRaceTime() {
        // Return total race time in seconds
        return totalRaceTime + (System.currentTimeMillis() - startTime) / 1000.0; // Total time of completed laps plus elapsed time
    }

    public int getLapsCompleted() {
        return lapsCompleted; // Return number of completed laps
    }

    public boolean isRaceComplete(int totalLaps) {
        return lapsCompleted >= totalLaps; // Check if the total laps are completed
    }
}
