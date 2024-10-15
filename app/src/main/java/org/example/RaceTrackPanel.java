package org.example;

import javax.swing.JPanel;
import org.example.RaceTrack.Wall;
import java.awt.Color;
import java.awt.Polygon;


public class RaceTrackPanel extends JPanel {
    RaceTrack raceTrack;

    RaceTrackPanel(RaceTrack track) {
        this.raceTrack = track;
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

        // Draw finish line
        g.setColor(Color.RED);
    }
}
