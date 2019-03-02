package org.thehills.brian.adventofcode;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day24 {

    public static void main(String args[]) throws IOException {
        InputStream is = null;
        try {
            is = new FileInputStream(args[0]);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            PriorityQueue<Group> immuneSystemGroups = new PriorityQueue<>(new EffectivePowerComparator());
            PriorityQueue<Group> infectionGroups = new PriorityQueue<>(new EffectivePowerComparator());
            PriorityQueue<Group> currentList = null;
            String currentUnitType = null;
            while ((line = reader.readLine()) != null) {
                if (line.equals("Immune System:")) {
                    currentList = immuneSystemGroups;
                    currentUnitType = "immune";
                } else if (line.equals("Infection:")) {
                    currentList = infectionGroups;
                    currentUnitType = "infection";
                } else if (!line.isEmpty()) {
                    currentList.add(new Group(line, currentUnitType, currentList.size() + 1));
                }
            }

            List<Group> allGroups = new ArrayList<>();
            allGroups.addAll(immuneSystemGroups);
            allGroups.addAll(infectionGroups);

            int currentRound = 0;

            while (shouldContinue(allGroups)) {
                PriorityQueue<FightingPair> pairs = selectTargets(immuneSystemGroups, infectionGroups, 0);
                doAttacks(pairs, ++currentRound, 0);
            }
            int remainingUnits = allGroups.stream()
                    .map(g -> g.units)
                    .reduce(0, (a, b) -> a + b);
            System.out.println("There are " + remainingUnits + " units left.");

            // Part two
            for (Group g : allGroups) {
                g.reset(0);
            }

            int boost = 0;
            int step = 100;
            int upperBound = 5000;
            int lowerBound = 0;
            do {
                for (Group g : allGroups) {
                    if (g.unitType.equals("immune")) {
                        g.reset(boost);
                    } else {
                        g.reset(0);
                    }
                }
                System.out.println("Trying boost = " + boost + ", step = " + step);
                boolean someoneDied = true;
                while (someoneDied && shouldContinue(allGroups)) {
                    PriorityQueue<FightingPair> pairs = selectTargets(immuneSystemGroups, infectionGroups, boost);
                    someoneDied = doAttacks(pairs, ++currentRound, boost);
                }
                if (someoneDied && findWinner(allGroups).equals("immune")) {
                    if (boost < upperBound) {
                        upperBound = boost;
                    }
                    if (lowerBound == upperBound - 1) {
                        remainingUnits = allGroups.stream()
                                .map(g -> g.units)
                                .reduce(0, (a, b) -> a + b);
                        System.out.println("There are " + remainingUnits + " units left.");
                        break;
                    }
                    boost = boost - (upperBound - lowerBound) / 2;
                } else if (someoneDied && findWinner(allGroups).equals("infection")) {
                    if (boost > lowerBound) {
                        lowerBound = boost;
                    }
                    if (lowerBound + 1 == upperBound) {
                        ++boost;
                    } else {
                        boost = boost + (upperBound - lowerBound) / 2;
                    }
                } else {
                    // someone didn't die
                    if (boost > lowerBound) {
                        lowerBound = boost;
                    }
                    ++boost;
                }
            } while (true);

        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private static final PriorityQueue<FightingPair> selectTargets(Collection<Group> immuneGroups, Collection<Group> infectionGroups, int boost) {
        PriorityQueue<Group> allGroups = new PriorityQueue<>(new EffectivePowerComparator());
        List<Group> potentialDefenders = new ArrayList<>();
        for (Group g : immuneGroups) {
            if (g.units > 0) {
                allGroups.add(g);
                potentialDefenders.add(g);
            }
        }
        for (Group g : infectionGroups) {
            if (g.units > 0) {
                allGroups.add(g);
                potentialDefenders.add(g);
            }
        }
        PriorityQueue<FightingPair> pairs = new PriorityQueue<>(new InitiativeComparator());
        for (Group g : allGroups) {
            g.attacking = null;
            g.attackedBy = null;
        }
        while (!allGroups.isEmpty()) {
            Group attacker = allGroups.poll();
            Group defender = selectTarget(attacker, potentialDefenders, boost);
            if (defender != null) {
                defender.attackedBy = attacker;
                attacker.attacking = defender;
                pairs.add(new FightingPair(attacker, defender));
            }
        }
        return pairs;
    }

    private static final Group selectTarget(Group attacker, Iterable<Group> defenders, int boost) {
        PriorityQueue<Group> bestDefenders = new PriorityQueue<>(new EffectivePowerComparator());
        int bestDamage = Integer.MIN_VALUE;
        for (Group defender : defenders) {
            if (defender.attackedBy != null || defender.unitType.equals(attacker.unitType)) {
                continue;
            }
            int damageDone = getDamageDoneBy(attacker, defender, boost);
            if (damageDone > bestDamage) {
                bestDamage = damageDone;
                bestDefenders.clear();
                bestDefenders.add(defender);
            } else if (damageDone == bestDamage) {
                bestDefenders.add(defender);
            }
        }
        if (bestDamage == 0) {
            return null;
        }
        return bestDefenders.poll();
    }

    private static final int getDamageDoneBy(Group attacker, Group defender, int boost) {
        if (defender.immunities.contains(attacker.attackType)) {
            return 0;
        }
        if (defender.weaknesses.contains(attacker.attackType)) {
            if (attacker.unitType.equals("immune")) {
                return 2 * (attacker.units) * (attacker.attackDamage + boost);
            } else {
                return 2 * attacker.units * attacker.attackDamage;
            }
        }
        if (attacker.unitType.equals("immune")) {
            return attacker.units * (attacker.attackDamage + boost);
        } else {
            return attacker.units * attacker.attackDamage;
        }
    }

    private static final Pattern groupPattern = Pattern.compile("(\\d+) units each with (\\d+) hit points (\\(.+\\) )?with an attack that does (\\d+) (\\w+) damage at initiative (\\d+)");
    private static final Pattern immuneToPattern = Pattern.compile(".*\\(.*immune to ([^;]+);?.*\\).*");
    private static final Pattern weakToPattern = Pattern.compile(".*\\(.*weak to ([^;]+);?.*\\).*");

    private static class Group {
        public int units;
        public int originalUnits;
        public int hitPoints;
        public int attackDamage;
        public String attackType;
        public int initiative;
        public List<String> weaknesses = new ArrayList<>();
        public List<String> immunities = new ArrayList<>();
        public Group attacking;
        public Group attackedBy;
        public String unitType;
        public int index;
        public int boost;

        public Group(String inputLine, String unitType, int groupIndex) {
            this.unitType = unitType;
            this.index = groupIndex;
            Matcher m = groupPattern.matcher(inputLine);
            if (m.matches()) {
                units = Integer.parseInt(m.group(1));
                originalUnits = units;
                hitPoints = Integer.parseInt(m.group(2));
                attackDamage = Integer.parseInt(m.group(4));
                attackType = m.group(5);
                initiative = Integer.parseInt(m.group(6));
                String qualities = m.group(3);
                if (qualities != null && !qualities.isEmpty()) {
                    Matcher immuneMatcher = immuneToPattern.matcher(qualities);
                    if (immuneMatcher.matches()) {
                        String[] immunitiesString = immuneMatcher.group(1).split(", ");
                        immunities.addAll(Arrays.asList(immunitiesString));
                    }
                    Matcher weaknessesMatcher = weakToPattern.matcher(qualities);
                    if (weaknessesMatcher.matches()) {
                        String[] weaknessesString = weaknessesMatcher.group(1).split(", ");
                        weaknesses.addAll(Arrays.asList(weaknessesString));
                    }
                }
            }
        }

        public int getEffectivePower() {
            return units * (attackDamage + boost);
        }

        public void reset(int boost) {
            units = originalUnits;
            this.boost = boost;
        }

    }

    public static class EffectivePowerComparator implements Comparator<Group> {

        @Override
        public int compare(Group o1, Group o2) {
            if (o1.getEffectivePower() < o2.getEffectivePower()) {
                return 1;
            } else if (o1.getEffectivePower() > o2.getEffectivePower()) {
                return -1;
            } else {
                // Equal effective power, the greater initiative wins
                if (o1.initiative < o2.initiative) {
                    return 1;
                } else if (o1.initiative > o2.initiative) {
                    return -1;
                } else {
                    return 0;
                }
            }
        }
    }

    private static class FightingPair {
        Group attacker;
        Group defender;
        public FightingPair(Group attacker, Group defender) {
            this.attacker = attacker;
            this.defender = defender;
        }
    }

    public static class InitiativeComparator implements Comparator<FightingPair> {

        @Override
        public int compare(FightingPair o1, FightingPair o2) {
            if (o1.attacker.initiative < o2.attacker.initiative) {
                return 1;
            } else if (o1.attacker.initiative > o2.attacker.initiative) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    public static boolean doAttacks(PriorityQueue<FightingPair> pairs, int currentRound, int boost) {
        // System.out.println("Round " + currentRound);
        boolean someoneDied = false;
        while (!pairs.isEmpty()) {
            FightingPair pair = pairs.poll();
            int baseDamage = pair.attacker.attackDamage * pair.attacker.units;
            int actualDamage = baseDamage;
            if (pair.attacker.unitType.equals("immune")) {
                actualDamage += (boost * pair.attacker.units);
            }
            if (pair.defender.weaknesses.contains(pair.attacker.attackType)) {
                actualDamage = actualDamage * 2;
            } else if (pair.defender.immunities.contains(pair.attacker.attackType)) {
                actualDamage = 0;
            }
            int unitsDestroyed = actualDamage / pair.defender.hitPoints;
            if (unitsDestroyed > pair.defender.units) {
                unitsDestroyed = pair.defender.units;
            }
            // System.out.println("Group " + pair.attacker.unitType + pair.attacker.index + " (" + pair.attacker.initiative + ")" + " does " + actualDamage + " to group " + pair.defender.unitType + pair.defender.index + ", killing " + unitsDestroyed + " units.");
            pair.defender.units -= unitsDestroyed;
            if (unitsDestroyed > 0) {
                someoneDied = true;
            }
        }
        return someoneDied;
    }

    public static boolean shouldContinue(Collection<Group> groups) {
        int immuneGroupsAlive = 0;
        int infectionGroupsAlive = 0;
        for (Group g : groups) {
            if (g.unitType.equals("immune") && g.units > 0) {
                immuneGroupsAlive++;
            } else if (g.unitType.equals("infection") && g.units > 0) {
                infectionGroupsAlive++;
            }
        }
        if (infectionGroupsAlive == 0) {
            System.out.println("Winner = immune");
        }
        if (immuneGroupsAlive == 0) {
            System.out.println("Winner = infection");
        }
        return immuneGroupsAlive > 0 && infectionGroupsAlive > 0;
    }

    public static String findWinner(Collection<Group> groups) {
        for (Group g : groups) {
            if (g.units > 0) {
                return g.unitType;
            }
        }
        return "alldead";
    }

}
