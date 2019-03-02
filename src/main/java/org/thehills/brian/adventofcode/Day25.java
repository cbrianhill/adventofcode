package org.thehills.brian.adventofcode;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day25 {

    private static final Pattern inputPattern = Pattern.compile("([\\d-]+),([\\d-]+),([\\d-]+),([\\d-]+)");

    public static void main(String args[]) throws IOException {
        InputStream is = null;
        try {
            is = new FileInputStream(args[0]);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            List<Point> points = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                Matcher m = inputPattern.matcher(line);
                if (m.matches()) {
                    Point p = new Point(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)),
                            Integer.parseInt(m.group(3)), Integer.parseInt(m.group(4)));
                    points.add(p);
                }
            }

            List<Constellation> constellations = new ArrayList<>();

            for (Point p : points) {
                System.out.println("Adding point " + p);
                boolean addedToExistingConstellation = false;
                for (Constellation c : constellations) {
                    if (c.findMinManhattanDistance(p) <= 3) {
                        c.add(p);
                        addedToExistingConstellation = true;
                        // Check if this constellation should now be merged with another one.
                        boolean moreMergingIsLeft = true;
                        while (moreMergingIsLeft) {
                            List<Constellation> removeMe = new ArrayList<>();
                            moreMergingIsLeft = false;
                            for (Constellation c2 : constellations) {
                                if (c != c2 && c2.shouldMerge(c)) {
                                    c2.add(c);
                                    removeMe.add(c);
                                    c = c2;
                                    moreMergingIsLeft = true;
                                    break;
                                }
                            }
                            constellations.removeAll(removeMe);
                        }
                        break;
                    }
                }
                if (addedToExistingConstellation) {
                    continue;
                }
                Constellation c = new Constellation(p);
                constellations.add(c);
            }

            System.out.println("There are " + constellations.size() + " constellations.");

            // Now, merge the constellations:


        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public static int findManhattanDistance(Point a, Point b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y) + Math.abs(a.z - b.z) + Math.abs(a.t - b.t);
    }

    public static class Point {
        public int x;
        public int y;
        public int z;
        public int t;

        public Point(int x, int y, int z, int t) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.t = t;
        }

        public String toString() {
            return x + "," + y + "," + z + "," + t;
        }
    }

    public static class Constellation {
        public List<Point> points = new ArrayList<>();

        public Constellation(Point p) {
            points.add(p);
        }

        public void add(Point p) {
            points.add(p);
        }
        public void add(Constellation c) {
            points.addAll(c.points);
        }

        public boolean shouldMerge(Constellation c) {
            for (Point p : c.points) {
                if (findMinManhattanDistance(p) <= 3) {
                    return true;
                }
            }
            return false;
        }

        public int findMinManhattanDistance(Point external) {
            return points.stream()
                    .map(p -> findManhattanDistance(p, external))
                    .reduce(Integer.MAX_VALUE, Math::min);
        }
    }
}
