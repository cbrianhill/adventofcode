#!/usr/bin/env python

from re import match
from typing import Dict, List, Set, Tuple

moves:Dict[str, Tuple[int, int]] = {
    "U": ( 0,  1),
    "D": ( 0, -1),
    "L": (-1,  0),
    "R": ( 1,  0)
}

follows: Dict[Tuple[int, int], Tuple[int, int]] = {
    ( 0,  2): ( 0,  1),
    ( 1,  2): ( 1,  1),
    ( 2,  0): ( 1,  0),
    ( 2,  2): ( 1,  1),
    ( 2,  1): ( 1,  1),
    (-1,  2): (-1,  1),
    (-2,  1): (-1,  1),
    (-2,  0): (-1,  0),
    (-2,  2): (-1,  1),
    (-1, -2): (-1, -1),
    (-2, -1): (-1, -1),
    (-2, -2): (-1, -1),
    ( 0, -2): ( 0, -1),
    ( 1, -2): ( 1, -1),
    ( 2, -1): ( 1, -1),
    ( 2, -2): ( 1, -1)
}


def move(tail_visited: Set[Tuple[int,int]], direction: str, distance: int, tail_pos: Tuple[int,int], mid_pos: List[Tuple[int,int]], head_pos: Tuple[int,int]) -> Tuple[Tuple[int,int], List[Tuple[int,int]], Tuple[int,int]]:
    for _ in range(0, distance):
        head_pos = (head_pos[0] + moves[direction][0], head_pos[1] + moves[direction][1])
        for i in range(0, len(mid_pos)):
            l = head_pos if i == 0 else mid_pos[i-1]
            f = mid_pos[i]
            spread = (l[0] - f[0], l[1] - f[1])
            if spread in follows:
                mid_pos[i] = (mid_pos[i][0] + follows[spread][0], mid_pos[i][1] + follows[spread][1])
        penultimate = mid_pos[len(mid_pos)-1] if len(mid_pos) > 0 else head_pos
        spread = (penultimate[0] - tail_pos[0], penultimate[1] - tail_pos[1])
        if spread in follows:
            tail_pos = (tail_pos[0] + follows[spread][0], tail_pos[1] + follows[spread][1])
        tail_visited.add(tail_pos)
    return (tail_pos, mid_pos, head_pos)

def sim(lines: List[str], knots: int) -> Set[Tuple[int, int]]:
    tail_visited: Set[Tuple[int, int]] = set([(0,0)])
    head_pos = (0,0)
    tail_pos = (0,0)
    mid_pos = [(0,0) for _ in range(0, knots-2)]
    for line in lines:
        m = match(r"^(U|D|L|R) (\d+)", line)
        if m:
            direction = m.group(1)
            distance = int(m.group(2))
            (tail_pos, mid_pos, head_pos) = move(tail_visited, direction, distance, tail_pos, mid_pos, head_pos)
    return tail_visited

with open("input.txt") as file:
    lines = file.readlines()
    tail_visited = sim(lines, 2)
    print(f"Part 1: Tail visited {len(tail_visited)} spaces.")

    tail_visited = sim(lines, 10)
    print(f"Part 2: Tail visited {len(tail_visited)} spaces.")
