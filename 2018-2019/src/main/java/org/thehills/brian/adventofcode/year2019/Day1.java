package org.thehills.brian.adventofcode.year2019;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

public class Day1
{

    public static void main(String args[]) throws IOException {
        InputStream is = null;
        try {
            is = new FileInputStream(args[0]);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            List<Integer> moduleWeights = reader.lines().map(Integer::parseInt).collect(Collectors.toList());
            Integer moduleFuel = moduleWeights.stream()
                                   .map(Day1::calculateFuelForModuleWithMass)
                                   .reduce(0, (a, b) -> a + b);
            System.out.println("Fuel required for modules alone: " + moduleFuel);
            Integer totalFuel = moduleWeights.stream()
                    .map(Day1::calculateFuelForModuleWithMassIncludingFuelMass)
                    .reduce(0, (a, b) -> a + b);
            System.out.println("Total fuel required: " + totalFuel);
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public static int calculateFuelForModuleWithMass(int mass) {
        return mass / 3 - 2;
    }

    public static int calculateFuelForModuleWithMassIncludingFuelMass(int mass) {
        int moduleFuel = calculateFuelForModuleWithMass(mass);
        int totalFuel = moduleFuel;
        int fuelFuel = calculateFuelForModuleWithMass(moduleFuel);
        if (fuelFuel > 0)
        {
            totalFuel = moduleFuel + fuelFuel;
        }
        while (fuelFuel > 0) {
            fuelFuel = calculateFuelForModuleWithMass(fuelFuel);
            if (fuelFuel > 0) {
                totalFuel += fuelFuel;
            }
        }
        return totalFuel;
    }

}
