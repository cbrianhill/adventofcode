import re

class Food:
    def __init__(self, ingredients, allergens):
        self.ingredients = ingredients
        self.allergens = allergens


def count_ingredients(food_list):
    ingredient_counts = {}
    for f in food_list:
        for i in f.ingredients:
            if i in ingredient_counts:
                ingredient_counts[i] += 1
            else:
                ingredient_counts[i] = 1
    return ingredient_counts

with open('input/day21.txt') as file:

    foods = []
    all_allergens = []
    all_ingredients = []
    allergen_to_foods = {}
    allergen_to_ingredient = {}
    possible_allergen_containing_ingredients = []

    for line in file.readlines():
        m = re.match('^(.+) \(contains (.+)\)', line)
        if m:
            ingredients = m[1].split()
            allergens = m[2].split(', ')
            f = Food(ingredients, allergens)
            foods.append(f)
            for i in ingredients:
                if i not in all_ingredients:
                    all_ingredients.append(i)
            for a in allergens:
                if a not in all_allergens:
                    all_allergens.append(a)
                    allergen_to_foods[a] = [f]
                else:
                    allergen_to_foods[a].append(f)
    for a in all_allergens:
        foods_containing = len(allergen_to_foods[a])
        ingredient_counts = count_ingredients(allergen_to_foods[a])
        for i in ingredient_counts:
            if ingredient_counts[i] == foods_containing:
                if a in allergen_to_ingredient:
                    allergen_to_ingredient[a].append(i)
                else:
                    allergen_to_ingredient[a] = [i]
                if i not in possible_allergen_containing_ingredients:
                    possible_allergen_containing_ingredients.append(i)
    non_allergen_appearances = 0
    for f in foods:
        for i in f.ingredients:
            if i not in possible_allergen_containing_ingredients:
                non_allergen_appearances += 1
    print(f'{non_allergen_appearances} non-allergic ingredient appearances.')

    allergens_found = 0
    allergens_map = {}
    while allergens_found < len(all_allergens):
        for a in allergen_to_ingredient:
            if len(allergen_to_ingredient[a]) == 1:
                ingredient = allergen_to_ingredient[a][0]
                allergens_map[a] = ingredient
                allergens_found += 1
                for x in allergen_to_ingredient:
                    if ingredient in allergen_to_ingredient[x]:
                        allergen_to_ingredient[x].remove(ingredient)
    print(f'{allergens_map}')
    sorted_allergen_list = list(allergens_map.keys())
    sorted_allergen_list.sort()
    for all in sorted_allergen_list:
        print(f',{allergens_map[all]}', end='')