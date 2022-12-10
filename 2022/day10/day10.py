#!/usr/bin/env python3

from re import match
from typing import Callable, Dict, List, Optional

def noop(register: List[int], cycle_num: int, _: Optional[int]) -> int:
    cycle_num += 1
    register.append(register[len(register)-1])
    return cycle_num

def addx(register: List[int], cycle_num: int, arg: Optional[int]) -> int:
    if arg is None:
        return cycle_num
    cycle_num += 2
    register.append(register[len(register)-1])
    register.append(register[len(register)-1]+arg)
    return cycle_num

commands: Dict[str, Callable[[List[int], int, Optional[int]], int]] = {
    "noop": noop,
    "addx": addx
}

def runcmd(register: List[int], cycle_num: int, command: str, arg: Optional[int]) -> int:
    cycle_num = commands[command](register, cycle_num, arg)
    return cycle_num

def paint(screen: List[List[bool]], register: List[int], cycle_num: int) -> None:
    position = cycle_num % 240 - 1
    cur_row = int(position / 40)
    cur_col = position % 40
    if register[cycle_num] >= cur_col -1 and register[cycle_num] <= cur_col + 1:
        screen[cur_row][cur_col] = True
    else:
        screen[cur_row][cur_col] = False

def display(screen: List[List[bool]]) -> None:
    for r in screen:
        output = ""
        for c in r:
            output += "#" if c else "."
        print(output)

with open("input.txt") as file:
    lines = file.readlines()
    register: List[int] = [0, 1]
    cycle_num: int = 1
    for l in lines:
        m = match(r"^(\w+) ?(-?\d+)?", l)
        if m:
            command = m.group(1)
            arg = int(m.group(2)) if m.group(2) else None
            cycle_num = runcmd(register, cycle_num, command, arg)
    print(f"There have been {len(register)} cycles.")
    part1 = 20 * register[20] + 60 * register[60] + 100 * register[100] + 140 * register[140] + 180 * register[180] + 220 * register[220]
    print(f"Signal strength is {part1} during 220th cycle.")

    screen: List[List[bool]] = [ [ False for _ in range(0, 40) ] for _ in range (0, 6) ]
    for i in range(1, len(register) - 1):
        paint(screen, register, i)
    display(screen)
