package org.example;

import java.awt.geom.Line2D;

public class Collision {

    public static boolean checkCollision(Car car, RaceTrack raceTrack) {
        // Get the car's bounding box as a rectangle
        int carWidth = 64; // Assuming the car's width is 64 pixels
        int carHeight = 64; // Assuming the car's height is 64 pixels
        int carX = (int) car.position.x;
        int carY = (int) car.position.y;

        // Create the car's bounding box (rectangle)
        Line2D carLeft = new Line2D.Double(carX, carY, carX, carY + carHeight);
        Line2D carRight = new Line2D.Double(carX + carWidth, carY, carX + carWidth, carY + carHeight);
        Line2D carTop = new Line2D.Double(carX, carY, carX + carWidth, carY);
        Line2D carBottom = new Line2D.Double(carX, carY + carHeight, carX + carWidth, carY + carHeight);

        // Check collision with inner walls
        for (RaceTrack.Wall wall : raceTrack.innerWalls) {
            Line2D wallLine = new Line2D.Double(wall.start.x, wall.start.y, wall.end.x, wall.end.y);
            if (carLeft.intersectsLine(wallLine) || carRight.intersectsLine(wallLine) ||
                carTop.intersectsLine(wallLine) || carBottom.intersectsLine(wallLine)) {
                return true; // Collision detected
            }
        }

        // Check collision with outer walls
        for (RaceTrack.Wall wall : raceTrack.outerWalls) {
            Line2D wallLine = new Line2D.Double(wall.start.x, wall.start.y, wall.end.x, wall.end.y);
            if (carLeft.intersectsLine(wallLine) || carRight.intersectsLine(wallLine) ||
                carTop.intersectsLine(wallLine) || carBottom.intersectsLine(wallLine)) {
                return true; // Collision detected
            }
        }

        return false; // No collision
    }
}

