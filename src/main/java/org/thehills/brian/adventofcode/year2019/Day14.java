package org.thehills.brian.adventofcode.year2019;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;
import org.apache.commons.math3.util.ArithmeticUtils;

public class Day14
{

    public static void main(String args[]) throws IOException
    {
        InputStream is = null;
        try
        {
            is = new FileInputStream(args[0]);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            Pattern p = Pattern.compile("([0-9A-Z, ]+) => (\\d+) ([A-Z]+)");
            Pattern p2 = Pattern.compile("(\\d+) ([A-Z]+)");
            List<Reaction> reactions = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                Matcher m = p.matcher(line);
                if (m.matches()) {
                    ChemicalIngredient result = new ChemicalIngredient(Integer.parseInt(m.group(2)), m.group(3));
                    String ingredients[] = m.group(1).split(", ");
                    List<ChemicalIngredient> ingredientList = new ArrayList<>();
                    for (String ingredient : ingredients) {
                        Matcher m2 = p2.matcher(ingredient);
                        if (m2.matches())
                        {
                            ingredientList.add(new ChemicalIngredient(Integer.parseInt(m2.group(1)), m2.group(2)));
                        }
                    }
                    reactions.add(new Reaction(result, ingredientList));
                }
            }

            Map<String, Reaction> resultMap = new HashMap<>();
            reactions.stream().forEach(r -> resultMap.put(r.getResultChemical(), r));
            long requiredOre = getReducedReactionForFuel(resultMap, 1);
            System.out.println("Total ore required for 1 FUEL = " + requiredOre);

            long lowerBound = 1000000000000L / requiredOre;
            long upperBound = lowerBound * 2;
            System.out.println("lowerBound = " + lowerBound);
            System.out.println("upperBound = " + upperBound);
            while (upperBound - lowerBound > 1) {
                long guess = lowerBound + (upperBound - lowerBound) / 2;
                long oreRequiredForGuess = getReducedReactionForFuel(resultMap, guess);
                if (oreRequiredForGuess < 1000000000000L) {
                    lowerBound += (upperBound - lowerBound) / 2;
                    System.out.println("Resetting lowerBound to " + lowerBound);
                } else if (oreRequiredForGuess > 1000000000000L) {
                    upperBound -= (upperBound - lowerBound) / 2;
                    System.out.println("Resetting upperBound to " + upperBound);
                } else {
                    System.out.println("Exact match found: " + guess + " requires " + oreRequiredForGuess + " ORE.");
                }
            }
            System.out.println("lowerBound = " + lowerBound);
            System.out.println("upperBound = " + upperBound);


        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public static long getReducedReactionForFuel(Map<String, Reaction> resultMap, long multiplier)
    {
        Reaction fuelReaction = new Reaction(resultMap.get("FUEL"));
        fuelReaction.multiplyBy(multiplier);
        while(fuelReaction.requiresMoreReactions()) {
            fuelReaction.replaceFirstGeneratedIngredientWithRecipes(resultMap);
        }
        return fuelReaction.countRequiredOre();
    }

    public static class ChemicalIngredient {
        public long units;
        public String chemical;

        public ChemicalIngredient(long units, String chemical) {
            this.units = units;
            this.chemical = chemical;
        }

        public ChemicalIngredient(ChemicalIngredient toCopy) {
            units = toCopy.units;
            chemical = toCopy.chemical;
        }

        public String toString() {
            return units + " " + chemical;
        }
    }

    public static class Reaction {
        public ChemicalIngredient result;
        public List<ChemicalIngredient> ingredients = new ArrayList<>();
        public Map<String, ChemicalIngredient> surplusMap = new HashMap<>();

        public Reaction(ChemicalIngredient result, List<ChemicalIngredient> ingredients) {
            this.result = result;
            this.ingredients = ingredients;
        }

        public Reaction(Reaction toCopy) {
            result = new ChemicalIngredient(toCopy.result);
            toCopy.ingredients.stream()
                    .forEach(i -> {
                        ingredients.add(new ChemicalIngredient(i));
                    });
        }

        public String getResultChemical() {
            return result.chemical;
        }

        public boolean requiresMoreReactions() {
            return ingredients.stream().anyMatch(i -> !i.chemical.equals("ORE"));
        }

        public void replaceFirstGeneratedIngredientWithRecipes(Map<String, Reaction> recipes) {
            Optional<ChemicalIngredient> ingredientOptional = ingredients.stream()
                                                                         .filter(i -> !i.chemical.equals("ORE"))
                                                                         .findFirst();
            ingredientOptional.ifPresent(ingredient -> {
                long availableSurplusIngredient = 0;
                if (surplusMap.get(ingredient.chemical) != null) {
                    availableSurplusIngredient = surplusMap.get(ingredient.chemical).units;
                }
                if (availableSurplusIngredient >= ingredient.units) {
                    availableSurplusIngredient -= ingredient.units;
                    surplusMap.get(ingredient.chemical).units = availableSurplusIngredient;
                } else {
                    Reaction reaction = recipes.get(ingredient.chemical);
                    long multiplier = 1;
                    if (reaction.result.units + availableSurplusIngredient < ingredient.units)
                    {
                        multiplier = (ingredient.units - availableSurplusIngredient) / reaction.result.units +
                                     (((ingredient.units - availableSurplusIngredient) % reaction.result.units == 0) ? 0 : 1);
                    }
                    System.out.println("Replacing " + ingredient + " with " + multiplier + " x " + reaction.ingredients);
                    if (multiplier * reaction.result.units + availableSurplusIngredient >= ingredient.units)
                    {
                        if (surplusMap.get(reaction.result.chemical) == null)
                        {
                            surplusMap.put(reaction.result.chemical,
                                           new ChemicalIngredient(0, reaction.result.chemical));
                        }
                        surplusMap.get(reaction.result.chemical).units =
                                (multiplier * reaction.result.units + availableSurplusIngredient) - ingredient.units;
                    }
                    final long multiplierLambda = multiplier;
                    reaction.ingredients.stream().forEach(i -> {
                        Optional<ChemicalIngredient> foundIngredient = ingredients.stream()
                                                                                  .filter(ing -> i.chemical.equals(ing.chemical))
                                                                                  .findFirst();
                        foundIngredient.ifPresent(ing -> {
                            ing.units += multiplierLambda * i.units;
                        });
                        if (!foundIngredient.isPresent())
                        {
                            ingredients.add(new ChemicalIngredient(multiplierLambda * i.units, i.chemical));
                        }

                    });
                }
                ingredients.remove(ingredient);

                System.out.println("Ingredients = " + ingredients);
                System.out.println("Surplus = " + surplusMap.values());

            });
        }

        public long countRequiredOre() {
            return ingredients.stream()
                    .filter(ing -> ing.chemical.equals("ORE"))
                    .map(ing -> ing.units)
                    .reduce(0L, (a, b) -> a + b);
        }

        public void multiplyBy(long multiplier) {
            this.result.units *= multiplier;
            this.ingredients.stream()
                    .forEach(i -> {
                        i.units *= multiplier;
                    });
        }
    }

}
