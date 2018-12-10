package org.thehills.brian.adventofcode;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day9 {

    private static final Pattern linePattern = Pattern.compile("(\\d+) players; last marble is worth (\\d+) points");

    public static void main(String args[]) throws IOException {
        InputStream is = null;
        try {
            is = new FileInputStream(args[0]);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line = reader.readLine();
            System.out.println("Input: " + line);
            Matcher m = linePattern.matcher(line);
            m.find();
            int playerCount = Integer.parseInt(m.group(1));
            int lastMarblePoints = Integer.parseInt(m.group(2));
            List<Player> playerList = new ArrayList<>(playerCount);
            for (int i = 0; i < playerCount; ++i) {
                playerList.add(new Player(i + 1));
            }
            int nextMarble = 1;
            Marble currentMarble = new Marble(0);
            currentMarble.clockwise = currentMarble;
            currentMarble.counterclockwise = currentMarble;

            int currentPlayer = 0;
            for (int i = 1; currentMarble.marbleNumber < lastMarblePoints; ++i) {
                ++currentPlayer;
                if (currentPlayer > playerCount) { currentPlayer = currentPlayer % playerCount; }
                System.out.print("[" + currentPlayer + "] ");
                if (i % 23 != 0) {
                    Marble newCounter = currentMarble.clockwise;
                    Marble newClock = currentMarble.clockwise.clockwise;
                    Marble newCurrent = new Marble(i);
                    newCurrent.clockwise = newClock;
                    newClock.counterclockwise = newCurrent;
                    newCurrent.counterclockwise = newCounter;
                    newCounter.clockwise = newCurrent;
                    currentMarble = newCurrent;
                    System.out.println("places marble " + currentMarble + " between " + currentMarble.counterclockwise + " and " + currentMarble.clockwise);
                } else {
                    Player p = playerList.get(currentPlayer - 1);
                    p.score += i;
                    Marble marbleToRemove = currentMarble.counterclockwise.counterclockwise.counterclockwise.counterclockwise.counterclockwise.counterclockwise.counterclockwise;
                    marbleToRemove.clockwise.counterclockwise = marbleToRemove.counterclockwise;
                    marbleToRemove.counterclockwise.clockwise = marbleToRemove.clockwise;
                    p.score += marbleToRemove.marbleNumber;
                    currentMarble = marbleToRemove.clockwise;
                    System.out.println("keeps marble " + i + " and removes marble " + marbleToRemove.marbleNumber + ".  Score = " + p.score + " (" + p.playerNumber + ")");
                }
            }

            long highScore = 0;
            int highPlayerNumber = 0;
            for (int i = 0; i < playerList.size(); ++i) {
                Player p = playerList.get(i);
                if (p.score > highScore) {
                    highScore = p.score;
                    highPlayerNumber = p.playerNumber;
                }
            }

            System.out.println("High schore is " + highScore);

        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private static class Player {
        public int playerNumber;
        public long score = 0;

        public Player(int number) { this.playerNumber = number; }
    }

    private static class Marble {
        public int marbleNumber;
        public Marble counterclockwise;
        public Marble clockwise;

        public Marble (int number) { this.marbleNumber = number; }

        public String toString() {
            return "" + marbleNumber;
        }
    }
}
