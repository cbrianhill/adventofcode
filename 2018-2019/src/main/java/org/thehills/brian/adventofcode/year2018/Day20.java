package org.thehills.brian.adventofcode.year2018;

import javafx.util.Pair;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day20 {
    private static final Pattern inputPattern = Pattern.compile("\\^(.+)\\$");

    public static void main(String args[]) throws IOException {
        InputStream is = null;
        try {
            is = new FileInputStream(args[0]);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String input = reader.readLine();
            Matcher m = inputPattern.matcher(input);
            if (m.matches()) {
                input = m.group(1);
                Room startingPoint = new Room(0);
                List<Room> allRooms = new ArrayList<>();
                createMap(input, 0, startingPoint, 0, allRooms);
                int maxLength = allRooms.stream().map(r -> r.distanceFromOrigin)
                        .max(Comparator.comparing(Integer::valueOf)).get();
                long farAwayRooms = allRooms.stream().filter(r -> r.distanceFromOrigin >= 1000).count();
                System.out.println("Farthest room = " + maxLength);
                System.out.println("Rooms >= 1000 distance = " + farAwayRooms);

            }

        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private static class PathState {
        public int stoppingPoint;
        public Room lastRoom;

        public PathState(int stoppingPoint, Room lastRoom) {
            this.stoppingPoint = stoppingPoint;
            this.lastRoom = lastRoom;
        }
    }

    private static class Room {
        public Room north;
        public Room south;
        public Room east;
        public Room west;
        public int distanceFromOrigin;

        public Room(int distanceFromOrigin) {
            this.distanceFromOrigin = distanceFromOrigin;
        }
    }

    private static final PathState createMap(String input, int startingPoint, Room currentRoom, int currentDistance, List<Room> allRooms) {
        int distanceFromOrigin = currentDistance;
        Room thisRoom = currentRoom;
        for (int i = startingPoint; i < input.length(); ++i) {
            if (input.charAt(i) == '(') {
                PathState result = createMap(input, i + 1, thisRoom, distanceFromOrigin, allRooms);
                i = result.stoppingPoint;
                thisRoom = result.lastRoom;
            } else if (input.charAt(i) == ')') {
                return new PathState(i, thisRoom);
            } else if (input.charAt(i) == '|') {
                thisRoom = currentRoom;
                distanceFromOrigin = currentDistance;
            } else {
                switch (input.charAt(i)) {
                    case 'N':
                        if (thisRoom.north == null) {
                            thisRoom.north = new Room(++distanceFromOrigin);
                            thisRoom.north.south = thisRoom;
                            allRooms.add(thisRoom.north);
                        }
                        thisRoom = thisRoom.north;
                        break;
                    case 'S':
                        if (thisRoom.south == null) {
                            thisRoom.south = new Room(++distanceFromOrigin);
                            thisRoom.south.north = thisRoom;
                            allRooms.add(thisRoom.south);
                        }
                        thisRoom = thisRoom.south;
                        break;
                    case 'E':
                        if (thisRoom.east == null) {
                            thisRoom.east = new Room(++distanceFromOrigin);
                            thisRoom.east.west = thisRoom;
                            allRooms.add(thisRoom.east);
                        }
                        thisRoom = thisRoom.east;
                        break;
                    case 'W':
                        if (thisRoom.west == null) {
                            thisRoom.west = new Room(++distanceFromOrigin);
                            thisRoom.west.east = thisRoom;
                            allRooms.add(thisRoom.west);
                        }
                        thisRoom = thisRoom.west;
                        break;
                }
            }
        }
        return new PathState(input.length(), thisRoom);
    }

}
