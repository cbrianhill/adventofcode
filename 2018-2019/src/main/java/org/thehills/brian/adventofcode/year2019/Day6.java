package org.thehills.brian.adventofcode.year2019;

import java.io.*;
import java.util.*;

public class Day6
{
    public static void main(String args[]) throws IOException
    {
        InputStream is = null;
        try {
            is = new FileInputStream(args[0]);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            HashMap<String, Object> objects = new HashMap<>();
            while ((line = reader.readLine()) != null) {
                String[] splitLine = line.split("\\)");
                String orbiterName = splitLine[1];
                String orbiteeName = splitLine[0];
                Object orbiter = objects.get(orbiterName);
                if (orbiter == null) {
                    orbiter = new Object(orbiterName);
                    objects.put(orbiterName, orbiter);
                }
                Object orbitee = objects.get(orbiteeName);
                if (orbitee == null) {
                    orbitee = new Object(orbiteeName);
                    objects.put(orbiteeName, orbitee);
                }
                orbiter.orbits = Optional.of(orbitee);
            }

            int totalOrbits = 0;
            for (Object o : objects.values()) {
                Object i = o;
                while (i.orbits.isPresent()) {
                    i = i.orbits.get();
                    ++totalOrbits;
                }
            }

            System.out.println("There are " + totalOrbits + " total orbits.");

            Object you = objects.get("YOU");
            Object santa = objects.get("SAN");

            List<Object> youTraversal = new LinkedList<>();
            Object currentObject = you;
            while (currentObject.orbits.isPresent()) {
                currentObject = currentObject.orbits.get();
                youTraversal.add(currentObject);
            }

            List<Object> sanTraversal = new LinkedList<>();
            currentObject = santa;
            while (currentObject.orbits.isPresent()) {
                currentObject = currentObject.orbits.get();
                sanTraversal.add(currentObject);
            }

            Object commonAncestor = null;
            for (Object o : youTraversal) {
                if (sanTraversal.contains(o)) {
                    commonAncestor = o;
                    break;
                }
            }

            int firstPartLength = youTraversal.indexOf(commonAncestor);
            int secondPartLength = sanTraversal.indexOf(commonAncestor);
            System.out.println("Orbital transfers needed = " + (firstPartLength + secondPartLength));

        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public static class Object {
        public Optional<Object> orbits = Optional.empty();
        public String name;

        public Object(String name) {
            this.name = name;
        }
    }
}
