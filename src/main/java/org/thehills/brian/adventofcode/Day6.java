package org.thehills.brian.adventofcode;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day6 {

    private static final Pattern linePattern = Pattern.compile("(\\d+), (\\d+)");

    public static void main(String args[]) throws IOException {
        InputStream is = null;
        try {
            is = new FileInputStream(args[0]);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            List<Point> points = new ArrayList<>();
            int maxX = 0;
            int maxY = 0;
            // Create a list of the points
            while ((line = reader.readLine()) != null) {
                Matcher m = linePattern.matcher(line);
                if (m.matches()) {
                    Point p = new Point(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)));
                    points.add(p);
                    if (p.x > maxX) maxX = p.x;
                    if (p.y > maxY) maxY = p.y;
                }
            }

            System.out.println("Created " + points.size() + " points");

            HashBasedTable<Integer, Integer, Point> closestPoints = HashBasedTable.create();

            // For each point on the graph, find the closest point in the list.  If there's a tie, don't choose one.
            for (int x = 0; x <= maxX; ++x) {
                for (int y = 0; y <= maxY; ++y) {
                    Point testPoint = new Point(x, y);
                    int minDistance = Integer.MAX_VALUE;
                    Set<Point> testClosestPoints = new HashSet<>();
                    for (Point p : points) {
                        int distance = findManhattanDistance(testPoint, p);
                        if (distance < minDistance) {
                            minDistance = distance;
                            testClosestPoints.clear();
                            testClosestPoints.add(p);
                        } else if (distance == minDistance) {
                            testClosestPoints.add(p);
                        }
                    }
                    if (testClosestPoints.size() == 1) {
                        // System.out.println("Closest point to " + x + "," + y + " is " + testClosestPoints.iterator().next());
                        closestPoints.put(x, y, testClosestPoints.iterator().next());
                    }
                }
            }

            // Saving the collection of points for part 2, since we mutated it for part 1.  Yuck.
            ArrayList<Point> points2 = new ArrayList<>();
            points2.addAll(points);

            // For each point in the list, count the size of its area.  Discard any regions that touch the
            // border of the defined plane because they are infinite.
            Set<Map.Entry<Integer, Point>>  firstRowSet = closestPoints.row(0).entrySet();
            for (Map.Entry<Integer, Point> e : firstRowSet) {
                System.out.println("Removing " + e.getValue());
                points.remove(e.getValue());
            }
            Set<Map.Entry<Integer, Point>>  lastRowSet = closestPoints.row(maxX).entrySet();
            for (Map.Entry<Integer, Point> e : lastRowSet) {
                System.out.println("Removing " + e.getValue());
                points.remove(e.getValue());
            }
            Set<Map.Entry<Integer, Point>>  firstColumnSet = closestPoints.column(0).entrySet();
            for (Map.Entry<Integer, Point> e : firstColumnSet) {
                System.out.println("Removing " + e.getValue());
                points.remove(e.getValue());
            }
            Set<Map.Entry<Integer, Point>>  lastColumnSet = closestPoints.column(maxY).entrySet();
            for (Map.Entry<Integer, Point> e : lastColumnSet) {
                System.out.println("Removing " + e.getValue());
                points.remove(e.getValue());
            }
            Point maxPoint = null;
            int maxPointCount = 0;
            System.out.println("Checking " + points.size() + " points which have finite areas");
            for (Point p : points) {
                int pointCount = 0;
                for (Table.Cell<Integer, Integer, Point> cell : closestPoints.cellSet()) {
                    if (cell.getValue().equals(p)) {
                        pointCount++;
                    }
                }
                if (pointCount > maxPointCount) {
                    maxPointCount = pointCount;
                    maxPoint = p;
                }
            }

            System.out.println("Maximum area = " + maxPointCount);

            int closePoints = 0;
            // Find the size of the region containing all points which are less than 10,000 units away from all points.
            for (int x = 0; x <= maxX; ++x) {
                for (int y = 0; y <= maxY; ++y) {
                    Point thisPoint = new Point(x, y);
                    int totalDistance = 0;
                    for (Point p : points2) {
                        totalDistance += findManhattanDistance(thisPoint, p);
                        if (totalDistance > 10000) {
                            break;
                        }
                    }
                    if (totalDistance < 10000) {
                        ++closePoints;
                    }
                }
            }

            System.out.println("Points within 10000 units (Manhattan distance) of all points: " + closePoints);
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private static class Point {
        public int x;
        public int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof Point)) {
                return false;
            }
            Point p2 = (Point)other;
            return this.x == p2.x && this.y == p2.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }


        @Override
        public String toString() {
            return "Point{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }
    }

    private static int findManhattanDistance(Point a, Point b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }

}
