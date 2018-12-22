package org.thehills.brian.adventofcode;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;

public class Day14 {

    public static void main(String args[]) throws IOException {
        Integer input = 864801;
        StringBuilder recipes = new StringBuilder("37");
        Integer elf1 = 0;
        Integer elf2 = 1;
        LinkedList<Integer> inputDigits = new LinkedList<>();
        Integer tmpInput = input;
        while (tmpInput > 0) {
            int digit = tmpInput % 10;
            tmpInput = tmpInput / 10;
            inputDigits.addFirst(digit);
            System.out.println(digit);
        }

        System.out.println("Pattern: ");

        boolean inputFound = false;
        while (!inputFound) {
            Integer newRecipeSum = Integer.parseInt(recipes.substring(elf1, elf1+1)) + Integer.parseInt(recipes.substring(elf2, elf2+1));
            Integer onesDigit = newRecipeSum % 10;
            newRecipeSum = newRecipeSum / 10;
            Integer tensDigit = newRecipeSum % 10;
            if (tensDigit > 0) {
                recipes.append(Integer.toString(tensDigit));
                if (recipes.length() >= inputDigits.size()) {
                    inputFound = true;
                    for (int i = 0; i < inputDigits.size(); ++i) {
                        int recipeIndex = recipes.length() - inputDigits.size() + i;
                        if (Integer.parseInt(recipes.substring(recipeIndex, recipeIndex + 1)) != inputDigits.get(i)) {
                            inputFound = false;
                            break;
                        }
                    }
                    if (inputFound) {
                        System.out.println("Found pattern " + input + " at " + (recipes.length() - inputDigits.size()));
                    }
                }
            }
            recipes.append(Integer.toString(onesDigit));
            Integer elf1Advancement = Integer.parseInt(recipes.substring(elf1, elf1+1)) + 1;
            elf1 = (elf1 + elf1Advancement) % recipes.length();
            Integer elf2Advancement = Integer.parseInt(recipes.substring(elf2, elf2+1)) + 1;
            elf2 = (elf2 + elf2Advancement) % recipes.length();
            /*for (Recipe recipe : recipes) {
                System.out.print(recipe.score + " ");
            }
            System.out.println("\nElf1: " + elf1 + ", Elf2: " + elf2); */
            if (recipes.length() >= inputDigits.size()) {
                inputFound = true;
                for (int i = 0; i < inputDigits.size(); ++i) {
                    int recipeIndex = recipes.length() - inputDigits.size() + i;
                    if (Integer.parseInt(recipes.substring(recipeIndex, recipeIndex + 1)) != inputDigits.get(i)) {
                        inputFound = false;
                        break;
                    }
                }
                if (inputFound) {
                    System.out.println("Found pattern " + input + " at " + (recipes.length() - inputDigits.size()));
                }
            }
            if (recipes.length() == input + 10) {
                for (int i = 0; i < 10; ++i) {
                    System.out.println(recipes.charAt(input + i));
                }
            }
            /*
            if (recipes.length() % 1000 == 0) {
                System.out.println("Processed " + recipes.length() + " recipes");
            } */
        }


    }

    public static class Recipe {
        public int score;
        public Recipe(int score) {
            this.score = score;
        }
    }
}
