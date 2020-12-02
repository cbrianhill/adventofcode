package org.thehills.brian.adventofcode.year2019;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Day5
{

    public static void main(String args[]) throws IOException
    {
        InputStream is = null;
        try {
            is = new FileInputStream(args[0]);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            List<Integer> originalCodes = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                String[] splitLine = line.split(",");
                for (String val : splitLine) {
                    originalCodes.add(Integer.parseInt(val));
                }
            }
            List<Integer> inputs = Collections.singletonList(Integer.parseInt(args[1]));
            System.out.println("Result = " + execute(originalCodes, 0, inputs));

        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private static final int HALT = 99;
    private static final int ADD = 1;
    private static final int MULTIPLY = 2;
    private static final int INPUT = 3;
    private static final int OUTPUT = 4;
    private static final int JUMP_IF_TRUE = 5;
    private static final int JUMP_IF_FALSE = 6;
    private static final int LESS_THAN = 7;
    private static final int EQUALS = 8;

    private static final int POSITION_MODE = 0;
    private static final int IMMEDIATE_MODE = 1;

    public static int execute (List<Integer> originalCodes,
                               int resultLocation,
                               List<Integer> inputs) {
        List<Integer> codes = new ArrayList<>();
        codes.addAll(originalCodes);
        int currentInput = 0;
        int currentPosition = 0;
        int opCode = codes.get(currentPosition);
        int parameterModes[] = getParameterModes(opCode);
        opCode %= 100;
        while (opCode != HALT) {
            if (opCode == ADD) {
                int addend1Location = codes.get(currentPosition + 1);
                int addend2Location = codes.get(currentPosition + 2);
                int sumLocation = codes.get(currentPosition + 3);
                int addend1 = addend1Location;
                if (parameterModes[0] == POSITION_MODE) addend1 = codes.get(addend1Location);
                int addend2 = addend2Location;
                if (parameterModes[1] == POSITION_MODE) addend2 = codes.get(addend2Location);
                codes.set(sumLocation, addend1 + addend2);
                currentPosition += 4;
            } else if (opCode == MULTIPLY) {
                int multiplicand1Location = codes.get(currentPosition + 1);
                int multiplicand2Location = codes.get(currentPosition + 2);
                int productLocation = codes.get(currentPosition + 3);
                int multiplicand1 = multiplicand1Location;
                if (parameterModes[0] == POSITION_MODE) multiplicand1 = codes.get(multiplicand1Location);
                int multiplicand2 = multiplicand2Location;
                if (parameterModes[1] == POSITION_MODE) multiplicand2 = codes.get(multiplicand2Location);
                codes.set(productLocation, multiplicand1 * multiplicand2);
                currentPosition += 4;
            } else if (opCode == INPUT) {
                int inputLocation = codes.get(currentPosition + 1);
                codes.set(inputLocation, inputs.get(currentInput++));
                currentPosition += 2;
            } else if (opCode == OUTPUT) {
                int outputLocation = codes.get(currentPosition + 1);
                int outputValue = outputLocation;
                if (parameterModes[0] == POSITION_MODE) outputValue = codes.get(outputLocation);
                System.out.println("OUTPUT: " + outputValue);
                currentPosition += 2;
            } else if (opCode == JUMP_IF_TRUE) {
                int parameterLocation = codes.get(currentPosition + 1);
                int jumpSpotLocation = codes.get(currentPosition + 2);
                int parameter = parameterLocation;
                int jumpSpot = jumpSpotLocation;
                if (parameterModes[0] == POSITION_MODE) parameter = codes.get(parameterLocation);
                if (parameterModes[1] == POSITION_MODE) jumpSpot = codes.get(jumpSpotLocation);
                if (parameter != 0)
                {
                    currentPosition = jumpSpot;
                }
                else
                {
                    currentPosition += 3;
                }
            } else if (opCode == JUMP_IF_FALSE) {
                int parameterLocation = codes.get(currentPosition + 1);
                int jumpSpotLocation = codes.get(currentPosition + 2);
                int parameter = parameterLocation;
                int jumpSpot = jumpSpotLocation;
                if (parameterModes[0] == POSITION_MODE) parameter = codes.get(parameterLocation);
                if (parameterModes[1] == POSITION_MODE) jumpSpot = codes.get(jumpSpotLocation);
                if (parameter == 0)
                {
                    currentPosition = jumpSpot;
                }
                else
                {
                    currentPosition += 3;
                }
            } else if (opCode == LESS_THAN) {
                int parameter1Location = codes.get(currentPosition + 1);
                int parameter2Location = codes.get(currentPosition + 2);
                int parameter3 = codes.get(currentPosition + 3);
                int parameter1 = parameter1Location;
                int parameter2 = parameter2Location;
                if (parameterModes[0] == POSITION_MODE) parameter1 = codes.get(parameter1Location);
                if (parameterModes[1] == POSITION_MODE) parameter2 = codes.get(parameter2Location);
                if (parameter1 < parameter2)
                {
                    codes.set(parameter3, 1);
                }
                else
                {
                    codes.set(parameter3, 0);
                }
                currentPosition += 4;
            } else if (opCode == EQUALS) {
                int parameter1Location = codes.get(currentPosition + 1);
                int parameter2Location = codes.get(currentPosition + 2);
                int parameter3 = codes.get(currentPosition + 3);
                int parameter1 = parameter1Location;
                int parameter2 = parameter2Location;
                if (parameterModes[0] == POSITION_MODE) parameter1 = codes.get(parameter1Location);
                if (parameterModes[1] == POSITION_MODE) parameter2 = codes.get(parameter2Location);
                if (parameter1 == parameter2)
                {
                    codes.set(parameter3, 1);
                }
                else
                {
                    codes.set(parameter3, 0);
                }
                currentPosition += 4;
            } else {
                System.out.println("Invalid opcode at position " + currentPosition);
                break;
            }
            opCode = codes.get(currentPosition);
            parameterModes = getParameterModes(opCode);
            opCode %= 100;
        }
        return codes.get(resultLocation);
    }

    public static final int[] getParameterModes(int opCode) {
        int result[] = new int[3];
        result[0] = Day4.getDigitFromRight(2, opCode);
        result[1] = Day4.getDigitFromRight(3, opCode);
        result[2] = Day4.getDigitFromRight(4, opCode);
        return result;
    }
}
