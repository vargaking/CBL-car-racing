package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class LeaderboardPanel extends JPanel {
    private ArrayList<RaceResult> results;
    private JButton backButton;

    public LeaderboardPanel(ArrayList<RaceResult> results) {
        this.results = results;
        setLayout(new BorderLayout());

        // Title
        JLabel titleLabel = new JLabel("Leaderboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 36));
        add(titleLabel, BorderLayout.NORTH);

        // Create the leaderboard table
        String[] columnNames = {"Position", "Car", "Laps Completed", "Fastest Lap"};
        String[][] data = new String[results.size()][4];

        for (int i = 0; i < results.size(); i++) {
            RaceResult result = results.get(i);
            data[i][0] = String.valueOf(i + 1); // Position
            data[i][1] = result.getCarName(); // Car Name
            data[i][2] = String.valueOf((int) result.getLapsCompleted()); // Total Time
            data[i][3] = formatTime(result.getFastestLap()); // Fastest Lap

            if (result.getLapsCompleted() == 0) {
                data[i][3] = "No laps completed";
            }
        }

        JTable table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Back button
        backButton = new JButton("Back to Menu");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                backToMenu();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void backToMenu() {
        // Logic to return to the main menu
        App.frame.getContentPane().removeAll();
        App.main(new String[0]); // Restart the application to show the main menu
    }

    private String formatTime(double timeInSeconds) {
        int minutes = (int) (timeInSeconds / 60);
        int seconds = (int) (timeInSeconds % 60);
        int milliseconds = (int) ((timeInSeconds * 100) % 100);
        return String.format("%02d:%02d.%02d", minutes, seconds, milliseconds);
    }
}