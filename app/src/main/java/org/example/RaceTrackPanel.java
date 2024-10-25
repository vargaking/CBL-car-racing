package org.example;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.example.RaceTrack.Point;
import org.example.RaceTrack.Wall;

import com.google.common.graph.Graph;

import java.awt.Color;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.awt.Graphics2D;

public class RaceTrackPanel extends JPanel implements KeyListener {
    RaceTrack raceTrack;
    BufferedImage carImage;
    Car player1;
    Timer timer;
    KeyEvent pressedKey;

    RaceTrackPanel(RaceTrack track) {
        this.raceTrack = track;

        // Init car
        player1 = new Car(track.new Point(200, 400), 0, 0, 1, 5, 30, "cars/car2.png", 64);

        setFocusable(true);
        addKeyListener(this);
        requestFocusInWindow();

        // animation loop
        timer = new Timer(1000 / 10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (pressedKey != null) {
                    switch (pressedKey.getKeyCode()) {
                        case KeyEvent.VK_W:
                            player1.accelerate();
                            break;
                        case KeyEvent.VK_A:
                            player1.turnLeft();
                            break;
                        case KeyEvent.VK_S:
                            player1.brake();
                            break;
                        case KeyEvent.VK_D:
                            player1.turnRight();
                            break;
                    }
                }

                player1.update(raceTrack); // Pass the raceTrack to the update method
                repaint();
            }
        });

        timer.start();
    }

    @Override
    public void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);
        // draw green background
        g.setColor(new Color(0x19b005));
        g.fillRect(0, 0, getWidth(), getHeight());

        // create polygons for inner and outer walls
        Polygon innerPolygon = new Polygon();
        Polygon outerPolygon = new Polygon();

        // create polygons for inner and outer wall backgrounds
        g.setColor(Color.BLACK);
        for (Wall wall : raceTrack.innerWalls) {
            innerPolygon.addPoint(wall.start.x, wall.start.y);
        }

        for (Wall wall : raceTrack.outerWalls) {
            outerPolygon.addPoint(wall.start.x, wall.start.y);
        }

        // fill the area between inner and outer walls
        g.setColor(Color.GRAY);
        g.fillPolygon(outerPolygon);
        g.setColor(new Color(0x19b005));
        g.fillPolygon(innerPolygon);

        // draw walls
        g.setColor(Color.BLACK);
        for (Wall wall : raceTrack.innerWalls) {
            g.drawLine(wall.start.x, wall.start.y, wall.end.x, wall.end.y);
        }

        for (Wall wall : raceTrack.outerWalls) {
            g.drawLine(wall.start.x, wall.start.y, wall.end.x, wall.end.y);
        }

        // draw car image
        if (player1.carImage != null) {
            int carX = (int) player1.renderPosition.getX();
            int carY = (int) player1.renderPosition.getY();
            g.drawImage(player1.renderImage, carX, carY, player1.containerWidth, player1.containerHeight, null);

            // Draw the car's container
            g.setColor(Color.RED);
            g.drawRect(carX, carY, player1.containerWidth, player1.containerHeight);
        
            // Draw the car's hitbox
            g.setColor(Color.MAGENTA);
            g.drawLine((int) player1.carLeft.getX1(), (int) player1.carLeft.getY1(), (int) player1.carLeft.getX2(), (int) player1.carLeft.getY2());
            g.drawLine((int) player1.carRight.getX1(), (int) player1.carRight.getY1(), (int) player1.carRight.getX2(), (int) player1.carRight.getY2());
            g.drawLine((int) player1.carTop.getX1(), (int) player1.carTop.getY1(), (int) player1.carTop.getX2(), (int) player1.carTop.getY2());
            g.drawLine((int) player1.carBottom.getX1(), (int) player1.carBottom.getY1(), (int) player1.carBottom.getX2(), (int) player1.carBottom.getY2());
        }

        // Draw finish line
        g.setColor(Color.RED);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        System.out.println("Key typed");
    }

    @Override
    public void keyPressed(KeyEvent e) {
        System.out.println(e.getKeyCode());
        pressedKey = e;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        pressedKey = null;
    }
}
