package org.thehills.brian.adventofcode.year2019;

import java.util.HashSet;
import java.util.Set;

public class Day4
{
    public static void main(String args[]) {
        int begin = Integer.parseInt(args[0]);
        int end = Integer.parseInt(args[1]);
        int values = 0;

        Set<Predicate> predicates = new HashSet<>();
        predicates.add(new GreaterThanOrEqualPredicate(begin));
        predicates.add(new LessThanOrEqualPredicate(end));
        predicates.add(new TwoConsecutiveEqualDigits());
        predicates.add(new NonDecreasingDigits());

        processNumbers(begin, end, predicates);

        predicates.add(new ExactlyTwoConsecutiveEqualDigits());

        processNumbers(begin, end, predicates);
    }

    public static void processNumbers(int begin,
                                      int end,
                                      Set<Predicate> predicates)
    {
        int matchingValues = 0;
        for (int i = begin; i <= end; ++i) {
            boolean match = true;
            for (Predicate p : predicates) {
                if (!p.matches(i)) {
                    match = false;
                    break;
                }
            }
            if (match) {
                ++matchingValues;
            }
        }
        System.out.println("There are " + matchingValues + " possible passwords.");
    }

    public static interface Predicate {
        boolean matches(int value);
    }

    public static class GreaterThanOrEqualPredicate implements Predicate {

        private int boundary;

        public GreaterThanOrEqualPredicate(int boundary) {
            this.boundary = boundary;
        }
        public boolean matches(int value) {
            return value >= boundary;
        }
    }

    public static class LessThanOrEqualPredicate implements Predicate {

        private int boundary;

        public LessThanOrEqualPredicate(int boundary) {
            this.boundary = boundary;
        }
        public boolean matches(int value) {
            return value <= boundary;
        }
    }

    public static class TwoConsecutiveEqualDigits implements Predicate {
        public boolean matches(int value) {
            int lastDigit = Integer.MAX_VALUE;
            for (int i = 0; (int)Math.pow(10, i) < value; ++i) {
                int newDigit = getDigitFromRight(i, value);
                if (lastDigit == newDigit) {
                    return true;
                } else {
                    lastDigit = newDigit;
                }
            }
            return false;
        }
    }

    public static class ExactlyTwoConsecutiveEqualDigits implements Predicate {
        public boolean matches(int value) {
            int lastDigit = Integer.MAX_VALUE;
            int currentConsecutive = 1;
            for (int i = 0; (int)Math.pow(10, i) < value; ++i) {
                int newDigit = getDigitFromRight(i, value);
                if (lastDigit == newDigit) {
                    ++currentConsecutive;
                } else {
                    lastDigit = newDigit;
                    if (currentConsecutive == 2) {
                        return true;
                    }
                    currentConsecutive = 1;
                }
            }
            return currentConsecutive == 2;
        }
    }

    public static class NonDecreasingDigits implements Predicate {
        public boolean matches(int value) {
            int previousDigit = Integer.MAX_VALUE;
            for (int i = 0; (int)Math.pow(10, i) < value; ++i) {
                int newDigit = getDigitFromRight(i, value);
                if (newDigit <= previousDigit) {
                    previousDigit = newDigit;
                } else {
                    return false;
                }
            }
            return true;
        }
    }

    public static int getDigitFromRight(int digit, int value) {
        int temp = value / (int)(Math.pow(10, digit));
        return temp % 10;
    }
}
