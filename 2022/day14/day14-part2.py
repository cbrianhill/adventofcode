#!/usr/bin/env python3

from enum import Enum
from typing import Dict, Optional, Tuple

class Material(Enum):
    ROCK = 1,
    SAND = 2

def fall(grid: Dict[Tuple[int,int], Material], max_y: int) -> bool:
    sand_pos = (500,0)
    while sand_pos not in grid:
        if sand_pos[1] == max_y - 1:
            grid[sand_pos] = Material.SAND
            return True
        elif (sand_pos[0], sand_pos[1]+1) not in grid:
            sand_pos = (sand_pos[0], sand_pos[1]+1)
        elif (sand_pos[0]-1, sand_pos[1]+1) not in grid:
            sand_pos = (sand_pos[0]-1, sand_pos[1]+1)
        elif (sand_pos[0]+1, sand_pos[1]+1) not in grid:
            sand_pos = (sand_pos[0]+1, sand_pos[1]+1)
        else:
            grid[sand_pos] = Material.SAND
            return True
    return False

with open("input.txt") as file:
    lines = file.readlines()
    grid: Dict[Tuple[int,int], Material] = {}
    max_y = 0
    for line in lines:
        start: Optional[Tuple[int,int]] = None
        end: Optional[Tuple[int,int]] = None
        line = line.strip()
        nodes = line.split(" -> ")
        for node in nodes:
            (x,y) = node.split(",")
            start = end
            end = (int(x), int(y))
            if start is not None and end is not None:
                if end[1] != start[1]:
                    dir = 1
                    if end[1] < start[1]:
                        dir = -1
                    for i in range(start[1], end[1] + dir, dir):
                        grid[(start[0],i)] = Material.ROCK
                        if i > max_y:
                            max_y = i
                elif end[0] != start[0]:
                    dir = 1
                    if end[0] < start[0]:
                        dir = -1
                    for i in range(start[0], end[0] + dir, dir):
                        grid[(i,start[1])] = Material.ROCK
                        if start[1] > max_y:
                            max_y = start[1]
    max_y += 2
    rested = 0
    while fall(grid, max_y):
        rested += 1
    print(f"{rested} grains of sand came to a rest")
