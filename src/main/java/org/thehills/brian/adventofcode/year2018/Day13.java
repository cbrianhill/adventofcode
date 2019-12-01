package org.thehills.brian.adventofcode.year2018;

import java.io.*;
import java.util.ArrayList;

import com.google.common.collect.HashBasedTable;

public class Day13 {

    public static void main(String args[]) throws IOException {
        InputStream is = null;
        try {
            is = new FileInputStream(args[0]);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            ArrayList<char[]> lines = new ArrayList<>();
            int longestLine = 0;
            while ((line = reader.readLine()) != null) {
                lines.add(line.toCharArray());
                if (line.length() + 1 > longestLine) {
                    longestLine = line.length() + 1;
                }
            }
            HashBasedTable<Integer, Integer, Car> locationTable = HashBasedTable.create();
            char[][] trafficGrid = lines.toArray(new char[lines.size()][longestLine]);
            ArrayList<Car> cars = new ArrayList<>();
            for (int y = 0; y < trafficGrid.length; ++y) {
                for (int x = 0; x < trafficGrid[y].length; ++x) {
                    if (trafficGrid[y][x] == '>' ||
                        trafficGrid[y][x] == '<' ||
                        trafficGrid[y][x] == '^' ||
                        trafficGrid[y][x] == 'v') {
                        Car car = new Car(trafficGrid[y][x], x, y);
                        cars.add(car);
                        locationTable.put(y, x, car);
                        // This assumes that cars can't start on a curve or at an intersection.
                        // Not sure if that's an unreasonable constraint, but it holds for my input.
                        if (trafficGrid[y][x] == '>' || trafficGrid[y][x] == '<') {
                            trafficGrid[y][x] = '-';
                        } else {
                            trafficGrid[y][x] = '|';
                        }
                    }
                }
            }
            boolean collisionOccurred = false;
            int ticks = 0;
            while (!collisionOccurred) {
                ++ticks;
                HashBasedTable<Integer, Integer, Car> newLocationTable = HashBasedTable.create(locationTable);
                for (Integer row : locationTable.rowMap().keySet()) {
                    for (Integer column : locationTable.row(row).keySet()) {
                        Car c = locationTable.get(row, column);
                        if (!c.alive) {
                            continue;
                        }
                        newLocationTable.remove(c.y, c.x);
                        // Advance the car to the next position
                        if (Direction.NORTH.equals(c.direction)) {
                            c.y--;
                        } else if (Direction.SOUTH.equals(c.direction)) {
                            c.y++;
                        } else if (Direction.EAST.equals(c.direction)) {
                            c.x++;
                        } else {
                            c.x--;
                        }

                        if (newLocationTable.get(c.y, c.x) != null && newLocationTable.get(c.y, c.x).alive) {
                            System.out.println("Collision at " + c.x + "," + c.y);
                            newLocationTable.get(c.y, c.x).alive = false;
                            newLocationTable.remove(c.y, c.x);
                            c.alive = false;
                            continue;
                        } else {
                            newLocationTable.put(c.y, c.x, c);
                        }


                        // Determine the direction based on new position
                        if (trafficGrid[c.y][c.x] == '\\') {
                            if (Direction.NORTH.equals(c.direction)) {
                                c.direction = Direction.WEST;
                            } else if (Direction.EAST.equals(c.direction)) {
                                c.direction = Direction.SOUTH;
                            } else if (Direction.SOUTH.equals(c.direction)) {
                                c.direction = Direction.EAST;
                            } else if (Direction.WEST.equals(c.direction)) {
                                c.direction = Direction.NORTH;
                            }
                        } else if (trafficGrid[c.y][c.x] == '/') {
                            if (Direction.NORTH.equals(c.direction)) {
                                c.direction = Direction.EAST;
                            } else if (Direction.EAST.equals(c.direction)) {
                                c.direction = Direction.NORTH;
                            } else if (Direction.SOUTH.equals(c.direction)) {
                                c.direction = Direction.WEST;
                            } else if (Direction.WEST.equals(c.direction)) {
                                c.direction = Direction.SOUTH;
                            }
                        } else if (trafficGrid[c.y][c.x] == '+') {
                            // Handle a turn
                            if (TurnDirection.LEFT.equals(c.nextTurn)) {
                                c.nextTurn = TurnDirection.STRAIGHT;
                                switch(c.direction) {
                                    case NORTH:
                                        c.direction = Direction.WEST;
                                        break;
                                    case EAST:
                                        c.direction = Direction.NORTH;
                                        break;
                                    case SOUTH:
                                        c.direction = Direction.EAST;
                                        break;
                                    case WEST:
                                        c.direction = Direction.SOUTH;
                                        break;
                                }
                            } else if (TurnDirection.STRAIGHT.equals(c.nextTurn)) {
                                c.nextTurn = TurnDirection.RIGHT;
                            } else {
                                c.nextTurn = TurnDirection.LEFT;
                                switch(c.direction) {
                                    case NORTH:
                                        c.direction = Direction.EAST;
                                        break;
                                    case EAST:
                                        c.direction = Direction.SOUTH;
                                        break;
                                    case SOUTH:
                                        c.direction = Direction.WEST;
                                        break;
                                    case WEST:
                                        c.direction = Direction.NORTH;
                                        break;
                                }

                            }
                        }
                        System.out.println("Tick " + ticks + ", car at " + c.x + "," + c.y + ", moving " + c.direction.toString() + " next turn " + c.nextTurn.toString());
                    }
                }
                locationTable = newLocationTable;
                if (locationTable.cellSet().stream().filter(c -> c.getValue().alive).count() == 1) {
                    Car c = locationTable.cellSet().stream().filter(s -> s.getValue().alive).findFirst().get().getValue();
                    System.out.println("Only one car remains: " + c.x + "," + c.y);
                    collisionOccurred = true;
                }
            }
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private static enum Direction {
        NORTH,
        EAST,
        SOUTH,
        WEST
    }

    private static enum TurnDirection {
        LEFT,
        STRAIGHT,
        RIGHT
    }

    private static class Car {
        public Car(char rep, int x, int y) {
            if (rep == '>') direction = Direction.EAST;
            else if (rep == '<') direction = Direction.WEST;
            else if (rep == '^') direction = Direction.NORTH;
            else if (rep == 'v') direction = Direction.SOUTH;
            this.x = x;
            this.y = y;
            nextTurn = TurnDirection.LEFT;
        }
        public Direction direction;
        public int x;
        public int y;
        public TurnDirection nextTurn;
        public boolean alive = true;

    }
}
