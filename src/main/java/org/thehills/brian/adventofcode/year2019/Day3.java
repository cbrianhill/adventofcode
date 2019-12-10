package org.thehills.brian.adventofcode.year2019;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import javafx.util.Pair;

public class Day3
{
    public static void main(String args[]) throws IOException
    {
        InputStream is = null;
        try {
            is = new FileInputStream(args[0]);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            List<Integer> originalCodes = new ArrayList<>();
            HashBasedTable<Integer, Integer, GridPoint> wireGrid = HashBasedTable.create();
            int wire = 0;
            while ((line = reader.readLine()) != null) {
                ++wire;
                Pair<Integer, Integer> currentLocation = new Pair<>(0, 0);
                String[] splitLine = line.split(",");
                for (String val : splitLine) {
                    currentLocation = fillTable(wireGrid, currentLocation, val, wire);
                }
            }

            int distance = findManhattanDistanceToClosestGridPointWithTwoWires(wireGrid);
            System.out.println("Closest Manhattan distance = " + distance);
            int combinedDistance = findCombinedDistanceToClosestIntersectionBySignalDistance(wireGrid);
            System.out.println("Closest combined signal distance = " + combinedDistance);

        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public static Pair<Integer, Integer> fillTable(HashBasedTable<Integer, Integer, GridPoint> table,
                                                   Pair<Integer, Integer> currentLocation, String val, int wire) {
        char direction = val.charAt(0);
        int deltaX = 0;
        int deltaY = 0;
        if (direction == 'U') {
            deltaY = 1;
            deltaX = 0;
        } else if (direction == 'D') {
            deltaY = -1;
            deltaX = 0;
        } else if (direction == 'L') {
            deltaX = -1;
            deltaY = 0;
        } else if (direction == 'R') {
            deltaX = 1;
            deltaY = 0;
        }
        int duration = Integer.parseInt(val.substring(1));
        Pair<Integer, Integer> location = currentLocation;
        int previousDistance = 0;
        if (wire == 1 && table.get(currentLocation.getKey(), currentLocation.getValue()) != null) {
            previousDistance = table.get(currentLocation.getKey(), currentLocation.getValue()).wire1Distance;
        } else if (wire == 2 && table.get(currentLocation.getKey(), currentLocation.getValue()) != null) {
            previousDistance = table.get(currentLocation.getKey(), currentLocation.getValue()).wire2Distance;
        }
        for (int i = 1; i <= duration; ++i) {
            location = new Pair<>(currentLocation.getKey() + deltaX * i,
                                                         currentLocation.getValue() + deltaY * i);
            previousDistance = addWire(table, location, wire, previousDistance);
        }
        return location;
    }

    public static int addWire(HashBasedTable<Integer, Integer, GridPoint> table,
                               Pair<Integer, Integer> location,
                               int wire, int previousDistance) {
        GridPoint currentGridPoint = table.get(location.getKey(), location.getValue());
        if (currentGridPoint == null)
        {
            currentGridPoint = new GridPoint();
        }
        if (wire == 1 && !currentGridPoint.wire1) {
            currentGridPoint.wire1 = true;
            currentGridPoint.wire1Distance = ++previousDistance;
        } else if (wire == 2 && !currentGridPoint.wire2) {
            currentGridPoint.wire2 = true;
            currentGridPoint.wire2Distance = ++previousDistance;
        } else {
            ++previousDistance;
        }
        table.put(location.getKey(), location.getValue(), currentGridPoint);
        return previousDistance;
    }

    public static int findManhattanDistanceToClosestGridPointWithTwoWires(
            HashBasedTable<Integer, Integer, GridPoint> table) {
        int closestDistance = Integer.MAX_VALUE;
        for (Table.Cell<Integer, Integer, GridPoint> cell : table.cellSet()) {
            GridPoint gp = cell.getValue();
            if (gp.wire2 && gp.wire1)
            {
                int md = findManhattanDistanceForCell(cell);
                if (md < closestDistance)
                {
                    closestDistance = md;
                }
            }
        }

        return closestDistance;
    }

    public static int findCombinedDistanceToClosestIntersectionBySignalDistance(
            HashBasedTable<Integer, Integer, GridPoint> table) {
        int bestCombinedDistance = Integer.MAX_VALUE;
        for (Table.Cell<Integer, Integer, GridPoint> cell: table.cellSet()) {
            GridPoint gp = cell.getValue();
            if (gp.wire1 && gp.wire2 && (gp.wire1Distance + gp.wire2Distance < bestCombinedDistance)) {
                bestCombinedDistance = gp.wire1Distance + gp.wire2Distance;
            }
        }
        return bestCombinedDistance;
    }

    public static int findManhattanDistanceForCell(Table.Cell<Integer, Integer, GridPoint> cell) {
        return Math.abs(cell.getRowKey()) + Math.abs(cell.getColumnKey());
    }

    public static class GridPoint {
        boolean wire1;
        boolean wire2;
        int wire1Distance;
        int wire2Distance;
    }
}
