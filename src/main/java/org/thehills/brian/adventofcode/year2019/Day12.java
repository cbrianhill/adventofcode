package org.thehills.brian.adventofcode.year2019;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Sets;
import org.apache.commons.math3.util.ArithmeticUtils;

public class Day12
{
    public static void main(String args[]) throws IOException
    {
        InputStream is = null;
        try
        {
            is = new FileInputStream(args[0]);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            Pattern p = Pattern.compile("<x=([0-9-]+), y=([0-9-]+), z=([0-9-]+)>");
            Set<Moon> moons = new HashSet<>();
            while ((line = reader.readLine()) != null) {
                Matcher m = p.matcher(line);
                if (m.matches()) {
                    int x = Integer.parseInt(m.group(1));
                    int y = Integer.parseInt(m.group(2));
                    int z = Integer.parseInt(m.group(3));
                    moons.add(new Moon(x, y, z));
                }
            }
            Set<Set<Moon>> pairsOfMoons = Sets.combinations(moons, 2);
            long timeStepsCompleted = 0;
            long xPeriod = 0;
            long yPeriod = 0;
            long zPeriod = 0;
            while (xPeriod == 0 || yPeriod == 0 || zPeriod == 0)
            {
                // Apply gravity for all pairs of moons
                for (Set<Moon> pair : pairsOfMoons)
                {
                    Object thisPair[] = pair.toArray();
                    ((Moon)thisPair[0]).applyGravityFrom((Moon)thisPair[1]);
                    ((Moon)thisPair[1]).applyGravityFrom((Moon)thisPair[0]);
                }
                // Apply velocity for all moons
                for (Moon m : moons) {
                    m.applyVelocity();
                }
                int totalEnergy = 0;
                for (Moon m : moons) {
                    totalEnergy += m.getTotalEnergy();
                }

                int potentialEnergy = 0;
                for (Moon m : moons) {
                    potentialEnergy += m.getPotentialEnergy();
                }

                int kineticEnergy = 0;
                for (Moon m : moons) {
                    kineticEnergy += m.getKineticEnergy();
                }

                timeStepsCompleted++;

                long moonsAtXStart = moons.stream().filter(Moon::isAtXStart).count();
                if (xPeriod == 0 && moonsAtXStart == moons.size()) {
                    System.out.println("X period = " + timeStepsCompleted);
                    xPeriod = timeStepsCompleted;
                }
                long moonsAtYStart = moons.stream().filter(Moon::isAtYStart).count();
                if (yPeriod == 0 && moonsAtYStart == moons.size()) {
                    System.out.println("Y period = " + timeStepsCompleted);
                    yPeriod = timeStepsCompleted;
                }
                long moonsAtZStart = moons.stream().filter(Moon::isAtZStart).count();
                if (zPeriod == 0 && moonsAtZStart == moons.size()) {
                    System.out.println("Z period = " + timeStepsCompleted);
                    zPeriod = timeStepsCompleted;
                }


                if (timeStepsCompleted == 1000) {
                    System.out.println("Total energy after 1000 steps = " + totalEnergy);
                }

                if (timeStepsCompleted % 1000000 == 0) {
                    System.out.println("Completed " + timeStepsCompleted + " steps.");
                }

            }

            long repeat = ArithmeticUtils.lcm(xPeriod, yPeriod);
            repeat = ArithmeticUtils.lcm(repeat, zPeriod);
            System.out.println("Universe repeates at " + repeat);
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public static class Moon {

        public int xPosition;
        public int yPosition;
        public int zPosition;

        public int xVelocity = 0;
        public int yVelocity = 0;
        public int zVelocity = 0;

        public int xPosStart;
        public int yPosStart;
        public int zPosStart;
        public int xVelStart = 0;
        public int yVelStart = 0;
        public int zVelStart = 0;

        public Moon(int x, int y, int z) {
            xPosition = x;
            yPosition = y;
            zPosition = z;
            xPosStart = x;
            yPosStart = y;
            zPosStart = z;
        }

        public void applyGravityFrom(Moon other) {
            if (other.xPosition > xPosition) {
                ++xVelocity;
            } else if (other.xPosition < xPosition) {
                --xVelocity;
            }
            if (other.yPosition > yPosition) {
                ++yVelocity;
            } else if (other.yPosition < yPosition) {
                --yVelocity;
            }
            if (other.zPosition > zPosition) {
                ++zVelocity;
            } else if (other.zPosition < zPosition) {
                --zVelocity;
            }
        }

        public void applyVelocity() {
            xPosition += xVelocity;
            yPosition += yVelocity;
            zPosition += zVelocity;
        }

        public int getTotalEnergy() {
            return getPotentialEnergy() * getKineticEnergy();
        }

        public int getPotentialEnergy() {
            return Math.abs(xPosition) + Math.abs(yPosition) + Math.abs(zPosition);
        }

        public int getKineticEnergy() {
            return Math.abs(xVelocity) + Math.abs(yVelocity) + Math.abs(zVelocity);
        }

        public boolean isAtXStart() {
            return xPosition == xPosStart
                    && xVelocity == xVelStart;
        }

        public boolean isAtYStart() {
            return yPosition == yPosStart
                   && yVelocity == yVelStart;
        }
        public boolean isAtZStart() {
            return zPosition == zPosStart
                   && zVelocity == zVelStart;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Moon moon = (Moon) o;
            return xPosition == moon.xPosition &&
                   yPosition == moon.yPosition &&
                   zPosition == moon.zPosition &&
                   xVelocity == moon.xVelocity &&
                   yVelocity == moon.yVelocity &&
                   zVelocity == moon.zVelocity;
        }

        @Override
        public int hashCode()
        {
            return Objects.hash(xPosition, yPosition, zPosition, xVelocity, yVelocity, zVelocity);
        }
    }

}
