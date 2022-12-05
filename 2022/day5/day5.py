#!/usr/bin/env python

from collections import deque
from copy import deepcopy
from re import match
from typing import Dict

stacks: Dict[int, deque] = {}
stacks_p2: Dict[int, deque] = {}

with open("input.txt") as f:
    lines = f.readlines()
    stacks_complete = False
    for line in lines:
        line = line.rstrip()
        if not stacks_complete:
            i = 1
            while i < len(line):
                stack_number = int(i / 4) + 1
                if stack_number not in stacks:
                    stacks[stack_number] = deque([])
                if line[i].isalpha():
                    stacks[stack_number].appendleft(line[i])
                i += 4
        if len(line) == 0:
            stacks_complete = True
            for s in stacks:
                print(f" Stack {s}: {stacks[s]}")
            stacks_p2 = deepcopy(stacks)

        if stacks_complete:
            m = match(r"^move (\d+) from (\d+) to (\d+)", line)
            if m is not None:
                crates_to_move = int(m.group(1))
                from_stack = int(m.group(2))
                to_stack = int(m.group(3))
                # Part 1
                for j in range(0, crates_to_move):
                    c = stacks[from_stack].pop()
                    stacks[to_stack].append(c)

                # Part 2
                temp = deque()
                for k in range(0, crates_to_move):
                    c = stacks_p2[from_stack].pop()
                    temp.append(c)
                for l in range(0, crates_to_move):
                    c = temp.pop()
                    stacks_p2[to_stack].append(c)

    # Part 1
    top_crates = ""
    for k in range(1, len(stacks)+1):
        if len(stacks[k]) > 0:
            top_crates += stacks[k].pop()
    print(f"Part 1 Top crates = {top_crates}")

    # Part 2
    top_crates_p2 = ""
    for k in range(1, len(stacks_p2)+1):
        if len(stacks_p2[k]) > 0:
            top_crates_p2 += stacks_p2[k].pop()
    print(f"Part 2 Top crates = {top_crates_p2}")
