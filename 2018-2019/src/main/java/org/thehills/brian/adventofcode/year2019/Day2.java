package org.thehills.brian.adventofcode.year2019;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Day2
{
    public static void main(String args[]) throws IOException {
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
            System.out.println("Original result = " + execute(originalCodes, 0, 12, 2));

            int desiredResult = 19690720;
            for (int noun = 0; noun <= 99; ++noun) {
                for (int verb = 0; verb <= 99; ++verb) {
                    if (execute(originalCodes, 0, noun, verb) == desiredResult) {
                        System.out.println("Answer = " + (100 * noun + verb));
                    }
                }
            }
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private static final int HALT = 99;
    private static final int ADD = 1;
    private static final int MULTIPLY = 2;

    public static int execute (List<Integer> originalCodes, int resultLocation, int noun, int verb) {
        List<Integer> codes = new ArrayList<>();
        codes.addAll(originalCodes);
        codes.set(1, noun);
        codes.set(2, verb);
       int currentPosition = 0;
       int opCode = codes.get(currentPosition);
       while (opCode != HALT) {
           if (opCode == ADD) {
               int addend1Location = codes.get(currentPosition + 1);
               int addend2Location = codes.get(currentPosition + 2);
               int sumLocation = codes.get(currentPosition + 3);
               codes.set(sumLocation, codes.get(addend1Location) + codes.get(addend2Location));
           } else if (opCode == MULTIPLY) {
               int multiplicand1Location = codes.get(currentPosition + 1);
               int multiplicand2Location = codes.get(currentPosition + 2);
               int productLocation = codes.get(currentPosition + 3);
               codes.set(productLocation, codes.get(multiplicand1Location) * codes.get(multiplicand2Location));
           } else {
               System.out.println("Invalid opcode at position " + currentPosition);
               break;
           }
           currentPosition += 4;
           opCode = codes.get(currentPosition);
       }
       return codes.get(resultLocation);
    }

}
