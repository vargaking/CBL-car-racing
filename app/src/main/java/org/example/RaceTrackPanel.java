package org.example;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.example.Car.Collided;
import org.example.RaceTrack.Wall;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class RaceTrackPanel extends JPanel implements KeyListener {
    RaceTrack raceTrack;
    BufferedImage carImage;
    Car player1;
    Car player2;
    BotCar botPlayer;
    int numberOfPlayers;
    Timer animationTimer;
    private TimerManager timerManager;
    private TimerManager timerManager2;
    private static final int TOTAL_LAPS = 3; // Limit to 5 laps
    Bot bot;

    ArrayList<Integer> keysPressed = new ArrayList<Integer>();
    private ArrayList<RaceResult> raceResults; // To store race results

    RaceTrackPanel(RaceTrack track, int numberOfPlayers) {
        this.raceTrack = track;
        this.numberOfPlayers = numberOfPlayers;
        this.timerManager = new TimerManager();
        this.timerManager2 = new TimerManager();
        this.raceResults = new ArrayList<>(); // Initialize the results list

        // Init cars
        player1 = new Car(new Point2D.Double(350, 300), 0, 0, .5, 5, 20, "cars/car2.png", 64, 0);

        if (numberOfPlayers == 1) {
            botPlayer = new BotCar(new Point2D.Double(250, 300), 0, 0, .5, 30, 7, "cars/car_blue.png", 64, 0);
            bot = new Bot(botPlayer, raceTrack, 7);
        } else {
            player2 = new Car(new Point2D.Double(250, 300), 0, 0, .5, 5, 20, "cars/car_blue.png", 64, 0);
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
                } else {
                    player2.update(raceTrack);
                }

                // Check for lap completion and finish line crossing
                if (raceTrack.isCarCrossedFinishLine(player1)) {
                    player1.laps++;

                    if (player1.laps % 4 == 0) {
                        timerManager.lapCompleted();
                    }

                    if (timerManager.isRaceComplete(TOTAL_LAPS)) {
                        animationTimer.stop(); // Stop the race when all laps are complete
                        recordResults(); // Record results when race is complete
                        showLeaderboard(); // Show leaderboard
                    }
                }

                if (numberOfPlayers == 1) {
                    if (raceTrack.isCarCrossedFinishLine(botPlayer)) {
                        botPlayer.laps++;

                        if (botPlayer.laps % 4 == 0) {
                            timerManager2.lapCompleted();
                        }

                        if (timerManager2.isRaceComplete(TOTAL_LAPS)) {
                            animationTimer.stop(); // Stop the race when all laps are complete
                            recordResults(); // Record results when race is complete
                            showLeaderboard(); // Show leaderboard
                        }
                    }
                } else {
                    if (raceTrack.isCarCrossedFinishLine(player2)) {
                        player2.laps++;

                        if (player2.laps % 4 == 0) {
                            timerManager2.lapCompleted();
                        }

                        if (timerManager2.isRaceComplete(TOTAL_LAPS)) {
                            animationTimer.stop(); // Stop the race when all laps are complete
                            recordResults(); // Record results when race is complete
                            showLeaderboard(); // Show leaderboard
                        }
                    }
                }
                repaint(); // Repaint panel for visual updates
            }
        });

        animationTimer.start(); // Start the timer

        // Thread for bot algorithm
        if (numberOfPlayers == 1) {
            new Thread(() -> {
                while (!Thread.currentThread().isInterrupted()) {
                    if (numberOfPlayers == 1) {
                        Car.Moves nextMove = bot.searchBestMove();
                        // Execute the bot's move
                        switch (nextMove) {
                            case ACCELERATE:
                                botPlayer.accelerate();
                                break;
                            case BRAKE:
                                botPlayer.brake();
                                break;
                            case TURN_LEFT:
                                botPlayer.turnLeft();
                                break;
                            case TURN_RIGHT:
                                botPlayer.turnRight();
                                break;
                            case NOTHING:
                                break;
                        }
                    }
                    try {
                        Thread.sleep(100 * 5); // Adjust based on bot's performance requirements
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }).start();
        }

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
                }

                if (numberOfPlayers == 2) {
                    switch (key) {
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
                        (int) botPlayer.renderPosition.getY(), botPlayer.containerWidth, botPlayer.containerHeight,
                        null);
            }
        } else {
            if (player2.renderImage != null) {
                g2d.drawImage(player2.renderImage, (int) player2.renderPosition.getX(),
                        (int) player2.renderPosition.getY(), player2.containerWidth, player2.containerHeight, null);

                System.out.println("Player 2 position: " + player2.position.getX() + ", " + player2.position.getY());
            }
        }

        // Draw finish line
        g2d.setColor(Color.RED);
        Wall finishLine = raceTrack.checkPoints.get(3);
        g2d.drawLine(finishLine.start.x, finishLine.start.y, finishLine.end.x, finishLine.end.y);

        // Draw lap counter and timer information
        g2d.setColor(Color.BLACK);
        g2d.drawString("Player 1 Lap: " + (int) player1.laps / 4 + "/" + TOTAL_LAPS, 10, 20);
        g2d.drawString("Player 1 Lap Time: " + formatTime(timerManager.getCurrentLapTime()), 10, 40);

        if (numberOfPlayers == 1) {
            g2d.drawString("Bot lap: " + botPlayer.laps / 4 + "/" + TOTAL_LAPS, 10, 80);
        } else {
            g2d.drawString("Player 2 lap: " + (int) player2.laps / 4 + "/" + TOTAL_LAPS, 10, 80);
            g2d.drawString("Player 2 Lap Time: " + formatTime(timerManager2.getCurrentLapTime()), 10, 100);
        }

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

    private void recordResults() {
        // Record results for player and bot (if applicable)
        if (timerManager.getLapsCompleted() > timerManager2.getLapsCompleted()) {
            raceResults.add(new RaceResult("Player 1", timerManager.getLapsCompleted(), timerManager.fastestLap));

            if (numberOfPlayers == 1) {
                raceResults.add(new RaceResult("Bot", timerManager2.getLapsCompleted(), timerManager2.fastestLap));
            } else {
                raceResults.add(new RaceResult("Player 2", timerManager2.getLapsCompleted(), timerManager2.fastestLap));
            }
        } else {
            if (numberOfPlayers == 1) {
                raceResults.add(new RaceResult("Bot", timerManager2.getLapsCompleted(), timerManager2.fastestLap));
            } else {
                raceResults.add(new RaceResult("Player 2", timerManager2.getLapsCompleted(), timerManager2.fastestLap));
            }

            raceResults.add(new RaceResult("Player 1", timerManager.getLapsCompleted(), timerManager.fastestLap));
        }

    }

    private void showLeaderboard() {
        // Clear the frame and show the leaderboard
        App.frame.getContentPane().removeAll();
        LeaderboardPanel leaderboardPanel = new LeaderboardPanel(raceResults);
        App.frame.add(leaderboardPanel);
        App.frame.revalidate();
        App.frame.repaint();
    }
}