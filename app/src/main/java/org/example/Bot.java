package org.example;

import java.util.ArrayList;
import java.util.List;

import javax.naming.directory.SearchResult;

import org.example.Car.Moves;

public class Bot {
    BotCar botPlayer;
    BotCar simulatedPlayer;
    RaceTrack raceTrack;
    int maxDepth;

    // Store the best sequence of moves
    private static class SearchResult {
        Moves firstMove;
        int score;
        
        SearchResult(Moves firstMove, int score) {
            this.firstMove = firstMove;
            this.score = score;
        }
    }

    Bot(BotCar botPlayer, RaceTrack raceTrack, int maxDepth) {
        this.botPlayer = botPlayer;
        // deep copy of the player
        this.simulatedPlayer = new BotCar(botPlayer.position, botPlayer.angle, botPlayer.speed, botPlayer.acceleration, botPlayer.turnSpeed, botPlayer.maxSpeed, botPlayer.carImagePath, botPlayer.imageHeight, botPlayer.laps);
        this.raceTrack = raceTrack;
        this.maxDepth = maxDepth;
    }

    public int evaluateState(Car car) {
        // Evaluate the state of the game
        int score = 0;

        // Check the speed of the car, the higher the speed, the higher the score
        score += car.speed * 3;

        // Check the distance to the finish line, the closer the car is to the finish line, the higher the score
        int distanceToFinishLine = raceTrack.distanceToFinishLine(car, false);
        score += (raceTrack.length / 4 - distanceToFinishLine) / 2;

        // Check the number of laps completed, the more laps completed, the higher the score
        score += (car.laps - botPlayer.laps) * 3000;

        // Check if the car has collided with the wall or another car
        if (car.collided != Car.Collided.FALSE) {
            score -= 5000;
        }

        return score;
    }

    private SearchResult simulateRecursive(int depth, List<Moves> previousMoves) {
        // Base case: reached maximum depth
        if (depth == maxDepth) {
            return new SearchResult(previousMoves.get(0), evaluateState(simulatedPlayer));
        }
        
        int bestScore = Integer.MIN_VALUE;
        Moves bestFirstMove = null;
        
        // Try each possible move
        for (Moves move : Moves.values()) {
            // Save current state
            BotCar.CarState savedState = simulatedPlayer.saveState();
            
            // Apply the move
            applyMove(simulatedPlayer, move);
            simulatedPlayer.updateWithoutRender(raceTrack, 5); // Update physics
            if (raceTrack.isCarCrossedFinishLine(simulatedPlayer)) {
                simulatedPlayer.laps++;
            }; // Check for collisions

            // For the first move, save it. For subsequent moves, use the first move from previousMoves
            List<Moves> newMoves = new ArrayList<>(previousMoves);
            if (depth == 0) {
                newMoves.add(move);
            }
            
            // Recursive simulation
            SearchResult result = simulateRecursive(depth + 1, newMoves);
            
            // Update best score
            if (result.score > bestScore) {
                bestScore = result.score;
                bestFirstMove = depth == 0 ? move : result.firstMove;
            }
            
            // Restore state for next iteration
            simulatedPlayer.restoreState(savedState);
        }
        
        return new SearchResult(bestFirstMove, bestScore);
    }

    private void applyMove(Car car, Moves move) {
        switch (move) {
            case ACCELERATE:
                car.accelerate();
                break;
            case BRAKE:
                car.brake();
                break;
            case TURN_LEFT:
                car.turnLeft();
                break;
            case TURN_RIGHT:
                car.turnRight();
                break;
            case NOTHING:
                // Do nothing
                break;
        }
    }

    public Moves searchBestMove() {
        // Create a fresh simulated player for this search
        simulatedPlayer = new BotCar(
            botPlayer.position,
            botPlayer.angle,
            botPlayer.speed,
            botPlayer.acceleration,
            botPlayer.turnSpeed,
            botPlayer.maxSpeed,
            botPlayer.carImagePath,
            botPlayer.imageHeight,
            botPlayer.laps
        );
        
        // Start the recursive search
        SearchResult result = simulateRecursive(0, new ArrayList<>());
        return result.firstMove;
    }
}
