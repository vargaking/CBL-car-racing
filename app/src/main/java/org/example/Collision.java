package org.example;

import java.awt.geom.Line2D;

import org.example.Car.Collided;

public class Collision {

    public static Collided checkCollision(Car car, RaceTrack raceTrack) {
        // Check collision with inner walls
        for (RaceTrack.Wall wall : raceTrack.innerWalls) {
            Line2D wallLine = new Line2D.Double(wall.start.x, wall.start.y, wall.end.x, wall.end.y);
            for (Line2D carLine : car.carHitbox) {
                if (carLine.intersectsLine(wallLine)) {
                    if (car.speed > 0) {
                        return Collided.GOINGFORWARD; // Collision detected while going forward
                    } else {
                        return Collided.GOINGBACKWARD; // Collision detected while going backward
                    }
                }
            }
        }

        // Check collision with outer walls
        for (RaceTrack.Wall wall : raceTrack.outerWalls) {
            Line2D wallLine = new Line2D.Double(wall.start.x, wall.start.y, wall.end.x, wall.end.y);
            for (Line2D carLine : car.carHitbox) {
                if (carLine.intersectsLine(wallLine)) {
                    if (car.speed > 0) {
                        return Collided.GOINGFORWARD; // Collision detected while going forward
                    } else {
                        return Collided.GOINGBACKWARD; // Collision detected while going backward
                    }
                }
            }
        }

        return Collided.FALSE; // No collision
    }

    public static Collided checkCollisionBetweenCars(Car car1, Car car2) {
        // Check collision between two cars
        for (Line2D car1Line : car1.carHitbox) {
            for (Line2D car2Line : car2.carHitbox) {
                if (car1Line.intersectsLine(car2Line)) {
                    return Collided.TRUE; // Collision detected
                }
            }
        }

        return Collided.FALSE; // No collision
    }

}
