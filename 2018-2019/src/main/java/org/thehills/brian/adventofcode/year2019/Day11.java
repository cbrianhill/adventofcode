package org.thehills.brian.adventofcode.year2019;

import javax.swing.*;
import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import com.google.common.collect.HashBasedTable;
import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.ui.InteractivePanel;

public class Day11 extends JFrame
{

    public Day11() {
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
            List<String> originalCodes = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                String[] splitLine = line.split(",");
                for (String val : splitLine) {
                    originalCodes.add(val);
                }
            }

            EmergencyHullPaintingRobot robot = new EmergencyHullPaintingRobot(HullColor.BLACK);
            execute(originalCodes, 0, robot::getNextInput, robot::acceptNextOutput);
            System.out.println("Squares painted at least once: " + robot.getSquaresPaintedAtLeastOnce());

            EmergencyHullPaintingRobot whiteRobot = new EmergencyHullPaintingRobot(HullColor.WHITE);
            execute(originalCodes, 0, whiteRobot::getNextInput, whiteRobot::acceptNextOutput);

            XYPlot plot = new XYPlot(whiteRobot.getHullMap());
            plot.getAxisRenderer(XYPlot.AXIS_Y).setShapeDirectionSwapped(true);
            Day11 program = new Day11();
            program.getContentPane().add(new InteractivePanel(plot));
            program.setVisible(true);

        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private static final BigInteger HALT = BigInteger.valueOf(99L);
    private static final BigInteger ADD = BigInteger.valueOf(1L);
    private static final BigInteger MULTIPLY = BigInteger.valueOf(2L);
    private static final BigInteger INPUT = BigInteger.valueOf(3L);
    private static final BigInteger OUTPUT = BigInteger.valueOf(4L);
    private static final BigInteger JUMP_IF_TRUE = BigInteger.valueOf(5L);
    private static final BigInteger JUMP_IF_FALSE = BigInteger.valueOf(6L);
    private static final BigInteger LESS_THAN = BigInteger.valueOf(7L);
    private static final BigInteger EQUALS = BigInteger.valueOf(8L);
    private static final BigInteger RELATIVE_BASE_OFFSET = BigInteger.valueOf(9L);

    private static final BigInteger POSITION_MODE = BigInteger.valueOf(0L);
    private static final BigInteger IMMEDIATE_MODE = BigInteger.valueOf(1L);
    private static final BigInteger RELATIVE_MODE = BigInteger.valueOf(2L);

    public static BigInteger execute (List<String> originalCodes,
                                      int resultLocation,
                                      Supplier<BigInteger> inputFunction,
                                      Consumer<BigInteger> outputFunction) {
        Map<BigInteger, BigInteger> codes = new LinkedHashMap<>();
        for (int i = 0; i < originalCodes.size(); ++i) {
            codes.put(BigInteger.valueOf(i), new BigInteger(originalCodes.get(i)));
        }
        BigInteger currentPosition = BigInteger.ZERO;
        BigInteger opCode = codes.get(currentPosition);
        BigInteger parameterModes[] = getParameterModes(opCode);
        BigInteger relativeOffset = BigInteger.ZERO;
        opCode = opCode.mod(BigInteger.valueOf(100L));
        while (!opCode.equals(HALT)) {
            if (opCode.equals(ADD)) {
                BigInteger addend1Location = codes.get(currentPosition.add(BigInteger.valueOf(1)));
                BigInteger addend2Location = codes.get(currentPosition.add(BigInteger.valueOf(2)));
                BigInteger sumLocation = codes.get(currentPosition.add(BigInteger.valueOf(3)));
                BigInteger addend1 = addend1Location;
                if (parameterModes[0].equals(POSITION_MODE)) addend1 = codes.getOrDefault(addend1Location, BigInteger.ZERO);
                if (parameterModes[0].equals(RELATIVE_MODE)) addend1 = codes.getOrDefault(relativeOffset.add(addend1Location), BigInteger.ZERO);
                BigInteger addend2 = addend2Location;
                if (parameterModes[1].equals(POSITION_MODE)) addend2 = codes.getOrDefault(addend2Location, BigInteger.ZERO);
                if (parameterModes[1].equals(RELATIVE_MODE)) addend2 = codes.getOrDefault(relativeOffset.add(addend2Location), BigInteger.ZERO);
                if (parameterModes[2].equals(RELATIVE_MODE)) sumLocation = relativeOffset.add(sumLocation);
                codes.put(sumLocation, addend1.add(addend2));
                currentPosition = currentPosition.add(BigInteger.valueOf(4));
            } else if (opCode.equals(MULTIPLY)) {
                BigInteger multiplicand1Location = codes.getOrDefault(currentPosition.add(BigInteger.valueOf(1)), BigInteger.ZERO);
                BigInteger multiplicand2Location = codes.getOrDefault(currentPosition.add(BigInteger.valueOf(2)), BigInteger.ZERO);
                BigInteger productLocation = codes.getOrDefault(currentPosition.add(BigInteger.valueOf(3)), BigInteger.ZERO);
                BigInteger multiplicand1 = multiplicand1Location;
                if (parameterModes[0].equals(POSITION_MODE)) multiplicand1 = codes.getOrDefault(multiplicand1Location, BigInteger.ZERO);
                if (parameterModes[0].equals(RELATIVE_MODE)) multiplicand1 = codes.getOrDefault(relativeOffset.add(multiplicand1Location), BigInteger.ZERO);
                BigInteger multiplicand2 = multiplicand2Location;
                if (parameterModes[1].equals(POSITION_MODE)) multiplicand2 = codes.getOrDefault(multiplicand2Location, BigInteger.ZERO);
                if (parameterModes[1].equals(RELATIVE_MODE)) multiplicand2 = codes.getOrDefault(relativeOffset.add(multiplicand2Location), BigInteger.ZERO);
                if (parameterModes[2].equals(RELATIVE_MODE)) productLocation = relativeOffset.add(productLocation);
                codes.put(productLocation, multiplicand1.multiply(multiplicand2));
                currentPosition = currentPosition.add(BigInteger.valueOf(4));
            } else if (opCode.equals(INPUT)) {
                BigInteger inputLocation = codes.getOrDefault(currentPosition.add(BigInteger.ONE), BigInteger.ZERO);
                if (parameterModes[0].equals(RELATIVE_MODE)) inputLocation = relativeOffset.add(inputLocation);
                codes.put(inputLocation, inputFunction.get());
                currentPosition = currentPosition.add(BigInteger.valueOf(2));
            } else if (opCode.equals(OUTPUT)) {
                BigInteger outputLocation = codes.getOrDefault(currentPosition.add(BigInteger.ONE), BigInteger.ZERO);
                BigInteger outputValue = outputLocation;
                if (parameterModes[0].equals(POSITION_MODE)) outputValue = codes.getOrDefault(outputLocation, BigInteger.ZERO);
                if (parameterModes[0].equals(RELATIVE_MODE)) outputValue = codes.getOrDefault(relativeOffset.add(outputLocation), BigInteger.ZERO);
                // System.out.println("OUTPUT: " + outputValue);
                outputFunction.accept(outputValue);
                currentPosition = currentPosition.add(BigInteger.valueOf(2));
            } else if (opCode.equals(JUMP_IF_TRUE)) {
                BigInteger parameterLocation = codes.getOrDefault(currentPosition.add(BigInteger.ONE), BigInteger.ZERO);
                BigInteger jumpSpotLocation = codes.getOrDefault(currentPosition.add(BigInteger.valueOf(2)), BigInteger.ZERO);
                BigInteger parameter = parameterLocation;
                BigInteger jumpSpot = jumpSpotLocation;
                if (parameterModes[0].equals(POSITION_MODE)) parameter = codes.getOrDefault(parameterLocation, BigInteger.ZERO);
                if (parameterModes[0].equals(RELATIVE_MODE)) parameter = codes.getOrDefault(relativeOffset.add(parameterLocation), BigInteger.ZERO);
                if (parameterModes[1].equals(POSITION_MODE)) jumpSpot = codes.getOrDefault(jumpSpotLocation, BigInteger.ZERO);
                if (parameterModes[1].equals(RELATIVE_MODE)) jumpSpot = codes.getOrDefault(relativeOffset.add(jumpSpotLocation), BigInteger.ZERO);
                if (!parameter.equals(BigInteger.ZERO))
                {
                    currentPosition = jumpSpot;
                }
                else
                {
                    currentPosition = currentPosition.add(BigInteger.valueOf(3));
                }
            } else if (opCode.equals(JUMP_IF_FALSE)) {
                BigInteger parameterLocation = codes.getOrDefault(currentPosition.add(BigInteger.ONE), BigInteger.ZERO);
                BigInteger jumpSpotLocation = codes.getOrDefault(currentPosition.add(BigInteger.valueOf(2)), BigInteger.ZERO);
                BigInteger parameter = parameterLocation;
                BigInteger jumpSpot = jumpSpotLocation;
                if (parameterModes[0].equals(POSITION_MODE)) parameter = codes.getOrDefault(parameterLocation, BigInteger.ZERO);
                if (parameterModes[0].equals(RELATIVE_MODE)) parameter = codes.getOrDefault(relativeOffset.add(parameterLocation), BigInteger.ZERO);
                if (parameterModes[1].equals(POSITION_MODE)) jumpSpot = codes.getOrDefault(jumpSpotLocation, BigInteger.ZERO);
                if (parameterModes[1].equals(RELATIVE_MODE)) jumpSpot = codes.getOrDefault(relativeOffset.add(jumpSpotLocation), BigInteger.ZERO);
                if (parameter.equals(BigInteger.ZERO))
                {
                    currentPosition = jumpSpot;
                }
                else
                {
                    currentPosition = currentPosition.add(BigInteger.valueOf(3));
                }
            } else if (opCode.equals(LESS_THAN)) {
                BigInteger parameter1Location = codes.getOrDefault(currentPosition.add(BigInteger.ONE), BigInteger.ZERO);
                BigInteger parameter2Location = codes.getOrDefault(currentPosition.add(BigInteger.valueOf(2)), BigInteger.ZERO);
                BigInteger parameter3 = codes.getOrDefault(currentPosition.add(BigInteger.valueOf(3)), BigInteger.ZERO);
                BigInteger parameter1 = parameter1Location;
                BigInteger parameter2 = parameter2Location;
                if (parameterModes[0].equals(POSITION_MODE)) parameter1 = codes.getOrDefault(parameter1Location, BigInteger.ZERO);
                if (parameterModes[0].equals(RELATIVE_MODE)) parameter1 = codes.getOrDefault(relativeOffset.add(parameter1Location), BigInteger.ZERO);
                if (parameterModes[1].equals(POSITION_MODE)) parameter2 = codes.getOrDefault(parameter2Location, BigInteger.ZERO);
                if (parameterModes[1].equals(RELATIVE_MODE)) parameter2 = codes.getOrDefault(relativeOffset.add(parameter2Location), BigInteger.ZERO);
                if (parameterModes[2].equals(RELATIVE_MODE)) parameter3 = relativeOffset.add(parameter3);
                if (parameter1.compareTo(parameter2) < 0)
                {
                    codes.put(parameter3, BigInteger.ONE);
                }
                else
                {
                    codes.put(parameter3, BigInteger.ZERO);
                }
                currentPosition = currentPosition.add(BigInteger.valueOf(4));
            } else if (opCode.equals(EQUALS))
            {
                BigInteger parameter1Location = codes.getOrDefault(currentPosition.add(BigInteger.ONE), BigInteger.ZERO);
                BigInteger parameter2Location = codes.getOrDefault(currentPosition.add(BigInteger.valueOf(2)), BigInteger.ZERO);
                BigInteger parameter3 = codes.getOrDefault(currentPosition.add(BigInteger.valueOf(3)), BigInteger.ZERO);
                BigInteger parameter1 = parameter1Location;
                BigInteger parameter2 = parameter2Location;
                if (parameterModes[0].equals(POSITION_MODE)) parameter1 = codes.getOrDefault(parameter1Location, BigInteger.ZERO);
                if (parameterModes[0].equals(RELATIVE_MODE)) parameter1 = codes.getOrDefault(relativeOffset.add(parameter1Location), BigInteger.ZERO);
                if (parameterModes[1].equals(POSITION_MODE)) parameter2 = codes.getOrDefault(parameter2Location, BigInteger.ZERO);
                if (parameterModes[1].equals(RELATIVE_MODE)) parameter2 = codes.getOrDefault(relativeOffset.add(parameter2Location), BigInteger.ZERO);
                if (parameterModes[2].equals(RELATIVE_MODE)) parameter3 = relativeOffset.add(parameter3);
                if (parameter1.equals(parameter2))
                {
                    codes.put(parameter3, BigInteger.ONE);
                }
                else
                {
                    codes.put(parameter3, BigInteger.ZERO);
                }
                currentPosition = currentPosition.add(BigInteger.valueOf(4));
            } else if (opCode.equals(RELATIVE_BASE_OFFSET)) {
                BigInteger parameterLocation = codes.getOrDefault(currentPosition.add(BigInteger.ONE), BigInteger.ZERO);
                BigInteger parameter = parameterLocation;
                if (parameterModes[0].equals(POSITION_MODE)) parameter = codes.getOrDefault(parameterLocation, BigInteger.ZERO);
                if (parameterModes[0].equals(RELATIVE_MODE)) parameter = codes.getOrDefault(relativeOffset.add(parameterLocation), BigInteger.ZERO);
                relativeOffset = relativeOffset.add(parameter);
                currentPosition = currentPosition.add(BigInteger.valueOf(2));
            } else {
                System.out.println("Invalid opcode at position " + currentPosition);
                break;
            }
            opCode = codes.get(currentPosition);
            parameterModes = getParameterModes(opCode);
            opCode = opCode.mod(BigInteger.valueOf(100));
        }
        return codes.getOrDefault(resultLocation, BigInteger.ZERO);
    }

    public static final BigInteger[] getParameterModes(BigInteger opCode) {
        BigInteger result[] = new BigInteger[3];
        result[0] = getDigitFromRight(2, opCode);
        result[1] = getDigitFromRight(3, opCode);
        result[2] = getDigitFromRight(4, opCode);
        return result;
    }

    public static BigInteger getDigitFromRight(int digit, BigInteger value) {
        BigInteger dividend = value;
        BigInteger remainder = BigInteger.ZERO;
        for (int i = 0; i <= digit; ++i) {
            remainder = dividend.mod(BigInteger.TEN);
            dividend = dividend.divide(BigInteger.TEN);
        }
        return remainder;
    }

    public static enum HullColor {
        BLACK, WHITE
    }

    public static enum OutputState {
        WAITING_FOR_COLOR, WAITING_FOR_TURN
    }

    public static enum Direction {
        NORTH, EAST, SOUTH, WEST
    }

    public static class EmergencyHullPaintingRobot {
        private HashBasedTable<BigInteger, BigInteger, HullColor> hullTable = HashBasedTable.create();
        private BigInteger robotX = BigInteger.ZERO;
        private BigInteger robotY = BigInteger.ZERO;
        private OutputState outputState = OutputState.WAITING_FOR_COLOR;
        private Direction direction = Direction.NORTH;

        public EmergencyHullPaintingRobot(HullColor startingColor) {
            if (startingColor == HullColor.WHITE) {
                hullTable.put(BigInteger.ZERO, BigInteger.ZERO, HullColor.WHITE);
            }
        }

        public BigInteger getNextInput() {
            return BigInteger.valueOf(getPaintColorAtCurrentLocation().ordinal());
        }

        public void acceptNextOutput(BigInteger output) {
            switch (outputState) {
                case WAITING_FOR_COLOR:
                    handleColorOutput(output);
                    outputState = OutputState.WAITING_FOR_TURN;
                    break;
                case WAITING_FOR_TURN:
                    handleTurnOutput(output);
                    outputState = OutputState.WAITING_FOR_COLOR;
                    break;
            }
        }

        public int getSquaresPaintedAtLeastOnce() {
            return hullTable.size();
        }

        public DataTable getHullMap() {
            DataTable dataTable = new DataTable(Integer.class, Integer.class);
            hullTable.cellSet().stream()
                     .filter(c -> c.getValue().equals(HullColor.WHITE))
                     .forEach(c -> {
                         dataTable.add(c.getColumnKey().intValue(), c.getRowKey().intValue());
                     });
            return dataTable;
        }

        protected HullColor getPaintColorAtCurrentLocation() {
            return hullTable.get(robotY, robotX) == null ? HullColor.BLACK : hullTable.get(robotY, robotX);
        }

        protected void handleColorOutput(BigInteger output) {
            hullTable.put(robotY, robotX, output.equals(BigInteger.ONE) ? HullColor.WHITE : HullColor.BLACK);
        }

        protected void handleTurnOutput(BigInteger output) {
            switch (direction) {
                case NORTH:
                    if (output.equals(BigInteger.ONE)) {
                        direction = Direction.EAST;
                    } else if (output.equals(BigInteger.ZERO)) {
                        direction = Direction.WEST;
                    }
                    moveForward();
                    break;
                case EAST:
                    if (output.equals(BigInteger.ONE)) {
                        direction = Direction.SOUTH;
                    } else if (output.equals(BigInteger.ZERO)) {
                        direction = Direction.NORTH;
                    }
                    moveForward();
                    break;
                case SOUTH:
                    if (output.equals(BigInteger.ONE)) {
                        direction = Direction.WEST;
                    } else if (output.equals(BigInteger.ZERO)) {
                        direction = Direction.EAST;
                    }
                    moveForward();
                    break;
                case WEST:
                    if (output.equals(BigInteger.ONE)) {
                        direction = Direction.NORTH;
                    } else if (output.equals(BigInteger.ZERO)) {
                        direction = Direction.SOUTH;
                    }
                    moveForward();
                    break;
            }
        }

        protected void moveForward() {
            switch (direction) {
                case NORTH:
                    robotY = robotY.subtract(BigInteger.ONE);
                    break;
                case EAST:
                    robotX = robotX.add(BigInteger.ONE);
                    break;
                case SOUTH:
                    robotY = robotY.add(BigInteger.ONE);
                    break;
                case WEST:
                    robotX = robotX.subtract(BigInteger.ONE);
                    break;
            }
        }
    }

}
