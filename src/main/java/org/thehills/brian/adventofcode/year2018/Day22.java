package org.thehills.brian.adventofcode.year2018;

import com.google.common.collect.ArrayTable;
import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day22 {

    private static final Pattern depthPattern = Pattern.compile("^depth: (\\d+)");
    private static final Pattern targetPattern = Pattern.compile("^target: (\\d+),(\\d+)");

    public static void main(String args[]) throws IOException {
        InputStream is = null;
        try {
            is = new FileInputStream(args[0]);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            Integer caveSystemDepth = null;
            Integer targetX = null;
            Integer targetY = null;
            while((line = reader.readLine()) != null) {
                Matcher depthMatcher = depthPattern.matcher(line);
                Matcher targetMatcher = targetPattern.matcher(line);
                if (depthMatcher.matches()) {
                    caveSystemDepth = Integer.parseInt(depthMatcher.group(1));
                } else if (targetMatcher.matches()) {
                    targetX = Integer.parseInt(targetMatcher.group(1));
                    targetY = Integer.parseInt(targetMatcher.group(2));
                }

            }

            int maxX = targetX * 8;
            int maxY = targetY * 8;

            int[][] geologicIndices = new int[maxX + 1][maxY + 1];
            int[][] erosionLevels = new int[maxX + 1][maxY + 1];
            Terrain[][] terrainTypes = new Terrain[maxX + 1][maxY + 1];
            computeEverything(geologicIndices, erosionLevels, terrainTypes, caveSystemDepth, targetX, targetY, maxX, maxY);
            display(terrainTypes, targetX, targetY);

            int riskLevel = findRiskLevel(terrainTypes, targetX, targetY);

            System.out.println("Risk level = " + riskLevel);

            int distanceToTarget = findTimeToTarget(terrainTypes, targetX, targetY);

            System.out.println("Time to target = " + distanceToTarget);



        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private static enum Terrain {
        ROCKY,
        WET,
        NARROW;

        public List<Tool> getValidTools() {
            switch(this) {
                case ROCKY:
                    return Arrays.asList(Tool.CLIMBING_GEAR, Tool.TORCH);
                case WET:
                    return Arrays.asList(Tool.CLIMBING_GEAR, Tool.NO_TOOL);
                case NARROW:
                    return Arrays.asList(Tool.TORCH, Tool.NO_TOOL);
            }
            return Collections.EMPTY_LIST;
        }
    }

    private static void computeEverything(int[][] geologicIndices, int[][] erosionLevels, Terrain[][] terrainTypes, int caveSystemDepth, int targetX, int targetY, int maxX, int maxY) {

        for (int y = 0; y <= maxY; ++y) {
            for (int x = 0; x <= maxX; ++x) {
                if ((x == 0 && y == 0) || (x == targetX && y == targetY)) {
                    geologicIndices[x][y] = 0;
                } else if (y == 0) {
                    geologicIndices[x][y] = 16807 * x;
                } else if (x == 0) {
                    geologicIndices[x][y] = 48271 * y;
                } else {
                    geologicIndices[x][y] = erosionLevels[x-1][y] * erosionLevels[x][y-1];
                }

                erosionLevels[x][y] = (geologicIndices[x][y] + caveSystemDepth) % 20183;
                terrainTypes[x][y] = Terrain.values()[erosionLevels[x][y] % 3];
            }
        }
    }

    private static int findRiskLevel(Terrain[][] terrainTypes, int targetX, int targetY) {
        int riskLevel = 0;
        for (int y = 0; y <= targetY; ++y) {
            for (int x = 0; x <= targetX; ++x) {
                riskLevel += terrainTypes[x][y].ordinal();
            }
        }
        return riskLevel;
    }

    private static void display(Terrain[][] terrainTypes, int targetX, int targetY) {
        for (int y = 0; y < terrainTypes[0].length; ++y) {
            for (int x = 0; x < terrainTypes.length; ++x) {
                if (x == 0 && y == 0) {
                    System.out.print('M');
                } else if (x == targetX && y == targetY) {
                    System.out.print('T');
                } else {
                    switch (terrainTypes[x][y]) {
                        case ROCKY:
                            System.out.print('.');
                            break;
                        case WET:
                            System.out.print('=');
                            break;
                        case NARROW:
                            System.out.print('|');
                            break;
                    }
                }
            }
            System.out.print("\n");
        }
    }

    private static int findTimeToTarget(Terrain[][] terrainTypes, int targetX, int targetY) {
        PriorityQueue<Location> locations = new PriorityQueue<>();
        Location start = new Location(0, 0, 0, Tool.TORCH, null);
        locations.add(start);
        Location closestLocation = null;
        HashSet<Location> created = new HashSet<>();
        HashMap<Location, Integer> distances = new HashMap<>();
        created.add(start);
        distances.put(start, 0);
        while (!locations.isEmpty()) {
            closestLocation = findClosestLocation(locations);



            locations.remove(closestLocation);
            for (Location neighbor : getNeighbors(closestLocation, locations, terrainTypes.length, terrainTypes[0].length)) {
                if (terrainTypes[neighbor.x][neighbor.y].getValidTools().contains(closestLocation.tool)) {
                    int potentialDistance = closestLocation.distance + 1;
                    neighbor.distance = potentialDistance;
                    neighbor.tool = closestLocation.tool;
                    neighbor.previous = closestLocation;
                    if (!created.contains(neighbor)) {
                        locations.add(neighbor);
                        created.add(neighbor);
                        distances.put(neighbor, neighbor.distance);
                    } else if (distances.get(neighbor) > neighbor.distance) {
                        locations.remove(neighbor);
                        created.remove(neighbor);
                        distances.remove(neighbor);
                        locations.add(neighbor);
                        created.add(neighbor);
                        distances.put(neighbor, neighbor.distance);
                    }
                }
                for (Tool t : Tool.values()) {
                    if (t != closestLocation.tool && terrainTypes[closestLocation.x][closestLocation.y].getValidTools().contains(t)) {
                        Location alternate = new Location(closestLocation.x, closestLocation.y, closestLocation.distance + 7, t, closestLocation.previous);
                        if (!created.contains(alternate)) {
                            locations.add(alternate);
                            created.add(alternate);
                            distances.put(alternate, alternate.distance);
                        } else if (distances.containsKey(alternate) && distances.get(alternate) > alternate.distance) {
                            locations.remove(alternate);
                            created.remove(alternate);
                            distances.remove(alternate);
                            locations.add(alternate);
                            created.add(alternate);
                            distances.put(alternate, alternate.distance);
                        }
                    }
                }
            }
        }

        for (Location l : created) {
            if (l.x == targetX && l.y == targetY && l.tool == Tool.TORCH) {
                closestLocation = l;
            }
        }
        if (closestLocation.x == targetX && closestLocation.y == targetY) {
            System.out.println("Path: ");
            Location now = closestLocation;
            while (now != null && (now.x != 0 || now.y != 0)) {
                System.out.println(now.x + "," + now.y + " :: " + terrainTypes[now.x][now.y] + " :: " + now.tool + " :: " + now.distance);
                now = now.previous;
            }
            if (closestLocation.tool != Tool.TORCH) {
                return closestLocation.distance + 7;
            }
            return closestLocation.distance;
        }

        return Integer.MAX_VALUE;

    }

    private static enum Tool {
        TORCH,
        CLIMBING_GEAR,
        NO_TOOL;

        public List<Terrain> getValidTerrain() {
            switch(this) {
                case TORCH:
                    return Arrays.asList(Terrain.ROCKY, Terrain.NARROW);
                case CLIMBING_GEAR:
                    return Arrays.asList(Terrain.ROCKY, Terrain.WET);
                case NO_TOOL:
                    return Arrays.asList(Terrain.WET, Terrain.NARROW);
            }
            return Collections.EMPTY_LIST;
        }
    }

    private static Pair<Integer, Tool> findDistance(Location start, Location next, Terrain[][] terrainTypes, Tool currentTool) {
        List<Pair<Integer, Tool>> results = new ArrayList<>();
        if (currentTool.getValidTerrain().contains(terrainTypes[next.x][next.y])) {
            // Current tool good for new location
            return new Pair<>(1, currentTool);
        } else {
            // Need to change tools to get to new location
            for (Tool t : terrainTypes[next.x][next.y].getValidTools()) {
                if (t.getValidTerrain().contains(terrainTypes[start.x][start.y])) {
                    return new Pair<>(8, t);
                }
            }
            return new Pair<>(Integer.MAX_VALUE, currentTool);
        }
    }

    private static final List<Location> getNeighbors(Location location, PriorityQueue<Location> locations, int maxX, int maxY) {

        List<Location> adjacentLocations = new ArrayList<>();
        if (location.y-1 >= 0) {
            Location l = new Location(location.x, location.y - 1, Integer.MAX_VALUE, null, null);
            adjacentLocations.add(l);
        }
        if (location.y+1 < maxY) {
            Location l = new Location(location.x, location.y + 1, Integer.MAX_VALUE, null, null);
            adjacentLocations.add(l);
        }
        if (location.x+1 < maxX) {
            Location l = new Location(location.x + 1, location.y, Integer.MAX_VALUE, null, null);
            adjacentLocations.add(l);
        }
        if (location.x-1 >= 0) {
            Location l = new Location(location.x-1, location.y, Integer.MAX_VALUE, null, null);
            adjacentLocations.add(l);
        }
        return adjacentLocations;
    }

    private static boolean isAdjacentTo(Location square, Location location) {
        if (location.x == square.x && (location.y - square.y == 1 || location.y - square.y == -1)) {
            return true;
        }
        if (location.y == square.y && (location.x - square.x == 1 || location.x - square.x == -1)) {
            return true;
        }
        return false;
    }

    private static Location findClosestLocation(PriorityQueue<Location> locations) {
        return locations.poll();
    }

    private static class Location implements Comparator<Location>, Comparable<Location> {
        public int x;
        public int y;
        public int distance;
        public Tool tool;
        public Location previous;

        public Location(int x, int y, int distance, Tool tool, Location previous) {
            this.x = x;
            this.y = y;
            this.distance = distance;
            this.tool =tool;
            this.previous = previous;
        }

        @Override
        public int compare(Location o1, Location o2) {
            if (o1.distance < o2.distance) {
                return -1;
            } else if (o1.distance == o2.distance) {
                return 0;
            } else {
                return 1;
            }
        }

        @Override
        public int compareTo(Location o) {
            return compare(this, o);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Location location = (Location) o;
            return x == location.x &&
                    y == location.y &&
                    tool == location.tool;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, tool);
        }
    }
}
