/*
 * This source file was generated by the Gradle 'init' task
 */
package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class App {

    // Define the getGreeting method
    public String getGreeting() {
        return "Welcome to Ultimate Car Racing!";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Step 1: Create the main window (frame) with a title.
            JFrame frame = new JFrame("Ultimate Car Racing");

            // Step 2: Create a JPanel for organizing components
            JPanel panel = new JPanel(); // Create a panel
            panel.setLayout(new BorderLayout()); // Set layout for the panel

            // Step 3: Add a label for the game title.
            JLabel titleLabel = new JLabel("Ultimate Car Racing", SwingConstants.CENTER);
            titleLabel.setFont(new Font("Serif", Font.BOLD, 36));
            panel.add(titleLabel, BorderLayout.NORTH); // Add title label at the top

            // Step 4: Add a label for controls instructions and greeting message
            JLabel controlsLabel = new JLabel("<html>" + new App().getGreeting() + "<br>Player 1: WASD | Player 2: Arrow Keys</html>", SwingConstants.CENTER);
            controlsLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            panel.add(controlsLabel, BorderLayout.CENTER); // Add instructions in the center

            // Step 5: Create a JPanel for the buttons
            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new FlowLayout());

            // Step 6: Create buttons for 1-Player and 2-Player modes
            JButton onePlayerButton = new JButton("1 Player");
            JButton twoPlayerButton = new JButton("2 Players");
            JButton exitButton = new JButton("Exit");

            // Add buttons to the panel
            buttonPanel.add(onePlayerButton);
            buttonPanel.add(twoPlayerButton);
            buttonPanel.add(exitButton);

            // Step 7: Add the button panel to the main panel at the bottom
            panel.add(buttonPanel, BorderLayout.SOUTH);

            // Step 8: Specify what happens when you close the window.
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Step 9: Set the size of the window.
            frame.setSize(600, 400);

            // Step 10: Add the main panel to the frame
            frame.add(panel);

            // Step 11: Make the window visible on the screen.
            frame.setVisible(true);

            // Step 12: Add ActionListener to the buttons
            onePlayerButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    startGame(1);  // Start the game in 1-player mode
                    frame.dispose(); // Close the opening screen
                }
            });

            twoPlayerButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    startGame(2);  // Start the game in 2-player mode
                    frame.dispose(); // Close the opening screen
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
    }
}
