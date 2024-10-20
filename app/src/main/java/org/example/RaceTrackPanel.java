package org.example;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.example.RaceTrack.Point;
import org.example.RaceTrack.Wall;
import java.awt.Color;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class RaceTrackPanel extends JPanel implements KeyListener {
    RaceTrack raceTrack;
    BufferedImage carImage;
    Car player1;
    Timer timer;
    KeyEvent pressedKey;

    RaceTrackPanel(RaceTrack track) {
        this.raceTrack = track;

        // Init car
        player1 = new Car(track.new Point(100, 500), 0, 0, 1, 5, 30, "cars/car1.png");

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
            g.drawImage(player1.op.filter(player1.carImage, null), player1.position.x, player1.position.y, 64, 64, null);
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

