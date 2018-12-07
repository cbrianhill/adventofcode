package org.thehills.brian.adventofcode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Day1 {

    public static void main(String args[]) throws IOException {
        InputStream is = null;
        try {
            is = new FileInputStream(args[0]);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            Integer answer = reader.lines().map(Integer::parseInt).reduce(0, (a, b) -> a + b);
            System.out.println("Final frequency: " + answer);
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }
}
