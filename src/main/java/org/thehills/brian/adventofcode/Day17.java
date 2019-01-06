package org.thehills.brian.adventofcode;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import javafx.util.Pair;

import java.io.*;
import java.util.Collections;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day17 {

    private static Pattern inputPattern = Pattern.compile("x=(\\d+), y=(\\d+)..(\\d+)");
    private static Pattern inputPattern2 = Pattern.compile("y=(\\d+), x=(\\d+)..(\\d+)");

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
            int minX = Integer.MAX_VALUE;
            int maxX = Integer.MIN_VALUE;
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
                    if (xCoord > maxX) {
                        maxX = xCoord;
                    }
                    if (xCoord < minX) {
                        minX = xCoord;
                    }
                } else {
                    Matcher m2 = inputPattern2.matcher(line);
                    if (m2.matches()) {
                        int yCoord = Integer.parseInt(m2.group(1));
                        int xBegin = Integer.parseInt(m2.group(2));
                        int xEnd = Integer.parseInt(m2.group(3));
                        for (int i = xBegin; i <= xEnd; ++i) {
                            scanData.put(yCoord, i, Material.CLAY);
                        }
                        if (xEnd > maxX) {
                            maxX = xEnd;
                        }
                        if (xBegin < minX) {
                            minX = xBegin;
                        }
                        if (yCoord > maxY) {
                            maxY = yCoord;
                        }
                        if (yCoord < minY) {
                            minY = yCoord;
                        }
                    }
                }
            }
            ++maxX;
            --minX;
            System.out.println("Range of x values: (" + minX + ".." + maxX + ")");
            System.out.println("Range of y values: (" + minY + ".." + maxY + ")");
            for (int y = springY; y <= maxY; ++y) {
                for (int x = minX; x <= maxX; ++x) {
                    if (scanData.get(y, x) == null) {
                        scanData.put(y, x, Material.SAND);
                    }
                }
            }
            fill(scanData, springX, springY, springY, maxY, minX, maxX, Direction.DOWN);
            print(scanData, minX, maxX, minY, maxY);
            int wetCells = 0;
            int afterDraining = 0;
            for (int y = minY; y <= maxY; ++y) {
                for (int x = minX; x <= maxX; ++x) {
                    if (scanData.get(y, x) == Material.WATER_STOP || scanData.get(y, x) == Material.WATER_RUN) {
                        ++wetCells;
                    }
                    if (scanData.get(y, x) == Material.WATER_STOP) {
                        ++afterDraining;
                    }
                }
            }
            System.out.println("There are " + wetCells + " wet cells.");
            System.out.println("After draining, there are " + afterDraining + " wet cells.");
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

   private static int fill(HashBasedTable<Integer, Integer, Material> scanData, Integer x, Integer y, Integer minY, Integer maxY, Integer minX, Integer maxX, Direction direction) {
        if (x > maxX || x < minX) return 0;
        if (y > maxY || y < minY) return 0;
        scanData.put(y, x, Material.WATER_RUN);
        Material materialBelow;
        switch (direction) {
            case DOWN:
                materialBelow = scanData.get(y+1, x);
                if (materialBelow == null) {
                    return 0;
                }
                switch (materialBelow) {
                    case CLAY:
                    case WATER_STOP:
                        int leftWall = fill(scanData, x, y, minY, maxY, minX, maxX, Direction.LEFT);
                        int rightWall = fill(scanData, x, y, minY, maxY, minX, maxX, Direction.RIGHT);
                        for (int i = leftWall + 1; leftWall != 0 && rightWall != 0 && i < rightWall; ++i) {
                            scanData.put(y, i, Material.WATER_STOP);
                        }
                        if (scanData.get(y, x - 1) == Material.WATER_STOP && scanData.get(y, x + 1) == Material.WATER_STOP) {
                            scanData.put(y, x, Material.WATER_STOP);
                        }
                        return 0;
                    case SAND:
                        fill(scanData, x, y + 1, minY, maxY, minX, maxX, Direction.DOWN);
                        Material sMaterialBelow = scanData.get(y+1, x);
                        if (sMaterialBelow == Material.WATER_STOP) {
                            fill(scanData, x, y, minY, maxY, minX, maxX, Direction.DOWN);
                        }
                }
                return 0;
            case LEFT:
                Material materialLeft = scanData.get(y, x - 1);
                Material lMaterialBelow = scanData.get(y+1, x);
                if (materialLeft == Material.CLAY || materialLeft == Material.WATER_STOP) {
                    return x - 1;
                } else if (lMaterialBelow == Material.CLAY || lMaterialBelow == Material.WATER_STOP) {
                    return fill(scanData, x - 1, y, minY, maxY, minX, maxX, Direction.LEFT);
                } else {
                    fill(scanData, x, y + 1, minY, maxY, minX, maxX, Direction.DOWN);
                    Material eMaterialBelow = scanData.get(y+1, x);
                    if (eMaterialBelow == Material.WATER_STOP) {
                        fill(scanData, x, y, minY, maxY, minX, maxX, Direction.DOWN);
                    }
                    return 0;
                }

            case RIGHT:
                Material materialRight = scanData.get(y, x + 1);
                Material rMaterialBelow = scanData.get(y+1, x);
                if (materialRight == Material.CLAY || materialRight == Material.WATER_STOP) {
                    return x + 1;
                } else if (rMaterialBelow == Material.CLAY || rMaterialBelow == Material.WATER_STOP) {
                    return fill(scanData, x + 1, y, minY, maxY, minX, maxX, Direction.RIGHT);
                } else {
                    fill(scanData, x, y + 1, minY, maxY, minX, maxX, Direction.DOWN);
                    Material fMaterialBelow = scanData.get(y+1, x);
                    if (fMaterialBelow == Material.WATER_STOP) {
                        fill(scanData, x, y, minY, maxY, minX, maxX, Direction.DOWN);
                    }
                    return 0;
                }
        }
        return 0;
   }

    private static void print(HashBasedTable<Integer, Integer, Material> scanData, Integer minX, Integer maxX, Integer minY, Integer maxY) {
        for (int y = minY; y <= maxY; ++y) {
            for (int x = minX; x <= maxX; ++x) {
                Material cell = scanData.get(y, x);
                if (cell == null) {
                    System.out.print(".");
                } else {
                    switch (cell) {
                        case CLAY:
                            System.out.print("#");
                            break;
                        case SAND:
                            System.out.print(".");
                            break;
                        case WATER_RUN:
                            System.out.print("|");
                            break;
                        case WATER_STOP:
                            System.out.print("~");
                            break;
                    }
                }
            }
            System.out.println("");
        }
    }

    private static Pair<Integer, Integer> findContainerSides(int x, int y, HashBasedTable<Integer, Integer, Material> scanData, int minX, int maxX) {
        boolean foundLeftWall = false;
        boolean foundRightWall = false;
        Integer leftWall = null;
        Integer rightWall = null;
        int currentX = x + 1;
        while (!foundLeftWall && currentX >= minX && scanData.get(y+1, currentX) != Material.SAND) {
            if (scanData.get(y, --currentX) == Material.CLAY) {
                leftWall = currentX;
                foundLeftWall = true;
            }
        }
        currentX = x - 1;
        while (!foundRightWall && currentX <= maxX && scanData.get(y + 1, currentX) != Material.SAND) {
            if (scanData.get(y, ++currentX) == Material.CLAY) {
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

    public static enum Direction {
        LEFT,
        RIGHT,
        DOWN
    }
}
