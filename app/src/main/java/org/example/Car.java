package org.example;

import org.example.RaceTrack.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.Graphics2D;

public class Car {
    Point position;
    double angle;
    double deltaRotation;
    double speed;
    double acceleration;
    double turnSpeed;
    double maxSpeed;
    String carImagePath;
    int imageHeight;
    int imageWidth;
    int newImageWidth;
    int newImageHeight;
    int containerWidth;
    int containerHeight;
    double scale;
    BufferedImage carImage;
    BufferedImage renderImage;
    AffineTransform transform;
    AffineTransformOp op;
    
    public Car(Point position, double angle, double speed, double acceleration, double turnSpeed, double maxSpeed, String carImagePath, int imageHeight) {
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

            this.imageHeight = imageHeight;
            this.imageWidth = carImage.getWidth() * imageHeight / carImage.getHeight();
            this.scale = (double) imageHeight / carImage.getHeight();
            this.containerHeight = imageHeight;
            this.containerWidth = imageWidth;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update(RaceTrack raceTrack) {
        double radians = Math.toRadians(angle);
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);
        double cosAbs = Math.abs(cos);
        double sinAbs = Math.abs(sin);

        // Update the car's position based on its speed and angle
        position.x += speed * sin;
        position.y -= speed * cos;

        // Check for collision
        if (Collision.checkCollision(this, raceTrack)) {
            // Collision detected, stop the car
            speed = 0; // Stop the car
            System.out.println("Collision detected! Car stopped.");
        }

        // Update the car's rotation for rendering
        newImageWidth = (int) (carImage.getWidth() * cosAbs + carImage.getHeight() * sinAbs);
        newImageHeight = (int) (carImage.getWidth() * sinAbs + carImage.getHeight() * cosAbs);

        containerWidth = (int) (newImageWidth * scale);
        containerHeight = (int) (newImageHeight * scale);

        System.out.println("Container width: " + newImageWidth + ", Container height: " + newImageHeight + " angle: " + angle);

        BufferedImage rotated = new BufferedImage(newImageWidth, newImageHeight, carImage.getType());
        Graphics2D graphic = rotated.createGraphics();

        graphic.translate((newImageWidth - carImage.getWidth()) / 2, (newImageHeight - carImage.getHeight()) / 2);
        graphic.rotate(radians, carImage.getWidth() / 2, carImage.getHeight() / 2);
        graphic.drawRenderedImage(carImage, null);
        graphic.dispose();

        renderImage = rotated;
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
        speed -= acceleration;

        if (speed < -maxSpeed) {
            speed = -maxSpeed;
        }
    }
}
