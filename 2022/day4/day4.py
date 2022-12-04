#!/usr/bin/env python

from typing import List

def contained(inner_range: List[int], outer_range: List[int]) -> bool:
    if inner_range[0] >= outer_range[0] and inner_range[1] <= outer_range[1]:
        return True
    return False

def any_overlap(range_1: List[int], range_2: List[int]) -> bool:
    return range_1[1] >= range_2[0] and range_1[0] <= range_2[1]

with open("input.txt") as f:
    lines = f.readlines()
    fully_contained = 0
    overlapping = 0
    for line in  lines:
        elves = line.strip().split(",")
        elf_ranges = [list(map(lambda x: int(x), e)) for e in (e.split("-") for e in elves)]
        if contained(elf_ranges[0], elf_ranges[1]) or contained(elf_ranges[1], elf_ranges[0]):
            fully_contained += 1
        if any_overlap(elf_ranges[0], elf_ranges[1]):
            overlapping += 1
    print (f"{fully_contained} pairs of elves are completely redundant.")
    print (f"{overlapping} pairs of elves are partially redundant.")
