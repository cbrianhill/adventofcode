package org.thehills.brian.adventofcode;

import com.google.common.collect.ArrayTable;
import com.google.common.collect.HashBasedTable;

public class Day11 {

    public static void main(String args[]) {
        Integer input = 9005;
        HashBasedTable<Integer, Integer, Integer> dataTable = HashBasedTable.create();
        for (int i = 1; i < 301; ++i) {
            for (int j = 1; j < 301; ++j) {
                dataTable.put(i, j, findPowerLevel(input, i, j));
            }
        }
        Integer maxPowerSum = Integer.MIN_VALUE;
        Integer maxX = 0;
        Integer maxY = 0;
            for (int i = 1; i < 299; ++i) {
                for (int j = 1; j < 299; ++j) {
                    Integer powerSum = findPowerLevelSum(dataTable, input, i, j, 3);
                    if (powerSum > maxPowerSum) {
                        maxX = i;
                        maxY = j;
                        maxPowerSum = powerSum;
                    }
                }
            }
        System.out.println("Maximum power sum = " + maxPowerSum + ", (" + maxX + "," + maxY + ")");
        maxPowerSum = Integer.MIN_VALUE;
        maxX = 0;
        maxY = 0;
        Integer maxSize = 0;
        for (int i = 1; i < 301; ++i) {
            for (int j = 1; j < 301; ++j) {
                Integer startingSum = dataTable.get(i, j);
                if (startingSum > maxPowerSum) {
                    maxX = i;
                    maxY = j;
                    maxSize = 1;
                    maxPowerSum = startingSum;
                }
                for (int s = 2; s < 301 - i && s < 301 - j; ++s) {
                    startingSum += findIncrementalPowerLevelSum(dataTable, input, i, j, s);
                    // System.out.println("(" + i + "," + j + "," + s + ") = " + startingSum);
                    if (startingSum > maxPowerSum) {
                        maxX = i;
                        maxY = j;
                        maxSize = s;
                        maxPowerSum = startingSum;
                    }
                }
            }
        }
        System.out.println("Maximum power sum = " + maxPowerSum + ", (" + maxX + "," + maxY + "," + maxSize+ ")");
    }

    public static int findPowerLevel(int serial, int x, int y) {
        Integer rackId = 10 + x;
        Integer powerLevel = rackId * y;
        powerLevel += serial;
        powerLevel *= rackId;
        powerLevel -= powerLevel % 100;
        powerLevel = powerLevel % 1000;
        powerLevel = powerLevel / 100;
        powerLevel -= 5;
        return powerLevel;
    }

    public static int findPowerLevelSum(HashBasedTable<Integer, Integer, Integer> dataTable, int serial, int x, int y, int squareSize) {
        Integer powerSum = 0;
        for (int i = x; i < x + squareSize; ++i) {
            for (int j = y; j < y + squareSize; ++j) {
                powerSum += dataTable.get(i, j);
            }
        }
        return powerSum;
    }
    public static int findIncrementalPowerLevelSum(HashBasedTable<Integer, Integer, Integer> dataTable, int serial, int x, int y, int squareSize) {
        Integer powerSum = 0;
        int incrementalx = x + squareSize - 1;
        int incrementaly = y + squareSize - 1;
        for (int xcoord = x; xcoord <= incrementalx; ++xcoord) {
            // System.out.println("--> adding " + xcoord + "," + incrementaly);
            powerSum += dataTable.get(xcoord, incrementaly);
        }
        for (int ycoord = y; ycoord < incrementaly; ++ycoord) {
            // System.out.println("--> adding " + incrementalx + "," + ycoord);
            powerSum += dataTable.get(incrementalx, ycoord);
        }

        return powerSum;
    }
}
