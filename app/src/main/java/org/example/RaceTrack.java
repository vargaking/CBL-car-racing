package org.example;

import java.awt.geom.Line2D;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class RaceTrack {
    static int trackWidth;
    static int trackHeight;
    int length;

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
    ArrayList<Wall> checkPoints; // last checkpoint is the finish line
    ArrayList<Point> checkPointsOnMidline;

    RaceTrack(int width, int height) {
        trackWidth = width;
        trackHeight = height;
        outerWalls = new ArrayList<>();
        innerWalls = new ArrayList<>();
        midLine = new ArrayList<>();
        checkPoints = new ArrayList<>();
        checkPointsOnMidline = new ArrayList<>();

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
        midLine.add(new Wall(new Point(20, 80), new Point(20, 20)));
        midLine.add(new Wall(new Point(20, 20), new Point(80, 20)));
        midLine.add(new Wall(new Point(80, 20), new Point(80, 80)));
        midLine.add(new Wall(new Point(80, 80), new Point(20, 80)));

        // Define finish line between outer and inner walls on the left
        checkPoints.add(new Wall(new Point(50, 10), new Point(50, 30)));
        checkPoints.add(new Wall(new Point(70, 50), new Point(90, 50)));
        checkPoints.add(new Wall(new Point(50, 70), new Point(50, 90)));
        checkPoints.add(new Wall(new Point(10, 50), new Point(30, 50)));

        checkPointsOnMidline.add(new Point(50, 20).scaleToTrackSize());
        checkPointsOnMidline.add(new Point(80, 50).scaleToTrackSize());
        checkPointsOnMidline.add(new Point(50, 80).scaleToTrackSize());
        checkPointsOnMidline.add(new Point(20, 50).scaleToTrackSize());

        length = 0;

        for (Wall wall : midLine) {
            length += (int) Math.abs(wall.start.x - wall.end.x) + (int) Math.abs(wall.start.y - wall.end.y);
        }

        System.out.println("Track length: " + length);
    }

    public boolean isCarCrossedFinishLine(Car car) {
        car.calculateCarHitbox();
        Wall currentCheckPoint = checkPoints.get(car.laps % 4);
        for (Line2D carLine : car.carHitbox) {
            if (carLine.intersectsLine(currentCheckPoint.start.x, currentCheckPoint.start.y, currentCheckPoint.end.x, currentCheckPoint.end.y)) {
                return true;
            }
        }

        return false;
    }

    public int distanceToFinishLine(Car car, boolean log) {
        int carX = (int) car.position.getX();
        int carY = (int) car.position.getY();

        // Calculate the distance to the racing line
        int distanceToRacingLine = Integer.MAX_VALUE;
        int distanceToNextCheckpoint = 0;
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
            int nextCheckPointIndex = car.laps % 4;
            int lastCheckPointIndex = (nextCheckPointIndex + 3) % 4;

            Point nextCheckPointOnMidline = checkPointsOnMidline.get(nextCheckPointIndex);
            Point lastCheckPointOnMidline = checkPointsOnMidline.get(lastCheckPointIndex);
            if (closestWallIndex == nextCheckPointIndex) {
                // we calculate quorter of checkpoint length - distance from the last checkpoint
                if (closestWallIndex == 0 || closestWallIndex == 1) {
                    distanceToNextCheckpoint = (int) length / 4 - (int) (closestPointToMidline.x - lastCheckPointOnMidline.x) - (int) (lastCheckPointOnMidline.y - closestPointToMidline.y);
                } else {
                    distanceToNextCheckpoint = (int) length / 4 - (int) (lastCheckPointOnMidline.x - closestPointToMidline.x) - (int) (closestPointToMidline.y - lastCheckPointOnMidline.y);
                }
            } else {
                // we calculate the distance to the next checkpoint
                distanceToNextCheckpoint = (int) Math.abs(nextCheckPointOnMidline.x - closestPointToMidline.x) + (int) Math.abs(nextCheckPointOnMidline.y - closestPointToMidline.y);
            }

            if (log) {
                System.out.println("log: " + nextCheckPointIndex + " " + closestWallIndex + " " + closestPointToMidline.x + " " + closestPointToMidline.y + " " + distanceToNextCheckpoint);
                System.out.println("log: " + nextCheckPointOnMidline.x + " " + nextCheckPointOnMidline.y + " " + lastCheckPointOnMidline.x + " " + lastCheckPointOnMidline.y);
            }            
        }

        return distanceToNextCheckpoint + distanceToRacingLine;
    }

    public Point pointOnMidline(Car car) {
        int carX = (int) car.position.getX();
        int carY = (int) car.position.getY();

        // Calculate the distance to the racing line
        int distanceToRacingLine = Integer.MAX_VALUE;
        int distanceToNextCheckpoint = 0;
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

        return closestPointToMidline;
    }
}
