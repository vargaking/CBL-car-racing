package org.example;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import org.example.Car.Collided;

public class BotCar extends Car {
    public BotCar(Point2D position, double angle, double speed, double acceleration, double turnSpeed, double maxSpeed,
            String carImagePath, int imageHeight, int laps) {
        super(position, angle, speed, acceleration, turnSpeed, maxSpeed, carImagePath, imageHeight, laps);
    }

    // Add this new class for state management
    public static class CarState {
        public final Point2D position;
        public final double angle;
        public final double speed;
        public final double deltaRotation;
        public final Collided collided;
        public final int laps;
        
        public CarState(Point2D position, double angle, double speed, double deltaRotation, 
                       Collided collided, int laps) {
            this.position = new Point2D.Double(position.getX(), position.getY());
            this.angle = angle;
            this.speed = speed;
            this.deltaRotation = deltaRotation;
            this.collided = collided;
            this.laps = laps;
        }
    }

    // Add these methods for state management
    public CarState saveState() {
        return new CarState(
            new Point2D.Double(position.getX(), position.getY()),
            angle,
            speed,
            deltaRotation,
            collided,
            laps
        );
    }

    public void restoreState(CarState state) {
        this.position = new Point2D.Double(state.position.getX(), state.position.getY());
        this.angle = state.angle;
        this.speed = state.speed;
        this.deltaRotation = state.deltaRotation;
        this.collided = state.collided;
        this.laps = state.laps;
        
        // Update render-related properties
        updateRenderPosition();
        calculateCarHitbox();
    }

    public void updateWithoutRender(RaceTrack raceTrack) {
        updateWithoutRender(raceTrack, 1);
    }

    public void updateWithoutRender(RaceTrack raceTrack, int count) {
        double radians = Math.toRadians(angle);
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);
        double cosAbs = Math.abs(cos);
        double sinAbs = Math.abs(sin);

        updateRenderPosition();
        calculateCarHitbox();

        if (collided == Collided.FALSE)
            collided = Collision.checkCollision(this, raceTrack);
        else if (Collision.checkCollision(this, raceTrack) == Collided.FALSE)
            collided = Collided.FALSE;

        // Check for collision
        if (collided == Collided.GOINGFORWARD) {
            if (speed > 0) {
                speed = 0;
            }
        } else if (collided == Collided.GOINGBACKWARD) {
            if (speed < 0) {
                speed = 0;
            }
        }

        // Update the car's position based on its speed and angle
        position.setLocation(position.getX() + speed * sin * count, position.getY() - speed * cos * count);
    }

    public double getFastestLap() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getFastestLap'");
    }

    public double getTotalRaceTime() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getTotalRaceTime'");
    }
}