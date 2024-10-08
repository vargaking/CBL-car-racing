/*
 * This source file was generated by the Gradle 'init' task
 */
package org.example;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;


public class App {
    public String getGreeting() {
        return "Hello World!";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Ultimate Car Racing");
            frame.add(new JLabel(new App().getGreeting()));
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            frame.pack();
            frame.setVisible(true);
        });
    }
}
