package org.thehills.brian.adventofcode.year2018;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day3 {

    public static void main(String args[]) throws IOException {
        InputStream is = null;
        try {
            is = new FileInputStream(args[0]);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            HashBasedTable<Integer, Integer, Integer> bigFabric = HashBasedTable.create();
            String line;
            List<String> uniques = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                addClaim(line, bigFabric, uniques);
            }
            Long multipleClaims = bigFabric.cellSet().stream()
                    .map(Table.Cell::getValue)
                    .filter(i -> i > 1)
                    .collect(Collectors.counting());

            System.out.println("Multiple claims = " + multipleClaims);

            uniques.stream().filter(unique -> checkUnique(unique, bigFabric)).forEach(System.out::println);

        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private static Pattern inputPattern = Pattern.compile("#(\\d+) @ (\\d+),(\\d+): (\\d+)x(\\d+)");

    private static void addClaim(String line, HashBasedTable<Integer, Integer, Integer> fabric, List<String> uniques) {
        Matcher matcher = inputPattern.matcher(line);
        if (!matcher.find()) {
            System.out.println("NO MATCH!!!");
            return;
        }

        int id = Integer.parseInt(matcher.group(1));
        int startX = Integer.parseInt(matcher.group(2));
        int startY = Integer.parseInt(matcher.group(3));
        int width = Integer.parseInt(matcher.group(4));
        int height = Integer.parseInt(matcher.group(5));


        boolean firstClaim = true;

        for (int i = startX; i < startX + width; ++i) {
            for (int j = startY; j < startY + height; ++j) {
                firstClaim = addClaim(i, j, fabric) && firstClaim;
            }
        }

        if (firstClaim) uniques.add(line);

    }

    private static boolean addClaim(int x, int y, HashBasedTable<Integer, Integer, Integer> fabric) {
        Integer currentClaims = fabric.get(x, y);
        if (currentClaims == null) {
            fabric.put(x, y, 1);
            return true;
        } else {
            fabric.put(x, y, ++currentClaims);
            return false;
        }
    }


    private static boolean checkUnique(String line, HashBasedTable<Integer, Integer, Integer> fabric) {
        Matcher matcher = inputPattern.matcher(line);
        if (!matcher.find()) {
            System.out.println("NO MATCH!!!");
            return false;
        }

        int id = Integer.parseInt(matcher.group(1));
        int startX = Integer.parseInt(matcher.group(2));
        int startY = Integer.parseInt(matcher.group(3));
        int width = Integer.parseInt(matcher.group(4));
        int height = Integer.parseInt(matcher.group(5));

        for (int i = startX; i < startX + width; ++i) {
            for (int j = startY; j < startY + height; ++j) {
                if (fabric.get(i, j) != 1) {
                    return false;
                }
            }
        }

        return true;

    }
}
