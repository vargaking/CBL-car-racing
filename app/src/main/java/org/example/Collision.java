package org.example;

import java.awt.geom.Line2D;

import org.example.Car.Collided;

public class Collision {

    public static Collided checkCollision(Car car, RaceTrack raceTrack) {
        // Check collision with inner walls
        for (RaceTrack.Wall wall : raceTrack.innerWalls) {
            Line2D wallLine = new Line2D.Double(wall.start.x, wall.start.y, wall.end.x, wall.end.y);
            if (car.carLeft.intersectsLine(wallLine) || car.carRight.intersectsLine(wallLine) ||
                    car.carTop.intersectsLine(wallLine) || car.carBottom.intersectsLine(wallLine)) {
                if (car.speed > 0) {
                    return Collided.GOINGFORWARD; // Collision detected while going forward
                } else {
                    return Collided.GOINGBACKWARD; // Collision detected while going backward
                }
            }
        }

        // Check collision with outer walls
        for (RaceTrack.Wall wall : raceTrack.outerWalls) {
            Line2D wallLine = new Line2D.Double(wall.start.x, wall.start.y, wall.end.x, wall.end.y);
            if (car.carLeft.intersectsLine(wallLine) || car.carRight.intersectsLine(wallLine) ||
                    car.carTop.intersectsLine(wallLine) || car.carBottom.intersectsLine(wallLine)) {
                if (car.speed > 0) {
                    return Collided.GOINGFORWARD; // Collision detected while going forward
                } else {
                    return Collided.GOINGBACKWARD; // Collision detected while going backward
                }
            }
        }

        return Collided.FALSE; // No collision
    }
}
