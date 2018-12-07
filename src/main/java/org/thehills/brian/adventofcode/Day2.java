package org.thehills.brian.adventofcode;

import com.sun.tools.javac.util.Pair;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Day2 {

    public static void main(String args[]) throws IOException {
        InputStream is = null;
        try {
            is = new FileInputStream(args[0]);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            int twos = 0, threes = 0;
            while ((line = reader.readLine()) != null) {
                Pair<Integer, Integer> twosAndThrees = getChecksum(line);
                twos += twosAndThrees.fst;
                threes += twosAndThrees.snd;
            }

            System.out.println("Checksum = " + (twos * threes));

        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private static Pair<Integer, Integer> getChecksum(String input) {
        HashMap<Character, Integer> characterFrequency = new HashMap<>();
        for (int i = 0; i < input.length(); ++i) {
            Character c = input.charAt(i);
            if (!characterFrequency.containsKey(c)) {
                characterFrequency.put(c, 1);
            } else {
                Integer frequency = characterFrequency.get(c);
                frequency++;
                characterFrequency.put(c, frequency);
            }
        }
        int twos = 0;
        int threes = 0;
        for (Map.Entry<Character, Integer> entry : characterFrequency.entrySet()) {
            if (entry.getValue().equals(2)) {
                twos++;
            } else if (entry.getValue().equals(3)) {
                threes++;
            }
        }
        return new Pair<>(twos > 0 ? 1 : 0, threes > 0 ? 1 : 0);
    }
}
