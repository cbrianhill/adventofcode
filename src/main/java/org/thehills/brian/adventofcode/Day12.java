package org.thehills.brian.adventofcode;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day12 {

    private static Pattern firstLinePattern = Pattern.compile("initial state: (.+)");
    private static Pattern secondPattern = Pattern.compile("(.+) => (.)");

    public static void main(String args[]) throws IOException {
        InputStream is = null;
        try {
            is = new FileInputStream(args[0]);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String firstLine = reader.readLine();
            Matcher firstMatcher = firstLinePattern.matcher(firstLine);
            HashBasedTable<Integer, Integer, Boolean> table = HashBasedTable.create();
            int leftMost = -5;
            int rightMost = 0;
            if (firstMatcher.matches()) {
                String initialPattern = firstMatcher.group(1);
                for (int i = 0; i < initialPattern.length() + 5; ++i) {
                    if (i < 0) {
                        table.put(0, i, false);
                    } else if (i >= initialPattern.length()) {
                        table.put(0, i, false);
                    } else if (initialPattern.charAt(i) == '#') {
                        table.put(0, i, true);
                    } else {
                        table.put(0, i, false);
                    }
                }
                rightMost = initialPattern.length() + 4;
            }
            String otherLine;
            List<Instruction> instructions = new ArrayList<>();
            while ((otherLine = reader.readLine()) != null) {
                Matcher m = secondPattern.matcher(otherLine);
                if (m.matches()) {
                    String currentPattern = m.group(1);
                    String newValue = m.group(2);
                    Instruction i = new Instruction(currentPattern, newValue);
                    instructions.add(i);
                }
            }

            // Create the next generation
            for (int g = 1; g < 1001; ++g) {
                for (int i = leftMost + 2; i <= rightMost - 2; ++i) {
                    Boolean[] currentPattern = new Boolean[5];
                    Boolean newValue = table.get(g - 1, i);
                    for (int j = 0; j < 5; ++j) {
                        Boolean currentValue = table.get(g - 1, i - 2 + j);
                        if (currentValue == null) {
                            currentValue = false;
                        }
                        currentPattern[j] = currentValue;
                    }
                    for (Instruction ins : instructions) {
                        if (Arrays.equals(currentPattern, ins.currentPattern)) {
                            newValue = ins.newValue;
                            table.put(g, i, ins.newValue);
                            if (i > rightMost - 5 && newValue) {
                                rightMost = i + 5;
                            } else if (i < leftMost + 5 && newValue) {
                                leftMost = i - 5;
                            }
                            break;
                        }
                    }
                }
            }

            int previousGenSum = 0;
            for (int gn = 0; gn < 1001; ++gn) {
                int sumPlants = 0;
                System.out.println("Generation " + gn + ": L:" + leftMost + ", R:" + rightMost);
                for (int k = leftMost; k <= rightMost; ++k) {
                    Boolean value = table.get(gn, k);
                    if (value == null) {
                        System.out.print(".");
                    } else if (!value) {
                        System.out.print(".");
                    } else {
                        System.out.print("#");
                    }
                    if (value != null && value) {
                        sumPlants += k;
                    }
                }
                System.out.println("");
                System.out.println("Total plants = " + sumPlants + ", delta = " + (sumPlants - previousGenSum));
                previousGenSum = sumPlants;
            }

            long remainingGenerations = 50000000000L - 1000;
            long totalPlants = previousGenSum + (46 * remainingGenerations);
            System.out.println("At 50 billion generations, sum = " + totalPlants);

        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public static class Instruction {

        public Instruction(String currentPattern, String instruction) {
            for (int i = 0; i < 5; ++i) {
                if (currentPattern.charAt(i) == '#') {
                    this.currentPattern[i] = true;
                } else {
                    this.currentPattern[i] = false;
                }
            }
            if ("#".equals(instruction)) {
                newValue = true;
            } else {
                newValue = false;
            }
        }
        public Boolean[] currentPattern = new Boolean[5];
        public Boolean newValue;
    }
}
