package org.thehills.brian.adventofcode;

import com.google.common.collect.HashBasedTable;
import javafx.util.Pair;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day17 {

    private static Pattern inputPattern = Pattern.compile("x=(\\d+), y=(\\d+)..(\\d+)");

    private static final int springX = 500;
    private static final int springY = 0;

    public static void main(String args[]) throws IOException {
        InputStream is = null;
        try {
            is = new FileInputStream(args[0]);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            HashBasedTable<Integer, Integer, Material> scanData = HashBasedTable.create();
            String line;
            int minY = Integer.MAX_VALUE;
            int maxY = Integer.MIN_VALUE;
            while ((line = reader.readLine()) != null) {
                Matcher m = inputPattern.matcher(line);
                if (m.matches()) {
                    int xCoord = Integer.parseInt(m.group(1));
                    int yBegin = Integer.parseInt(m.group(2));
                    int yEnd = Integer.parseInt(m.group(3));
                    for (int i = yBegin; i <= yEnd; ++i) {
                        scanData.put(i, xCoord, Material.CLAY);
                    }
                    if (yEnd > maxY) {
                        maxY = yEnd;
                    }
                    if (yBegin < minY) {
                        minY = yBegin;
                    }
                }
            }
            System.out.println("Range of y values: (" + minY + ".." + maxY + ")");
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private static void fill(HashBasedTable<Integer, Integer, Material> scanData, int springX, int springY, int maxY) {
        boolean stopped = false;
        int currentX = springX;
        int currentY = springY + 1;
        boolean goingDown = true;
        while (!stopped) {
            scanData.put(currentY, currentX, Material.WATER_RUN);
            // If we can go down, go down
            Material blockBelow = scanData.get(currentY + 1, currentX);
            if (blockBelow == Material.CLAY) {
                Pair<Integer, Integer> walls = findContainerSides(currentY, currentX, scanData);
                if (walls.getKey() == null || walls.getValue() == null) {
                    
                }
            }
        }

    }

    private static Pair<Integer, Integer> findContainerSides(int x, int y, HashBasedTable<Integer, Integer, Material> scanData) {
        boolean foundLeftWall = false;
        boolean foundRightWall = false;
        Integer leftWall = null;
        Integer rightWall = null;
        int currentX = x;
        while (!foundLeftWall) {
            if (scanData.get(y, --currentX) == Material.CLAY && scanData.get(y + 1, currentX) == Material.CLAY) {
                leftWall = currentX;
                foundLeftWall = true;
            }
        }
        while (!foundRightWall) {
            if (scanData.get(y, ++currentX) == Material.CLAY && scanData.get(y + 1, currentX) == Material.CLAY) {
                rightWall = currentX;
                foundRightWall = true;
            }
        }
        return new Pair<>(leftWall, rightWall);
    }

    public static enum Material {
        CLAY,
        SAND,
        WATER_RUN,
        WATER_STOP
    }
}
