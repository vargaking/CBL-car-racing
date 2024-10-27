package org.example;

import java.lang.reflect.Array;
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
    ArrayList<Wall> midLine; // can be used to calculate the distance to the finish line

    Wall finishLine;

    RaceTrack(int width, int height) {
        trackWidth = width;
        trackHeight = height;
        outerWalls = new ArrayList<>();
        innerWalls = new ArrayList<>();
        midLine = new ArrayList<>();

        // Define outer and inner walls
        outerWalls.add(new Wall(new Point(10, 10), new Point(90, 10)));
        outerWalls.add(new Wall(new Point(90, 10), new Point(90, 90)));
        outerWalls.add(new Wall(new Point(90, 90), new Point(10, 90)));
        outerWalls.add(new Wall(new Point(10, 90), new Point(10, 10)));

        innerWalls.add(new Wall(new Point(30, 30), new Point(70, 30)));
        innerWalls.add(new Wall(new Point(70, 30), new Point(70, 70)));
        innerWalls.add(new Wall(new Point(70, 70), new Point(30, 70)));
        innerWalls.add(new Wall(new Point(30, 70), new Point(30, 30)));

        // we break the line cutting the finish line into two parts to make it easier 
        // to calculate the distance to the finish line
        midLine.add(new Wall(new Point(20, 60), new Point(20, 20)));
        midLine.add(new Wall(new Point(20, 20), new Point(80, 20)));
        midLine.add(new Wall(new Point(80, 20), new Point(80, 80)));
        midLine.add(new Wall(new Point(80, 80), new Point(20, 80)));
        midLine.add(new Wall(new Point(20, 80), new Point(20, 60)));

        // Define finish line between outer and inner walls on the left
        finishLine = new Wall(new Point(10, 60), new Point(30, 60));
    }

    public boolean isCarCrossedFinishLine(Car car) {
        int carX = (int) car.position.getX();
        int carY = (int) car.position.getY();

        // Check if the car has crossed the finish line
        return carY >= finishLine.start.y && carY <= finishLine.end.y && carX >= finishLine.start.x
                && carX <= finishLine.end.x;
    }

    public int distanceToFinishLine(Car car) {
        int carX = (int) car.position.getX();
        int carY = (int) car.position.getY();

        // Calculate the distance to the racing line
        int distanceToRacingLine = Integer.MAX_VALUE;
        int distanceToFinishLine = 0;
        Point closestPointToMidline = null;
        int closestWallIndex = -1;

        for (int i = 0; i < midLine.size(); i++) {
            Wall wall = midLine.get(i);
            int distance = (int) Math.abs((wall.end.x - wall.start.x) * (wall.start.y - carY)
                    - (wall.start.x - carX) * (wall.end.y - wall.start.y)) /
                    (int) Math.sqrt(Math.pow(wall.end.x - wall.start.x, 2) + Math.pow(wall.end.y - wall.start.y, 2));

            if (distance < distanceToRacingLine) {
                distanceToRacingLine = distance;

                // Calculate the closest point on the wall segment
                double t = ((carX - wall.start.x) * (wall.end.x - wall.start.x)
                        + (carY - wall.start.y) * (wall.end.y - wall.start.y)) /
                        Math.pow(Math.sqrt(
                                Math.pow(wall.end.x - wall.start.x, 2) + Math.pow(wall.end.y - wall.start.y, 2)), 2);

                t = Math.max(0, Math.min(1, t)); // Clamp t to the range [0, 1]

                int closestX = (int) (wall.start.x + t * (wall.end.x - wall.start.x));
                int closestY = (int) (wall.start.y + t * (wall.end.y - wall.start.y));

                closestPointToMidline = new Point(closestX, closestY);
                closestWallIndex = i;
            }
        }

        //System.out.println("Player 1 distance to racing line: " + distanceToRacingLine);

        // calculate the distance to the finish line based on the distance to the racing line

        if (closestWallIndex != -1) {
            for (int i = closestWallIndex + 1; i < midLine.size(); i++) {
                Wall wall = midLine.get(i);
                int length = (int) Math.abs(wall.start.x - wall.end.x) + (int) Math.abs(wall.start.y - wall.end.y);

                distanceToFinishLine += length;
            }

            Wall closestWall = midLine.get(closestWallIndex);

            distanceToFinishLine += (int) Math.abs(closestWall.end.x - carX) + (int) Math.abs(closestWall.end.y - carY);
        }

        //System.out.println("Player 1 distance to finish line: " + distanceToFinishLine);

        return distanceToFinishLine;
        // calculate the distance to the finish line based on the distance to the racing
        // line
    }
}
