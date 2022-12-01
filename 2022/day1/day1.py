#!/usr/bin/env python

from typing import List

class Elf:
    number: int
    calories: int
    def __init__(self, number: int):
        self.number = number
        self.calories = 0

    def addItem(self, calories: int):
        self.calories += calories


elves: List[Elf] = []
elves.append(Elf(0))
backpack: Elf = elves[0]
next_elf: int = 1
with open('input.txt') as file:
    lines: List[str] = file.readlines()
    for line in lines:
        value: str = line.strip()
        if value == "":
            backpack = Elf(next_elf)
            next_elf += 1
            elves.append(backpack)
        else:
            backpack.addItem(int(value))

elves.sort(key=lambda e: e.calories)
highest_calories: int = elves[len(elves) - 1].calories
highest_elf: int = elves[len(elves) - 1].number

print(f"Elf {highest_elf} is carrying {highest_calories} calories.")

top_three_calories: int = 0
for i in range(len(elves) - 3, len(elves)):
    top_three_calories += elves[i].calories

print (f"Top three elves are carrying {top_three_calories} calories.")
