#!/usr/bin/env python3

from functools import reduce
from typing import Set, Tuple


def countExposedSurfaces(grid: Set[Tuple[int, int, int]], pixel: Tuple[int, int, int]) -> int:
    exposedSurfaces = 0
    (x, y, z) = pixel
    if (x-1, y, z) not in grid:
        exposedSurfaces += 1
    if (x+1, y, z) not in grid:
        exposedSurfaces += 1
    if (x, y-1, z) not in grid:
        exposedSurfaces += 1
    if (x, y+1, z) not in grid:
        exposedSurfaces += 1
    if (x, y, z-1) not in grid:
        exposedSurfaces += 1
    if (x, y, z+1) not in grid:
        exposedSurfaces += 1
    return exposedSurfaces


def findSurfaceArea(grid: Set[Tuple[int, int, int]]) -> int:
    return reduce(lambda a, b: a + countExposedSurfaces(grid, b), grid, 0)


if __name__ == "__main__":
    with open("input.txt") as file:
        grid: Set[Tuple[int, int, int]] = set({})
        lines = file.readlines()
        for line in lines:
            (x, y, z) = line.split(",")
            grid.add((int(x), int(y), int(z)))
        print(grid)
        print(f"Surface area = {findSurfaceArea(grid)}")
