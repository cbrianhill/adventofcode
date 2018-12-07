package org.thehills.brian.adventofcode;

import com.sun.tools.javac.util.Pair;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class Day2Part2 {

    public static void main(String args[]) throws IOException {
        InputStream is = null;
        try {
            is = new FileInputStream(args[0]);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            ArrayList<String> lines = reader.lines().collect(Collectors.toCollection(ArrayList::new));

            for (int n = 0; n < lines.size() - 1; ++n) {
                for (int m = n + 1; m < lines.size(); ++m) {
                    Pair<Integer, String> diffsResult = countDiffs(lines.get(n), lines.get(m));
                    if (diffsResult.fst == 1) {
                        System.out.println(diffsResult.snd);
                    }
                }
            }

        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private static Pair<Integer, String> countDiffs(String a, String b) {
        int diffs = 0;
        StringBuilder sameString = new StringBuilder();
        for (int i = 0; i < a.length(); ++i) {
            if ( a.charAt(i) != b.charAt(i)) {
                diffs++;
            } else {
                sameString.append(a.charAt(i));
            }
        }
        return new Pair<>(diffs, sameString.toString());
    }
}
