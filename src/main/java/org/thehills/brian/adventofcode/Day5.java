package org.thehills.brian.adventofcode;

import java.io.*;

public class Day5 {

    public static void main(String args[]) throws IOException {
        InputStream is = null;
        try {
            is = new FileInputStream(args[0]);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String input = reader.readLine();
            String newInput = react(input);

            System.out.println("Remaining units = " + newInput.length());

            int shortenedReactedLength = Integer.MAX_VALUE;

            for (char k = 'A'; k < 'A' + 26; ++k) {
                String shortenedInput = removeAll(input, k, (char)(k + 32));
                String reactedInput = react(shortenedInput);
                if (reactedInput.length() < shortenedReactedLength) {
                    shortenedReactedLength = reactedInput.length();
                }
            }

            System.out.println("Shortest reacted string = " + shortenedReactedLength);

        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private static String removeAll(String input, char x, char y) {
        for (int i = 0; i < input.length(); ++i) {
            if (input.charAt(i) == x || input.charAt(i) == y) {
                if (i == 0) {
                    input = input.substring(1);
                } else if (i == input.length() - 1) {
                    input = input.substring(0, input.length() - 1);
                } else {
                    input = input.substring(0, i) + input.substring(i + 1);
                }
                i--;
            }
        }
        return input;
    }

    private static String react(String input) {
        String newInput = input;
        do {
            input = newInput;
            newInput = removeOne(input);
        } while (newInput != input);
        return newInput;
    }

    public static String removeOne(String input) {
        for (int i = 0; i < input.length() - 1; ++i) {
            char first = input.charAt(i);
            char second = input.charAt(i + 1);
            if (first - second == 32 || first - second == -32) {
                if (i >= input.length() - 2) {
                    return input.substring(0, i);
                } else if (i == 0) {
                    return input.substring(2);
                }
                return input.substring(0, i) + input.substring(i + 2);
            }
        }
        return input;
    }
}
