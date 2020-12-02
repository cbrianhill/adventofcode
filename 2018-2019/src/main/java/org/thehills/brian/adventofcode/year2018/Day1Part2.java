package org.thehills.brian.adventofcode.year2018;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;

public class Day1Part2 {
    public static void main(String args[]) throws IOException {
        FileInputStream is = null;
        try {
            is = new FileInputStream(args[0]);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            HashSet<Integer> seenFrequencies = new HashSet<>();
            Integer frequency = null;
            while(!seenFrequencies.contains(frequency)) {
                if (frequency != null) {
                    seenFrequencies.add(frequency);
                }
                if (!reader.ready()) {
                    System.out.println("Resetting buffer");
                    is = new FileInputStream(args[0]);
                    reader = new BufferedReader(new InputStreamReader(is));
                }
                frequency = nextFrequency(reader, frequency);
            }
            System.out.println("First duplicate frequency = " + frequency);

        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private static Integer nextFrequency(BufferedReader reader, Integer currentFrequency) throws IOException {
        Integer nextInteger = Integer.parseInt(reader.readLine());
        return (currentFrequency == null ? 0 : currentFrequency) + nextInteger;
    }
}
