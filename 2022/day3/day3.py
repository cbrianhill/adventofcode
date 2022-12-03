#!/usr/bin/env python

def get_priority(item: str) -> int:
    if item.isupper():
        return ord(item) - ord('A') + 27
    else:
        return ord(item) - ord('a') + 1


with open("input.txt") as file:
    lines = file.readlines()

    priority_sum = 0
    badge_priority_sum = 0
    lines = [l.strip() for l in lines]
    for n in range(0, len(lines)):
        rucksack = lines[n]
        duplicated_item = [c for c in rucksack[0:int(len(rucksack)/2)] if c in rucksack[int(len(rucksack)/2):]][0]
        priority_sum += get_priority(duplicated_item)
        if n % 3 == 2:
            badge  = [c for c in lines[n-2] if c in lines[n-1] and c in rucksack][0]
            badge_priority_sum += get_priority(badge)
                
    print(f"Part 1: Sum of priorities = {priority_sum}")
    print(f"Part 2: Sum of priorities = {badge_priority_sum}")

