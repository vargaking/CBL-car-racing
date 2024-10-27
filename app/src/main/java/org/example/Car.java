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
    Point2D position;
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
    Line2D[] carHitbox; // Array of lines representing the car's hitbox: top, bottom, left, right
    Line2D carLeft;
    Line2D carRight;
    Line2D carTop;
    Line2D carBottom;
    
    Collided collided = Collided.FALSE;
    BufferedImage carImage;
    BufferedImage renderImage;
    AffineTransform transform;
    AffineTransformOp op;

    private boolean hasCrossedFinishLine = false; // Flag for finish line crossing

    enum Collided {
        FALSE,
        TRUE,
        GOINGFORWARD,
        GOINGBACKWARD
    }

    enum Moves {
        ACCELERATE,
        TURN_LEFT,
        TURN_RIGHT,
        BRAKE,
        NOTHING
    }

    public Car(Point2D position, double angle, double speed, double acceleration, double turnSpeed, double maxSpeed,
            String carImagePath, int imageHeight) {       
        this.position = new Point2D.Double(position.getX(), position.getY());
        System.out.println("Car position: " + this.position.getX() + ", " + this.position.getY());
        this.angle = angle;
        this.deltaRotation = 0;
        this.speed = speed;
        this.acceleration = acceleration;
        this.turnSpeed = turnSpeed;
        this.maxSpeed = maxSpeed;
        this.carImagePath = carImagePath;
        this.transform = new AffineTransform();
        this.op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
        this.carHitbox = new Line2D[4];

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
        int carX = (int) position.getX();
        int carY = (int) position.getY();
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
        carHitbox[0] = new Line2D.Double(corners[0][0], corners[0][1], corners[1][0], corners[1][1]);
        carHitbox[1] = new Line2D.Double(corners[2][0], corners[2][1], corners[3][0], corners[3][1]);
        carHitbox[2] = new Line2D.Double(corners[0][0], corners[0][1], corners[3][0], corners[3][1]);
        carHitbox[3] = new Line2D.Double(corners[1][0], corners[1][1], corners[2][0], corners[2][1]);
    }

    void updateRenderPosition() {
        int renderPosX = (int) position.getX() - containerWidth / 2;
        int renderPosY = (int) position.getY() - containerHeight / 2;

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
            if (speed > 0) {
                speed = 0;
            }
        } else if (collided == Collided.GOINGBACKWARD) {
            if (speed < 0) {
                speed = 0;
            }
        }

        // Update the car's position based on its speed and angle
        position.setLocation(position.getX() + speed * sin, position.getY() - speed * cos);

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
        speed += acceleration;

        if (speed > maxSpeed) {
            speed = maxSpeed;
        }
    }

    public void turnLeft() {
        angle -= turnSpeed;
        deltaRotation = -turnSpeed;
    }

    public void turnRight() {
        angle += turnSpeed;
        deltaRotation = turnSpeed;
    }

    public void brake() {
        speed -= acceleration;

        if (speed < -maxSpeed) {
            speed = -maxSpeed;
        }
    }

    // Finish line crossing methods
    public void setHasCrossedFinishLine(boolean value) {
        this.hasCrossedFinishLine = value;
    }

    public boolean hasCrossedFinishLine() {
        return this.hasCrossedFinishLine;
    }
}
