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
            String carImagePath, int imageHeight) {
        super(position, angle, speed, acceleration, turnSpeed, maxSpeed, carImagePath, imageHeight);
    }

    // Add this new class for state management
    public static class CarState {
        public final Point2D position;
        public final double angle;
        public final double speed;
        public final double deltaRotation;
        public final Collided collided;
        
        public CarState(Point2D position, double angle, double speed, double deltaRotation, 
                       Collided collided) {
            this.position = new Point2D.Double(position.getX(), position.getY());
            this.angle = angle;
            this.speed = speed;
            this.deltaRotation = deltaRotation;
            this.collided = collided;
        }
    }

    // Add these methods for state management
    public CarState saveState() {
        return new CarState(
            new Point2D.Double(position.getX(), position.getY()),
            angle,
            speed,
            deltaRotation,
            collided
        );
    }

    public void restoreState(CarState state) {
        this.position = new Point2D.Double(state.position.getX(), state.position.getY());
        this.angle = state.angle;
        this.speed = state.speed;
        this.deltaRotation = state.deltaRotation;
        this.collided = state.collided;
        
        // Update render-related properties
        updateRenderPosition();
        calculateCarHitbox();
    }

    // Add a method to create a deep copy of the car
    public Car createCopy() {
        Car copy = new Car(
            new Point2D.Double(position.getX(), position.getY()),
            angle,
            speed,
            acceleration,
            turnSpeed,
            maxSpeed,
            carImagePath,
            imageHeight
        );
        copy.deltaRotation = this.deltaRotation;
        copy.collided = this.collided;
        return copy;
    }
}