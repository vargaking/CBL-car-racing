package org.example;

import org.example.RaceTrack.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;

public class Car {
    Point position;
    double angle;
    double deltaRotation;
    double speed;
    double acceleration;
    double turnSpeed;
    double maxSpeed;
    String carImagePath;
    BufferedImage carImage;
    AffineTransform transform;
    AffineTransformOp op;
    
    public Car(Point position, double angle, double speed, double acceleration, double turnSpeed, double maxSpeed, String carImagePath) {
        this.position = position;
        this.angle = angle;
        this.deltaRotation = 0;
        this.speed = speed;
        this.acceleration = acceleration;
        this.turnSpeed = turnSpeed;
        this.maxSpeed = maxSpeed;
        this.carImagePath = carImagePath;
        this.transform = new AffineTransform();
        this.op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);

        // Load car image
        try {
            carImage = ImageIO.read(getClass().getClassLoader().getResource(carImagePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update(RaceTrack raceTrack) {
        // Update the car's position based on its speed and angle
        position.x += speed * Math.sin(Math.toRadians(angle));
        position.y -= speed * Math.cos(Math.toRadians(angle));

        // Check for collision
        if (Collision.checkCollision(this, raceTrack)) {
            // Collision detected, stop the car
            speed = 0; // Stop the car
            System.out.println("Collision detected! Car stopped.");
        }

        // Update the car's rotation for rendering
        transform = AffineTransform.getRotateInstance(Math.toRadians(angle), carImage.getWidth() / 2, carImage.getHeight() / 2);
        op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
    }

    public void accelerate() {
        // Increase the car's speed
        speed += acceleration;

        System.out.println("Speed: " + speed);

        if (speed > maxSpeed) {
            speed = maxSpeed;
        }
    }

    public void turnLeft() {
        // Turn the car to the left
        angle -= turnSpeed;
        deltaRotation = -turnSpeed;
    }

    public void turnRight() {
        // Turn the car to the right
        angle += turnSpeed;
        deltaRotation = turnSpeed;
    }

    public void brake() {
        // Slow down the car
        if (speed > 0) {
            speed -= acceleration;
            if (speed < 0) {
                speed = 0;
            }
        } else if (speed < 0) {
            speed += acceleration;
            if (speed > 0) {
                speed = 0;
            }
        }
    }
}
