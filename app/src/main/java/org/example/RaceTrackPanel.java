package org.example;

import javax.annotation.Syntax;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.example.Car.Collided;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import java.util.ArrayList;

public class RaceTrackPanel extends JPanel implements KeyListener {
    RaceTrack raceTrack;
    BufferedImage carImage;
    Car player1;
    Car player2;
    BotCar botPlayer;
    int numberOfPlayers;
    Timer animationTimer;
    Timer botTimer;
    private TimerManager timerManager;
    private static final int TOTAL_LAPS = 5; // Limit to 5 laps
    Bot bot;

    ArrayList<Integer> keysPressed = new ArrayList<Integer>();

    RaceTrackPanel(RaceTrack track, int numberOfPlayers) {
        this.raceTrack = track;
        this.numberOfPlayers = numberOfPlayers;
        this.timerManager = new TimerManager();

        // Init cars
        player1 = new Car(new Point2D.Double(350, 400), 0, 0, .5, 5, 20, "cars/car2.png", 64);

        //player2 = new Car(new Point2D.Double(450, 400), 0, 0, .5, 5, 20, "cars/car_blue.png", 64);

        if (numberOfPlayers == 1) {
            botPlayer = new BotCar(new Point2D.Double(450, 400), 0, 0, .5, 20, 20, "cars/car_blue.png", 64);
            bot = new Bot(botPlayer, raceTrack, 8);
        }

        setFocusable(true);
        addKeyListener(this);
        requestFocusInWindow();

        // Animation loop
        animationTimer = new Timer(1000 / 10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleKeyPress();
                player1.update(raceTrack); // Update car position

                if (numberOfPlayers == 1) {
                    botPlayer.update(raceTrack);
                }

                // Check for lap completion and finish line crossing
                if (raceTrack.isCarCrossedFinishLine(player1)) {
                    timerManager.lapCompleted();
                    if (timerManager.isRaceComplete(TOTAL_LAPS)) {
                        animationTimer.stop(); // Stop the race when all laps are complete
                    }
                }

                if (numberOfPlayers == 1) {
                    if (raceTrack.isCarCrossedFinishLine(botPlayer)) {
                        botPlayer.laps++;
                    }
                } else {
                    if (raceTrack.isCarCrossedFinishLine(player2)) {
                        player2.laps++;
                    }
                }
                repaint(); // Repaint panel for visual updates

                //player1.update(raceTrack); // Pass the raceTrack to the update method
                //player2.update(raceTrack); // Pass the raceTrack to the update method

                /*if (numberOfPlayers == 1) {
                    if (Collision.checkCollisionBetweenCars(player1, botPlayer) == Collided.TRUE) {
                        player1.speed = 0;
                        botPlayer.speed = 0;
                    }
                } else {
                    if (Collision.checkCollisionBetweenCars(player2, player1) == Collided.TRUE) {
                        player1.speed = 0;
                        player2.speed = 0;
                    }
                }*/
            }
        });

        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                if (numberOfPlayers == 1) {
                    Car.Moves nextMove = bot.searchBestMove();
                    // Execute the bot's move
                    switch (nextMove) {
                        case ACCELERATE: botPlayer.accelerate(); break;
                        case BRAKE: botPlayer.brake(); break;
                        case TURN_LEFT: botPlayer.turnLeft(); break;
                        case TURN_RIGHT: botPlayer.turnRight(); break;
                        case NOTHING: break;
                    }
                }
                try {
                    Thread.sleep(100 * 5); // Adjust based on bot's performance requirements
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }).start();
        

        animationTimer.start(); // Start the timer
    }

    private void handleKeyPress() {
        if (keysPressed.size() > 0) {
            for (int key : keysPressed) {
                switch (key) {
                    case KeyEvent.VK_W:
                        player1.accelerate();
                        break;
                    case KeyEvent.VK_S:
                        player1.brake();
                        break;
                    case KeyEvent.VK_A:
                        player1.turnLeft();
                        break;
                    case KeyEvent.VK_D:
                        player1.turnRight();
                        break;
                    case KeyEvent.VK_UP:
                        player2.accelerate();
                        break;
                    case KeyEvent.VK_DOWN:
                        player2.brake();
                        break;
                    case KeyEvent.VK_LEFT:
                        player2.turnLeft();
                        break;
                    case KeyEvent.VK_RIGHT:
                        player2.turnRight();
                        break;
                }
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

        
        if (numberOfPlayers == 1) {
            if (botPlayer.renderImage != null) {
                g2d.drawImage(botPlayer.renderImage, (int) botPlayer.renderPosition.getX(),
                        (int) botPlayer.renderPosition.getY(), botPlayer.containerWidth, botPlayer.containerHeight, null);
            }
        } else {
            if (player2.renderImage != null) {
                g2d.drawImage(player2.renderImage, (int) player2.renderPosition.getX(),
                        (int) player2.renderPosition.getY(), player2.containerWidth, player2.containerHeight, null);
            }
        }
        
        // Draw finish line and debug its location
        g2d.setColor(Color.RED);
        g2d.drawLine(raceTrack.finishLine.start.x, raceTrack.finishLine.start.y,
                raceTrack.finishLine.end.x, raceTrack.finishLine.end.y);

        // Draw lap counter and timer information
        g2d.setColor(Color.BLACK);
        g2d.drawString("Lap: " + timerManager.getLapsCompleted() + "/" + TOTAL_LAPS, 10, 20);
        g2d.drawString("Bot lap: " + botPlayer.laps, 10, 80);
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

        g2d.setColor(Color.CYAN);
        for (RaceTrack.Wall wall : raceTrack.midLine) {
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
        // Add the key event to the list of keys pressed if it is not already in the
        // list
        if (!keysPressed.contains(e.getKeyCode())) {
            keysPressed.add(e.getKeyCode());
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Remove the key event from the list of keys pressed
        keysPressed.remove((Integer) e.getKeyCode());
    }
}
