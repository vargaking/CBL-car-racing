package org.example;

import org.example.RaceTrack.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class Car {
    Point2D renderPosition = new Point2D.Double(0, 0);
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
    Line2D carLeft;
    Line2D carRight;
    Line2D carTop;
    Line2D carBottom;

    Collided collided = Collided.FALSE;

    BufferedImage carImage;
    BufferedImage renderImage;
    AffineTransform transform;
    AffineTransformOp op;

    enum Collided {
        FALSE,
        GOINGFORWARD,
        GOINGBACKWARD
    }

    public Car(Point position, double angle, double speed, double acceleration, double turnSpeed, double maxSpeed,
            String carImagePath, int imageHeight) {
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

    public void calculateCarHitbox() {
        int carX = (int) position.x;
        int carY = (int) position.y;
        int carWidth = imageWidth;
        int carHeight = imageHeight;

        double radians = Math.toRadians(angle);
        double cos = Math.cos(radians);
        double sin = Math.sin(radians);

        // Define corners relative to the center point
        double[][] corners = new double[4][2];

        // Calculate corner positions relative to center before rotation
        corners[0] = new double[] { -carWidth / 2, -carHeight / 2 }; // Top-left
        corners[1] = new double[] { carWidth / 2, -carHeight / 2 }; // Top-right
        corners[2] = new double[] { carWidth / 2, carHeight / 2 }; // Bottom-right
        corners[3] = new double[] { -carWidth / 2, carHeight / 2 }; // Bottom-left

        // Rotate each corner around the origin and then translate to final position
        for (int i = 0; i < 4; i++) {
            double x = corners[i][0];
            double y = corners[i][1];

            // Rotate point
            double rotatedX = x * cos - y * sin;
            double rotatedY = x * sin + y * cos;

            // Translate to final position
            corners[i][0] = carX + rotatedX;
            corners[i][1] = carY + rotatedY;
        }

        // Create the car's bounding box (rectangle)
        carLeft = new Line2D.Double(corners[0][0], corners[0][1], corners[3][0], corners[3][1]);
        carRight = new Line2D.Double(corners[1][0], corners[1][1], corners[2][0], corners[2][1]);
        carTop = new Line2D.Double(corners[0][0], corners[0][1], corners[1][0], corners[1][1]);
        carBottom = new Line2D.Double(corners[2][0], corners[2][1], corners[3][0], corners[3][1]);
    }

    void updateRenderPosition() {
        int renderPosX = position.x - containerWidth / 2;
        int renderPosY = position.y - containerHeight / 2;

        renderPosition.setLocation(renderPosX, renderPosY);
    }

    public void update(RaceTrack raceTrack) {
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
            // Collision detected, stop the car
            if (speed > 0) {
                speed = 0; // Stop the car
            }

            System.out.println("Collision detected forward! Car stopped.");
        } else if (collided == Collided.GOINGBACKWARD) {
            // Collision detected, stop the car
            if (speed < 0) {
                speed = 0; // Stop the car
            }

            System.out.println("Collision detected backward! Car stopped.");
        }

        // Update the car's position based on its speed and angle
        position.x += speed * sin;
        position.y -= speed * cos;

        // Update the car's rotation for rendering
        newImageWidth = (int) (carImage.getWidth() * cosAbs + carImage.getHeight() * sinAbs);
        newImageHeight = (int) (carImage.getWidth() * sinAbs + carImage.getHeight() * cosAbs);

        containerWidth = (int) (newImageWidth * scale);
        containerHeight = (int) (newImageHeight * scale);

        BufferedImage rotated = new BufferedImage(newImageWidth, newImageHeight, carImage.getType());
        Graphics2D graphic = rotated.createGraphics();

        graphic.translate((newImageWidth - carImage.getWidth()) / 2, (newImageHeight - carImage.getHeight()) / 2);
        graphic.rotate(radians, carImage.getWidth() / 2, carImage.getHeight() / 2);
        graphic.drawRenderedImage(carImage, null);
        graphic.dispose();

        deltaRotation = 0;
        renderImage = rotated;
    }

    public void accelerate() {
        // Increase the car's speed
        speed += acceleration;

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
