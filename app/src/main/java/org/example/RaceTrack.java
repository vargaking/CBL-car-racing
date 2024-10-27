package org.example;

import java.util.ArrayList;

public class RaceTrack {
    static int trackWidth;
    static int trackHeight;

    public class Point {
        public int x;
        public int y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Point scaleToTrackSize() {
            return new Point((int) (x * trackWidth / 100), (int) (y * trackHeight / 100));
        }
    }

    public class Wall {
        public Point start;
        public Point end;

        Wall(Point start, Point end) {
            this.start = start.scaleToTrackSize();
            this.end = end.scaleToTrackSize();
        }
    }

    ArrayList<Wall> innerWalls;
    ArrayList<Wall> outerWalls;

    Wall finishLine;

    // Finish line coordinates
    Point finishLineStart;
    Point finishLineEnd;

    RaceTrack(int width, int height) {
        trackWidth = width;
        trackHeight = height;
        outerWalls = new ArrayList<>();
        innerWalls = new ArrayList<>();

        // Define outer and inner walls
        outerWalls.add(new Wall(new Point(10, 10), new Point(90, 10)));
        outerWalls.add(new Wall(new Point(90, 10), new Point(90, 90)));
        outerWalls.add(new Wall(new Point(90, 90), new Point(10, 90)));
        outerWalls.add(new Wall(new Point(10, 90), new Point(10, 10)));

        innerWalls.add(new Wall(new Point(30, 30), new Point(70, 30)));
        innerWalls.add(new Wall(new Point(70, 30), new Point(70, 70)));
        innerWalls.add(new Wall(new Point(70, 70), new Point(30, 70)));
        innerWalls.add(new Wall(new Point(30, 70), new Point(30, 30)));

        // Define finish line between outer and inner walls on the left
        finishLine = new Wall(new Point(10, 60), new Point(30, 60));

        // Print finish line points after scaling
        //System.out.println("Finish Line Start: " + finishLineStart.x + ", " + finishLineStart.y);
        //System.out.println("Finish Line End: " + finishLineEnd.x + ", " + finishLineEnd.y);
    }

    public boolean isCarCrossedFinishLine(Car car) {
        int carX = (int) car.position.x;
        int carY = (int) car.position.y;

        // Print car position for debug
        System.out.println("Car Position: " + carX + ", " + carY);

        // Check if the car has crossed the finish line
        return carY >= finishLine.start.y && carY <= finishLine.end.y && carX >= finishLine.start.x && carX <= finishLine.end.x;
    }
}



