package org.thehills.brian.adventofcode;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import sun.reflect.generics.tree.TypeArgument;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Day15 {

    public static void main(String args[]) throws Exception {

        List<Square> elves = new ArrayList<>();
        List<Square> goblins = new ArrayList<>();
        List<Square> opens = new ArrayList<>();


        HashBasedTable<Integer, Integer,Square> gameBoard = loadGameBoard(args[0], 3);
        setupLists(elves, goblins, opens, gameBoard);
        int beginningElfCount = elves.size();

        System.out.println("### PART ONE ###");
        runGame(elves, goblins, opens, gameBoard);

        elves.clear();
        System.out.println("### PART TWO ###");
        for (int i = 4; elves.size() != beginningElfCount; ++i) {
            gameBoard = loadGameBoard(args[0], i);
            setupLists(elves, goblins, opens, gameBoard);
            System.out.println("Running game with attack power " + i);
            runGame(elves, goblins, opens, gameBoard);
        }
        System.out.println("Elves sustain no casualties with attack power " + elves.get(0).attackPower);

    }

    private static class Square {
        public UnitType unitType;
        int x;
        int y;
        public int attackPower;
        public int hitPoints = 200;
        public int turnsCompleted = 0;

        public Square(UnitType unitType, int x, int y, int attackStrength) {
            this.unitType = unitType;
            this.x = x;
            this.y = y;
            this.attackPower = attackStrength;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Square square = (Square) o;
            return x == square.x &&
                    y == square.y &&
                    attackPower == square.attackPower &&
                    hitPoints == square.hitPoints &&
                    unitType == square.unitType;
        }

        @Override
        public int hashCode() {
            return Objects.hash(unitType, x, y, attackPower, hitPoints);
        }
    }

    private static enum UnitType {
        GOBLIN,
        ELF,
        WALL,
        OPEN
    }

    private static void printBoard(HashBasedTable<Integer, Integer, Square> board) {
        for (Map.Entry<Integer, Map<Integer, Square>> row : board.rowMap().entrySet()) {
            StringBuilder hitPoints = new StringBuilder("   ");
            for (Map.Entry<Integer, Square> cell : row.getValue().entrySet()) {
                if (cell.getValue().unitType.equals(UnitType.GOBLIN)) {
                    System.out.print("G");
                    hitPoints.append("G(" + cell.getValue().hitPoints + ") ");
                } else if (cell.getValue().unitType.equals(UnitType.ELF)) {
                    System.out.print("E");
                    hitPoints.append("E(" + cell.getValue().hitPoints + ") ");
                } else if (cell.getValue().unitType.equals(UnitType.WALL)) {
                    System.out.print("#");
                } else if (cell.getValue().unitType.equals(UnitType.OPEN)) {
                    System.out.print(".");
                }
            }
            System.out.print(hitPoints.toString() + "\n");
        }
    }

    private static List<Square> findAdjacentSquaresTo(Square target, HashBasedTable<Integer, Integer, Square> board) {
        List<Square> adjacentSquares = new ArrayList<>();
        if (target.x < board.row(0).size()) {
            adjacentSquares.add(board.get(target.y, target.x + 1));
        }
        if (target.y < board.rowMap().size()) {
            adjacentSquares.add(board.get(target.y + 1, target.x));
        }
        if (target.y > 0) {
            adjacentSquares.add(board.get(target.y - 1, target.x));
        }
        if (target.x > 0) {
            adjacentSquares.add(board.get(target.y, target.x - 1));
        }
        return adjacentSquares;
    }

    private static List<Square> findAdjacentOpenSquaresTo(Square target, HashBasedTable<Integer, Integer, Square> board) {
        List<Square> adjacentSquares = findAdjacentSquaresTo(target, board);
        return adjacentSquares.stream().filter(s -> s.unitType == UnitType.OPEN).collect(Collectors.toList());
    }

    private static boolean isAdjacentTo(Square one, Square two) {
        return (Math.abs(one.x - two.x) == 1 && one.y == two.y)
                || (Math.abs(one.y - two.y) == 1 && one.x == two.x);
    }

    private static boolean isAdjacentTo(Square one, List<Square> any) {
        for (Square s : any) {
            if (isAdjacentTo(one, s)) {
                return true;
            }
        }
        return false;
    }

    private static List<Square> findAdjacentSquares(Square one, List<Square> any) {
        List<Square> adjacents = new ArrayList<>();
        for (Square s : any) {
            if (isAdjacentTo(one, s)) {
                adjacents.add(s);
            }
        }
        return adjacents;
    }

    private static List<Square> findSquaresInRangeOfTargets(List<Square> targets, HashBasedTable<Integer, Integer, Square> board) {
        List<Square> cellsInRangeOfTargets = new ArrayList<>();
        for (Square target : targets) {
            List<Square> cellsInRange = findAdjacentOpenSquaresTo(target, board);
            cellsInRangeOfTargets.addAll(cellsInRange);
        }
        return cellsInRangeOfTargets;
    }

    private static Integer findDistanceFrom(Square a, Square b, HashBasedTable<Integer, Integer, Square> board) {
        Set<Square> reachableSquares = new HashSet<>();
        reachableSquares.add(a);
        boolean foundReachable = true;
        while (foundReachable) {
            foundReachable = false;
            Set<Square> toAdd = new HashSet<>();
            for (Square s : reachableSquares) {
                List<Square> newSquares = findAdjacentOpenSquaresTo(s, board);
                toAdd.addAll(newSquares.stream().filter(sq -> !reachableSquares.contains(sq)).collect(Collectors.toList()));
            }
            if (toAdd.size() > 0) {
                foundReachable = true;
            }
            reachableSquares.addAll(toAdd);
        }

        HashSet<SquareDistance> visitedSquares = new HashSet<>();
        visitedSquares.add(new SquareDistance(a, 0));
        boolean foundSome = true;
        while (foundSome) {
            foundSome = false;
            Set<SquareDistance> toAdd = new HashSet<>();
            for (SquareDistance sd : visitedSquares) {
                Set<Square> toRemoveFromReachable = new HashSet<>();
                for (Square s : reachableSquares) {
                    if (isAdjacentTo(sd.square, s)) {
                        if (s.equals(b)) {
                            return sd.distance + 1;
                        }
                        foundSome = true;
                        toAdd.add(new SquareDistance(s, sd.distance + 1));
                        toRemoveFromReachable.add(s);
                    }
                }
                reachableSquares.removeAll(toRemoveFromReachable);
            }
            visitedSquares.addAll(toAdd);
        }
        return Integer.MAX_VALUE;
    }

    private static Square findFirstStepFrom(Square a, Square b, HashBasedTable<Integer, Integer, Square> board) {
        if (isAdjacentTo(a, b)) {
            return b;
        }
        Set<Square> reachableSquares = new HashSet<>();
        reachableSquares.add(b);
        boolean foundReachable = true;
        while (foundReachable) {
            foundReachable = false;
            Set<Square> toAdd = new HashSet<>();
            for (Square s : reachableSquares) {
                List<Square> newSquares = findAdjacentOpenSquaresTo(s, board);
                toAdd.addAll(newSquares.stream().filter(sq -> !reachableSquares.contains(sq)).collect(Collectors.toList()));
            }
            if (toAdd.size() > 0) {
                foundReachable = true;
            }
            reachableSquares.addAll(toAdd);
        }

        HashBasedTable<Integer, Integer, Integer> distanceTable = HashBasedTable.create();
        distanceTable.put(b.y, b.x, 0);
        boolean foundNewSquares = true;
        while (foundNewSquares) {
            foundNewSquares = false;
            HashSet<Square> toRemove = new HashSet<>();
            HashMap<Square, Integer> toAdd = new HashMap<>();
            for (Square s : reachableSquares) {
                for (Table.Cell<Integer, Integer, Integer> c : distanceTable.cellSet()) {
                    if (isAdjacentTo(s, board.get(c.getRowKey(), c.getColumnKey()))) {
                        foundNewSquares = true;
                        toAdd.put(s, c.getValue() + 1);
                        toRemove.add(s);
                    }
                }
            }
            reachableSquares.removeAll(toRemove);
            for(Map.Entry<Square, Integer> entry : toAdd.entrySet()) {
                distanceTable.put(entry.getKey().y, entry.getKey().x, entry.getValue());
            }
        }

        List<Square> possibleMoves = findAdjacentOpenSquaresTo(a, board);
        int minDistance = Integer.MAX_VALUE;
        List<Square> minSquares = new ArrayList<>();
        for (Square sq : possibleMoves) {
            if (distanceTable.get(sq.y, sq.x) != null && distanceTable.get(sq.y, sq.x) == minDistance) {
                minSquares.add(sq);
            } else if (distanceTable.get(sq.y, sq.x) != null && distanceTable.get(sq.y, sq.x) < minDistance) {
                minDistance = distanceTable.get(sq.y, sq.x);
                minSquares.clear();
                minSquares.add(sq);
            }
        }
        Collections.sort(minSquares, new ReadingOrderSorter());
        return minSquares.size() > 0? minSquares.get(0) : null;

    }

    private static int findManhattanDistance(Square a, Square b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }

    private static class SquareDistance {
        public Square square;
        public Integer distance;
        public SquareDistance(Square square, Integer distance) {
            this.square = square;
            this.distance = distance;
        }

    }

    private static class ReadingOrderSorter implements Comparator<Square> {

        @Override
        public int compare(Square o1, Square o2) {
            if (o1.y < o2.y) return -1;
            if (o1.y > o2.y) return 1;
            if (o1.x < o2.x) return -1;
            if (o1.x > o2.x) return 1;
            return 0;
        }
    }

    private static class TargetSelectionSorter implements Comparator<Square> {

        @Override
        public int compare(Square o1, Square o2) {
            if (o1.hitPoints < o2.hitPoints) return -1;
            if (o1.hitPoints > o2.hitPoints) return 1;
            if (o1.y < o2.y) return -1;
            if (o1.y > o2.y) return 1;
            if (o1.x < o2.x) return -1;
            if (o1.x > o2.x) return 1;
            return 0;
        }
    }

    private static void moveFrom(Square from, Square to, HashBasedTable<Integer, Integer, Square> board) {
        System.out.println(from.unitType + " at (" + from.x + "," + from.y + ") moving to (" + to.x + "," + to.y + ")");
        Square newOpenSquare = new Square(UnitType.OPEN, from.x, from.y, 0);
        from.x = to.x;
        from.y = to.y;
        board.put(newOpenSquare.y, newOpenSquare.x, newOpenSquare);
        board.put(from.y, from.x, from);
    }

    private static HashBasedTable<Integer, Integer, Square> loadGameBoard(String file, int attackStrength) throws Exception {
        InputStream is = null;
        try {
            is = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            HashBasedTable<Integer, Integer, Square> gameBoard = HashBasedTable.create();
            Integer rowIndex = -1;
            while ((line = reader.readLine()) != null) {
                rowIndex++;
                for (int i = 0; i < line.length(); ++i) {
                    char squareChar = line.charAt(i);
                    if (squareChar == '#') {
                        gameBoard.put(rowIndex, i, new Square(UnitType.WALL, i, rowIndex, 0));
                    } else if (squareChar == 'G') {
                        gameBoard.put(rowIndex, i, new Square(UnitType.GOBLIN, i, rowIndex, 3));
                    } else if (squareChar == 'E') {
                        gameBoard.put(rowIndex, i, new Square(UnitType.ELF, i, rowIndex, attackStrength));
                    } else if (squareChar == '.') {
                        gameBoard.put(rowIndex, i, new Square(UnitType.OPEN, i, rowIndex, 0));
                    }
                }
            }
            return gameBoard;
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private static void setupLists(List<Square> elves, List<Square> goblins, List<Square> opens, HashBasedTable<Integer, Integer, Square> gameBoard) {
        elves.clear();
        goblins.clear();
        opens.clear();
        gameBoard.cellSet().forEach(c -> {
            switch (c.getValue().unitType) {
                case ELF:
                    elves.add(c.getValue());
                    break;
                case GOBLIN:
                    goblins.add(c.getValue());
                    break;
                case OPEN:
                    opens.add(c.getValue());
                    break;
                default:
                    break;
            }
        });
    }

    private static void runGame(List<Square> elves, List<Square> goblins, List<Square> opens, HashBasedTable<Integer, Integer, Square> gameBoard) {
        ReadingOrderSorter sorter = new ReadingOrderSorter();
        printBoard(gameBoard);
        boolean combatOver = false;
        int currentTurnNumber = 0;

        while (!combatOver) {
            ++currentTurnNumber;
            System.out.println("Turn " + currentTurnNumber + " starting.");
            // Proceed in reading order
            for (Map.Entry<Integer, Map<Integer, Square>> row : gameBoard.rowMap().entrySet()) {
                for (Map.Entry<Integer, Square> cell : row.getValue().entrySet()) {
                    // Skip cells which don't move
                    Square square = cell.getValue();
                    if (square.unitType.equals(UnitType.WALL) || square.unitType.equals(UnitType.OPEN)) {
                        continue;
                    }
                    // Skip cells which have already moved
                    if (square.turnsCompleted >= currentTurnNumber) {
                        continue;
                    }
                    ++square.turnsCompleted;
                    // Only goblins and elves make it this far...
                    List<Square> targets = square.unitType.equals(UnitType.GOBLIN) ? elves : goblins;
                    if (targets.isEmpty()) {
                        combatOver = true;
                        break;
                    }
                    List<Square> cellsInRangeOfTargets = findSquaresInRangeOfTargets(targets, gameBoard);
                    List<Square> adjacentTargets = findAdjacentSquares(square, targets);
                    if (cellsInRangeOfTargets.isEmpty() && adjacentTargets.size() == 0) {
                        // end turn
                        continue;
                    }
                    // Only move if we are not already adjacent to a target.
                    if (adjacentTargets.size() == 0) {
                        List<Square> closestTargets = new ArrayList<>();
                        int minDistance = Integer.MAX_VALUE;
                        for (Square a : cellsInRangeOfTargets) {
                            int distance = findDistanceFrom(square, a, gameBoard);
                            if (distance == minDistance) {
                                closestTargets.add(a);
                            } else if (distance < minDistance) {
                                closestTargets.clear();
                                closestTargets.add(a);
                                minDistance = distance;
                            }
                        }
                        if (closestTargets.size() > 0) {
                            Collections.sort(closestTargets, sorter);
                            Iterator<Square> iter = closestTargets.iterator();
                            Square newLocation = null;
                            Square selectedDestination = null;
                            while(newLocation == null && iter.hasNext()) {
                                selectedDestination = iter.next();
                                newLocation = findFirstStepFrom(square, selectedDestination, gameBoard);
                            }
                            if (newLocation == null) {
                                System.out.println(square.unitType + " at (" + square.x + "," + square.y + ") cannot move toward a target.");
                            } else {
                                moveFrom(square, newLocation, gameBoard);
                            }
                        }
                        // Recalculate adjacent targets
                        adjacentTargets = targets.stream().filter(s -> isAdjacentTo(square, s)).collect(Collectors.toList());
                    }
                    // Now, attack if we are adjacent to a target.
                    if (adjacentTargets.size() > 0) {
                        Collections.sort(adjacentTargets, new TargetSelectionSorter());
                        Square target = adjacentTargets.get(0);
                        target.hitPoints = target.hitPoints - square.attackPower;
                        System.out.println(square.unitType + " at (" + square.x + "," + square.y + ") attacking " +
                                target.unitType + " at (" + target.x + "," + target.y + "), new HP: " + target.hitPoints);
                        if (target.hitPoints <= 0) {
                            gameBoard.put(target.y, target.x, new Square(UnitType.OPEN, target.x, target.y, 0));
                            opens.add(gameBoard.get(target.y, target.x));
                            if (target.unitType.equals(UnitType.ELF)) {
                                elves.remove(target);
                            } else if (target.unitType.equals(UnitType.GOBLIN)) {
                                goblins.remove(target);
                            }
                        }

                    }
                }
                if (combatOver) {
                    break;
                }
            }
            System.out.println("Turn " + currentTurnNumber + " complete.");
            printBoard(gameBoard);

        }
        int totalHitPoints = gameBoard.cellSet().stream()
                .map(Table.Cell::getValue)
                .filter(s -> s.unitType.equals(UnitType.GOBLIN) || s.unitType.equals(UnitType.ELF))
                .map(s -> s.hitPoints).reduce(0, (a, b) -> a + b);
        System.out.println(--currentTurnNumber + " turns were completed.  " + totalHitPoints + " total HP remaining.  Answer = " + (totalHitPoints * currentTurnNumber));
    }

}
