package org.thehills.brian.adventofcode.year2019;

import javax.swing.*;
import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.data.Row;
import de.erichseifert.gral.navigation.NavigationDirection;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.ui.InteractivePanel;
import org.apache.commons.math3.fraction.Fraction;

public class Day10 extends JFrame
{

    public Day10() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(800, 600);
    }

    public static void main(String args[]) throws IOException
    {
        InputStream is = null;
        try {
            is = new FileInputStream(args[0]);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            int lineIndex = -1;
            DataTable dataTable = new DataTable(Integer.class, Integer.class, Integer.class);
            while ((line = reader.readLine()) != null) {
                int y = ++lineIndex;
                for (int x = 0; x < line.length(); ++x) {
                    if (line.charAt(x) == '#') {
                        dataTable.add(x, y, 0);
                    }
                }
            }
            XYPlot plot = new XYPlot(dataTable);
            plot.getAxisRenderer(XYPlot.AXIS_Y).setShapeDirectionSwapped(true);
            Day10 program = new Day10();
            program.getContentPane().add(new InteractivePanel(plot));
            //program.setVisible(true);

            int maxAsteroidsVisible = 0;
            Row bestRow = null;
            TreeMap<Fraction, List<Row>> bestMap = null;
            for (int i = 0; i < dataTable.getRowCount(); ++i) {
                Row r = dataTable.getRow(i);
                Integer xCoordinate = (Integer) r.get(0);
                Integer yCoordinate = (Integer) r.get(1);
                TreeMap<Fraction, List<Row>> visibleAsteroids = findAsteroidsVisibleFrom(xCoordinate, yCoordinate, dataTable);
                int totalVisible = 0;
                for (Map.Entry<Fraction, List<Row>> e : visibleAsteroids.entrySet()) {
                    totalVisible += e.getValue().size();
                }
                dataTable.set(2, i, totalVisible);
                if (totalVisible > maxAsteroidsVisible) {
                    maxAsteroidsVisible = totalVisible;
                    bestRow = r;
                    bestMap = visibleAsteroids;
                }
            }

            System.out.println("Best location is " + bestRow.get(0) + "," + bestRow.get(1) + " with " + bestRow.get(2) + " asteroids visible.");

            // Part 2

            findAsteroidDestroyedAtOrdinal(200, (Integer) bestRow.get(0), (Integer) bestRow.get(1), bestMap);

        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private static TreeMap<Fraction, List<Row>> findAsteroidsVisibleFrom(int xCoordinate, int yCoordinate, DataTable dataTable) {
        TreeMap<Fraction, List<Row>> slopesSeen = new TreeMap<>();
        for (int i = 0; i < dataTable.getRowCount(); ++i) {
            Row r = dataTable.getRow(i);
            if ((Integer)r.get(0) == xCoordinate && (Integer) r.get(1) == yCoordinate) {
                continue;
            }
            Fraction slope = findSlopeBetween(xCoordinate, yCoordinate, r);
            if (slopesSeen.get(slope) == null) {
                ArrayList<Row> list = new ArrayList<>();
                list.add(r);
                slopesSeen.put(slope, list);
            } else {
                addIfRequired(xCoordinate, yCoordinate, slopesSeen.get(slope), r);
            }
        }
        return slopesSeen;
    }

    private static Fraction findSlopeBetween(int xCoordinate, int yCoordinate, Row r) {
        int yDistance = yCoordinate - (Integer) r.get(1);
        int xDistance = xCoordinate - (Integer) r.get(0);
        if ( xDistance == 0) {
            return new Fraction(Integer.MAX_VALUE, 1);
        }
        return new Fraction(yDistance, xDistance);
    }

    private static void addIfRequired(int xCoordinate, int yCoordinate, List<Row> list, Row newRow) {
        if (list.size() > 1) return;

        boolean positiveDirection = findDirection(xCoordinate, yCoordinate, newRow);
        boolean matchFound = false;
        for (int i = 0; i < list.size(); ++i)
        {
            boolean existingDirection = findDirection(xCoordinate, yCoordinate, list.get(i));
            if (positiveDirection == existingDirection)
            {
                matchFound = true;
                if (findDistance(xCoordinate, yCoordinate, newRow) <
                    findDistance(xCoordinate, yCoordinate, list.get(i)))
                {
                    list.set(i, newRow);
                }
            }
        }
        if (!matchFound)
        {
            list.add(newRow);
        }
    }

    private static int findDistance(int xCoordinate, int yCoordiante, Row r) {
        int dX = Math.abs(xCoordinate - (Integer) r.get(0));
        int dY = Math.abs(yCoordiante - (Integer) r.get(1));
        return dX + dY;
    }

    private static boolean findDirection(int xCoordinate, int yCoordinate, Row candidate) {
        boolean positiveDirection = false;
        if ((Integer) candidate.get(0) > xCoordinate ||
            ((Integer) candidate.get(0) == xCoordinate && (Integer) candidate.get(1) > yCoordinate)) {
            positiveDirection = true;
        }
        return positiveDirection;
    }

    private static void findAsteroidDestroyedAtOrdinal(int ordinal, int originX, int originY, Map<Fraction, List<Row>> map) {

        /*

            I'm not sure if it's a trick question, but with my input, there are more asteroids visible from the
            station than 200, so we don't need to worry about the giant rotating laser swinging around more than once.
            Maybe this won't work for other inputs.

         */
        int totalAsteroidsDestroyed = 0;
        List<Row> asteroidsDestoyed = new ArrayList<>();
        List<Row> startDirectionAsteroids = map.get(new Fraction(Integer.MAX_VALUE, 1));
        for (Row r : startDirectionAsteroids) {
            if (!findDirection(originX, originY, r)) {
                // There's an asteroid directly above.
                ++totalAsteroidsDestroyed;
                asteroidsDestoyed.add(r);
            }
        }
        for (Map.Entry<Fraction, List<Row>> e : map.entrySet()) {
            for (Row r : e.getValue()) {
                if (findDirection(originX, originY, r)) {
                    ++totalAsteroidsDestroyed;
                    asteroidsDestoyed.add(r);
                }
            }
        }
        for (Row r : startDirectionAsteroids) {
            if (findDirection(originX, originY, r)) {
                // There's an asteroid directly below.
                ++totalAsteroidsDestroyed;
                asteroidsDestoyed.add(r);
            }
        }
        for (Map.Entry<Fraction, List<Row>> e : map.entrySet()) {
            for (Row r : e.getValue()) {
                if (!findDirection(originX, originY, r)) {
                    ++totalAsteroidsDestroyed;
                    asteroidsDestoyed.add(r);
                }
            }
        }
        Row theOne = asteroidsDestoyed.get(ordinal);
        System.out.println(ordinal + "th asteroid destroyed = " + theOne.get(0) + "," + theOne.get(1));
    }

}
