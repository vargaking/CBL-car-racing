/*
 * This source file was generated by the Gradle 'init' task
 */
package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class App {
    static JFrame frame;
    static int screenWidth = 800;
    static int screenHeight = 600;

    // Define the getGreeting method
    public String getGreeting() {
        return "Welcome to Ultimate Car Racing!";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Create the main window (frame) with a title.
            frame = new JFrame("Ultimate Car Racing");

            // Create a JPanel for organizing components
            JPanel panel = new JPanel(); // Create a panel
            panel.setLayout(new BorderLayout()); // Set layout for the panel

            // Add a label for the game title.
            JLabel titleLabel = new JLabel("Ultimate Car Racing", SwingConstants.CENTER);
            titleLabel.setFont(new Font("Serif", Font.BOLD, 36));
            panel.add(titleLabel, BorderLayout.NORTH); // Add title label at the top

            // Add a label for controls instructions and greeting message
            JLabel controlsLabel = new JLabel("<html>" + new App().getGreeting() + "<br>Player 1: WASD | Player 2: Arrow Keys</html>", SwingConstants.CENTER);
            controlsLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            panel.add(controlsLabel, BorderLayout.CENTER); // Add instructions in the center

            // Create a JPanel for the buttons
            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new FlowLayout());

            // Create buttons for 1-Player and 2-Player modes
            JButton onePlayerButton = new JButton("1 Player");
            JButton twoPlayerButton = new JButton("2 Players");
            JButton exitButton = new JButton("Exit");

            // Add buttons to the panel
            buttonPanel.add(onePlayerButton);
            buttonPanel.add(twoPlayerButton);
            buttonPanel.add(exitButton);

            // Add the button panel to the main panel at the bottom
            panel.add(buttonPanel, BorderLayout.SOUTH);

            // Specify what happens when you close the window.
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Set the size of the window.
            frame.setSize(screenWidth, screenHeight);

            // Add the main panel to the frame
            frame.add(panel);

            // Make the window visible on the screen.
            frame.setVisible(true);

            // Add ActionListener to the buttons
            onePlayerButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    startGame(1);  // Start the game in 1-player mode
                    //frame.dispose(); // Close the opening screen
                }
            });

            twoPlayerButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    startGame(2);  // Start the game in 2-player mode
                    //frame.dispose(); // Close the opening screen
                }
            });

            exitButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.exit(0); // Exit the game
                }
            });

            
        });
    }

    // Method to start the game with the selected mode
    private static void startGame(int numberOfPlayers) {
        // This is where you will call the logic to initialize and run your 2D racing game
        System.out.println("Starting game with " + numberOfPlayers + " player(s)...");
        // Game logic should go here

        RaceTrack track = new RaceTrack(screenWidth, screenHeight);

        RaceTrackPanel gamePanel = new RaceTrackPanel(track, numberOfPlayers);

        gamePanel.repaint();

        frame.getContentPane().removeAll();
        frame.add(gamePanel);
        gamePanel.requestFocusInWindow();
        frame.revalidate();
        frame.repaint();
    }
}
