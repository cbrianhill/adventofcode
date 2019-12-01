package org.thehills.brian.adventofcode.year2018;

import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Day18 {

    public static void main(String args[]) throws IOException {
        InputStream is = null;
        try {
            is = new FileInputStream(args[0]);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            List<String> lines = reader.lines().collect(Collectors.toList());
            Contents[][] grid = new Contents[lines.size()][lines.get(0).length()];
            for (int i = 0; i < lines.size(); ++i) {
                String line = lines.get(i);
                for (int j = 0; j < line.length(); ++j) {
                    char n = line.charAt(j);
                    if (n == '.')
                        grid[i][j] = Contents.OPEN;
                    else if (n == '|')
                        grid[i][j] = Contents.TREES;
                    else
                        grid[i][j] = Contents.LUMBERYARD;
                }
            }
            int height = grid.length;
            int width = grid[0].length;

            print(grid);
            HashSet<Integer> previousValues = new HashSet<>();
            for (int i = 0; i < 524; ++i) {
                // System.out.println("Simulating minute " + (i + 1));
                grid = simulateMinute(grid);
                int wooded = 0;
                int lumberyards = 0;
                for (int row = 0; row < grid.length; ++row) {
                    for (int column = 0; column < grid[row].length; ++column) {
                        switch(grid[row][column]) {
                            case TREES:
                                ++wooded;
                                break;
                            case LUMBERYARD:
                                ++lumberyards;
                                break;
                        }
                    }
                }
                int newResourceValue = wooded * lumberyards;
                boolean duplicate = previousValues.contains(newResourceValue);
                previousValues.add(newResourceValue);
                System.out.println("Total resource value = " + (wooded * lumberyards) + " at minute " + (i+1));
                if (duplicate) {
                    System.out.println("---> Duplicate value found");
                }
            }

        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public static enum Contents {
        OPEN,
        TREES,
        LUMBERYARD
    }

    public static void print(Contents[][] grid) {
        for (int i = 0; i < grid.length; ++i) {
            for (int j = 0; j < grid[i].length; ++j) {
                switch(grid[i][j]) {
                    case OPEN:
                        System.out.print(".");
                        break;
                    case TREES:
                        System.out.print("|");
                        break;
                    case LUMBERYARD:
                        System.out.print("#");
                        break;
                }
            }
            System.out.print("\n");
        }
    }

    public static List<Contents> findAdjacentContents(Contents[][] grid, int row, int column) {
        Set<Pair<Integer, Integer>> coords = new HashSet<>();
        coords.add(new Pair<>(row - 1, column - 1));
        coords.add(new Pair<>(row - 1, column));
        coords.add(new Pair<>(row - 1, column + 1));
        coords.add(new Pair<>(row, column - 1));
        coords.add(new Pair<>(row, column + 1));
        coords.add(new Pair<>(row + 1, column - 1));
        coords.add(new Pair<>(row + 1, column));
        coords.add(new Pair<>(row + 1, column + 1));
        return coords.stream()
                .filter(e -> (e.getKey() >= 0 && e.getKey() < grid.length) && (e.getValue() >= 0 && e.getValue() < grid[0].length))
                .map(e -> grid[e.getKey()][e.getValue()])
                .collect(Collectors.toList());
    }

    public static Contents[][] simulateMinute(Contents[][] grid) {
        Contents[][] newGrid = new Contents[grid.length][grid[0].length];

        for (int row = 0; row < grid.length; ++row) {
            for (int column = 0; column < grid[row].length; ++column) {
                List<Contents> adjacentContents = findAdjacentContents(grid, row, column);
                Contents currentContents = grid[row][column];
                switch (currentContents) {
                    case OPEN:
                        long adjacentTrees = adjacentContents.stream().filter(c -> c == Contents.TREES).count();
                        if (adjacentTrees >= 3) {
                            newGrid[row][column] = Contents.TREES;
                        } else {
                            newGrid[row][column] = Contents.OPEN;
                        }
                        break;
                    case TREES:
                        long adjacentLumberyards = adjacentContents.stream().filter(c -> c == Contents.LUMBERYARD).count();
                        if (adjacentLumberyards >= 3) {
                            newGrid[row][column] = Contents.LUMBERYARD;
                        } else {
                            newGrid[row][column] = Contents.TREES;
                        }
                        break;
                    case LUMBERYARD:
                        long nearbyLumberyards = adjacentContents.stream().filter(c -> c == Contents.LUMBERYARD).count();
                        long nearbyTrees = adjacentContents.stream().filter(c -> c == Contents.TREES).count();
                        if (nearbyLumberyards >= 1 && nearbyTrees >= 1) {
                            newGrid[row][column] = Contents.LUMBERYARD;
                        } else {
                            newGrid[row][column] = Contents.OPEN;
                        }
                        break;
                }
            }
        }
        return newGrid;
    }
}
