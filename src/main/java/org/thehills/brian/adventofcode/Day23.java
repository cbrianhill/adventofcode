package org.thehills.brian.adventofcode;

import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day23 {

    private static final Pattern inputPattern = Pattern.compile("pos=<(-?\\d+),(-?\\d+),(-?\\d+)>, r=(\\d+)");

    public static void main(String args[]) throws IOException {
        InputStream is = null;
        try {
            is = new FileInputStream(args[0]);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            List<Nanobot> bots = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                Matcher m = inputPattern.matcher(line);
                if (m.matches()) {
                    long x = Long.parseLong(m.group(1));
                    long y = Long.parseLong(m.group(2));
                    long z = Long.parseLong(m.group(3));
                    long radius = Long.parseLong(m.group(4));

                    Nanobot n = new Nanobot(x, y, z, radius);
                    bots.add(n);
                }
            }

            Nanobot strongest = bots.get(0);
            long minx = 0, maxx = 0;
            long miny = 0, maxy = 0;
            long minz = 0, maxz = 0;
            for (Nanobot n : bots) {
                if (n.radius > strongest.radius) {
                    strongest = n;
                }
                if (n.x > maxx) maxx = n.x;
                if (n.x < minx) minx = n.x;
                if (n.y > maxy) maxy = n.y;
                if (n.y < miny) miny = n.y;
                if (n.z > maxz) maxz = n.z;
                if (n.z < minz) minz = n.z;
            }

            int closeBots = 0;
            for (Nanobot n : bots) {
                if (findManhattanDistance(n, strongest) <= strongest.radius) {
                    ++closeBots;
                }
            }

            System.out.println(closeBots + " nanobots are within range of the strongest bot.");
            System.out.println("Coordinate system range:");
            System.out.println("X: " + minx + " <-> " + maxx);
            System.out.println("Y: " + miny + " <-> " + maxy);
            System.out.println("Z: " + minz + " <-> " + maxz);

            long resolution = 1;
            while (resolution < maxx - minx || resolution < maxy - miny || resolution < maxz - minz) {
                resolution *= 2;
            }

            long offsetX = -1 * minx;
            long offsetY = -1 * miny;
            long offsetZ = -1 * minz;

            long span = 1;
            while (span < bots.size()) {
                span = span * 2;
            }

            long forcedCheck = 1;

            // Map of MD -> MD + count of bots in range
            Map<Long, Pair<Long, Long>> previousResults = new HashMap<>();

            long bestMD = Long.MAX_VALUE, bestCount = 0;

            while(true) {
                if (!previousResults.containsKey(forcedCheck)) {
                    Pair<Long, Long> result = findLocalOptimum(bots, minx, maxx, miny, maxy, minz, maxz, resolution,
                            offsetX, offsetY, offsetZ, forcedCheck);
                    previousResults.put(forcedCheck, result);
                }

                Long testMD = previousResults.get(forcedCheck).getKey();
                Long testCount = previousResults.get(forcedCheck).getValue();

                if (testMD == null) {
                    if (span > 1) {
                        span = span / 2;
                    }
                    forcedCheck = Math.max(1, forcedCheck - span);
                } else {
                    if (testCount > bestCount) {
                        bestMD = testMD;
                        bestCount = testCount;
                    }
                    if (span == 1) {
                        break;
                    }
                    forcedCheck += span;
                }
            }

            // We are done!
            System.out.println("Best MD = " + bestMD + ", in range of " + bestCount + " boxes.");

        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private static long findManhattanDistance(Point a, Point b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y) + Math.abs(a.z - b.z);
    }

    private static class Point {
        public long x;
        public long y;
        public long z;

        public Point (long x, long y, long z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Point point = (Point) o;
            return x == point.x &&
                    y == point.y &&
                    z == point.z;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, z);
        }
    }
    private static class Nanobot extends Point {
        public long radius;

        public Nanobot (long x, long y, long z, long radius) {
            super(x, y, z);
            this.radius = radius;
        }
    }

    private static Pair<Long, Long> findLocalOptimum(List<Nanobot> bots, long minx, long maxx, long miny, long maxy,
                                                     long minz, long maxz, long resolution, long offsetX, long offsetY,
                                                     long offsetZ, long minimumCount) {

        Map<Point, Long> atTarget = new HashMap<>();
        for (long x = minx; x <= maxx; x += resolution) {
            for (long y = miny; y <= maxy; y += resolution) {
                for (long z = minz; z <= maxz; z += resolution) {
                    // Find how many bots are in range of this spot.
                    long botCount = 0;
                    for (Nanobot b : bots) {
                        if (resolution == 1) {
                            long distance = findManhattanDistance(b, new Point(x,y,z));
                            if (distance <= b.radius) {
                                ++botCount;
                            }
                        } else {
                            long distance = Math.abs(offsetX + x - offsetX - b.x);
                            distance += Math.abs(offsetY + y - offsetY - b.y);
                            distance += Math.abs(offsetZ + z - offsetZ - b.z);
                            if (distance / resolution - 3 <= b.radius / resolution) {
                                ++botCount;
                            }
                        }
                    }
                    if (botCount >= minimumCount) {
                        Point p = new Point(x,y,z);
                        atTarget.put(p, botCount);
                    }
                }
            }
        }

        while (atTarget.size() > 0) {
            Point best = null;
            long bestDistance = Long.MAX_VALUE;
            long bestCount = 0;
            for (Map.Entry<Point, Long> e : atTarget.entrySet()) {
                long thisDistance = findManhattanDistance(ORIGIN, e.getKey());
                if (thisDistance < bestDistance) {
                    best = e.getKey();
                    bestDistance = thisDistance;
                    bestCount = e.getValue();
                }
            }

            if (resolution == 1) {
                return new Pair<>(bestDistance, bestCount);
            } else {
                Pair<Long, Long> subResult = findLocalOptimum(bots, best.x, best.x + resolution / 2,
                        best.y, best.y + resolution / 2, best.z, best.z + resolution / 2,
                        resolution / 2, offsetX, offsetY, offsetZ, minimumCount);
                if (subResult.getKey() == null) {
                    atTarget.remove(best);
                } else {
                    return subResult;
                }
            }

        }

        return new Pair<>(null, null);
    }

    private static final Point ORIGIN = new Point(0,0,0);
}
