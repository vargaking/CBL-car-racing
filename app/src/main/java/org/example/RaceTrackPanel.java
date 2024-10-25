package org.example;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.IOException;

public class RaceTrackPanel extends JPanel implements KeyListener {
    private RaceTrack raceTrack;
    private Car player1;
    private Timer timer;
    private KeyEvent pressedKey;
    private TimerManager timerManager;
    private static final int TOTAL_LAPS = 5; // Limit to 5 laps

    public RaceTrackPanel(RaceTrack track) {
        this.raceTrack = track;
        this.timerManager = new TimerManager();

        // Initialize the car with specific starting position and image
        player1 = new Car(track.new Point(200, 400), 0, 0, 1, 5, 30, "cars/car2.png", 64);

        setFocusable(true);
        addKeyListener(this);
        requestFocusInWindow();

        // Animation loop
        timer = new Timer(1000 / 10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleKeyPress();
                player1.update(raceTrack); // Update car position

                // Check for lap completion and finish line crossing
                if (raceTrack.isCarCrossedFinishLine(player1)) {
                    timerManager.lapCompleted();
                    if (timerManager.isRaceComplete(TOTAL_LAPS)) {
                        timer.stop(); // Stop the race when all laps are complete
                    }
                }
                repaint(); // Repaint panel for visual updates
            }
        });

        timer.start(); // Start the timer
    }

    private void handleKeyPress() {
        if (pressedKey != null) {
            switch (pressedKey.getKeyCode()) {
                case KeyEvent.VK_W -> player1.accelerate();
                case KeyEvent.VK_A -> player1.turnLeft();
                case KeyEvent.VK_S -> player1.brake();
                case KeyEvent.VK_D -> player1.turnRight();
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Draw the race track background
        g2d.setColor(new Color(0x19b005));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        // Draw the walls
        drawWalls(g2d);

        // Draw the car
        if (player1.renderImage != null) {
            g2d.drawImage(player1.renderImage, (int) player1.renderPosition.getX(),
                    (int) player1.renderPosition.getY(), player1.containerWidth, player1.containerHeight, null);
        }

        // Draw finish line and debug its location
        g2d.setColor(Color.RED);
        System.out.println("Drawing finish line at: (" + raceTrack.finishLineStart.x + ", " + raceTrack.finishLineStart.y +
                           ") to (" + raceTrack.finishLineEnd.x + ", " + raceTrack.finishLineEnd.y + ")");
        g2d.drawLine(raceTrack.finishLineStart.x, raceTrack.finishLineStart.y, 
                     raceTrack.finishLineEnd.x, raceTrack.finishLineEnd.y);

        // Draw lap counter and timer information
        g2d.setColor(Color.BLACK);
        g2d.drawString("Lap: " + timerManager.getLapsCompleted() + "/" + TOTAL_LAPS, 10, 20);
        g2d.drawString("Lap Time: " + formatTime(timerManager.getCurrentLapTime()), 10, 40);
        g2d.drawString("Total Time: " + formatTime(timerManager.getTotalRaceTime()), 10, 60);
    }

    private void drawWalls(Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        Polygon innerPolygon = new Polygon();
        Polygon outerPolygon = new Polygon();

        for (RaceTrack.Wall wall : raceTrack.innerWalls) {
            innerPolygon.addPoint(wall.start.x, wall.start.y);
        }

        for (RaceTrack.Wall wall : raceTrack.outerWalls) {
            outerPolygon.addPoint(wall.start.x, wall.start.y);
        }

        // Fill area between inner and outer walls for visual effect
        g2d.setColor(Color.GRAY);
        g2d.fillPolygon(outerPolygon);
        g2d.setColor(new Color(0x19b005));
        g2d.fillPolygon(innerPolygon);

        g2d.setColor(Color.BLACK);
        for (RaceTrack.Wall wall : raceTrack.innerWalls) {
            g2d.drawLine(wall.start.x, wall.start.y, wall.end.x, wall.end.y);
        }

        for (RaceTrack.Wall wall : raceTrack.outerWalls) {
            g2d.drawLine(wall.start.x, wall.start.y, wall.end.x, wall.end.y);
        }
    }

    private String formatTime(double timeInSeconds) {
        int minutes = (int) (timeInSeconds / 60);
        int seconds = (int) (timeInSeconds % 60);
        int milliseconds = (int) ((timeInSeconds * 100) % 100);
        return String.format("%02d:%02d.%02d", minutes, seconds, milliseconds);
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        pressedKey = e;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        pressedKey = null;
    }
}
