#!/usr/bin/env python3

from functools import reduce
from typing import Dict, Set, Tuple


def isAdjacent(grid: Set[Tuple[int, int, int]], pixel: Tuple[int, int, int]) -> bool:
    (x, y, z) = pixel
    adjacent_pixels = set({
        (x-1, y, z),
        (x+1, y, z),
        (x, y-1, z),
        (x, y+1, z),
        (x, y, z-1),
        (x, y, z+1)
    })
    return len(grid.intersection(adjacent_pixels)) > 0


def countExposedSurfaces(grid: Set[Tuple[int, int, int]], empty: Set[Tuple[int, int, int]], pixel: Tuple[int, int, int]) -> int:
    exposedSurfaces = 0
    (x, y, z) = pixel
    if (x-1, y, z) not in grid and (x-1, y, z) in empty:
        exposedSurfaces += 1
    if (x+1, y, z) not in grid and (x+1, y, z) in empty:
        exposedSurfaces += 1
    if (x, y-1, z) not in grid and (x, y-1, z) in empty:
        exposedSurfaces += 1
    if (x, y+1, z) not in grid and (x, y+1, z) in empty:
        exposedSurfaces += 1
    if (x, y, z-1) not in grid and (x, y, z-1) in empty:
        exposedSurfaces += 1
    if (x, y, z+1) not in grid and (x, y, z+1) in empty:
        exposedSurfaces += 1
    return exposedSurfaces


def findSurfaceArea(grid: Set[Tuple[int, int, int]]) -> int:
    empty_space = set()
    x_min = min([p[0] for p in grid])
    x_max = max([p[0] for p in grid])
    y_min = min([p[1] for p in grid])
    y_max = max([p[1] for p in grid])
    z_min = min([p[2] for p in grid])
    z_max = max([p[2] for p in grid])
    empty_space.update([(x_min - 1, y, z) for y in range(y_min - 1, y_max + 2)
                       for z in range(z_min - 1, z_max + 2)])
    empty_space.update([(x_max + 1, y, z) for y in range(y_min - 1, y_max + 2)
                       for z in range(z_min - 1, z_max + 2)])
    empty_space.update([(x, y_min - 1, z) for x in range(x_min - 1, x_max + 2)
                       for z in range(z_min - 1, z_max + 2)])
    empty_space.update([(x, y_max + 1, z) for x in range(x_min - 1, x_max + 2)
                       for z in range(z_min - 1, z_max + 2)])
    empty_space.update([(x, y, z_min - 1) for x in range(x_min - 1, x_max + 2)
                       for y in range(y_min - 1, y_max + 2)])
    empty_space.update([(x, y, z_max + 1) for x in range(x_min - 1, x_max + 2)
                       for y in range(y_min - 1, y_max + 2)])
    for x in range(x_min - 1, x_max + 2):
        for y in range(y_min - 1, y_max + 2):
            for z in range(z_min - 1, z_max + 2):
                if (x, y, z) not in grid and (x, y, z) not in empty_space:
                    # it's empty
                    reachable = isAdjacent(empty_space, (x, y, z))
                    if reachable:
                        empty_space.add((x, y, z))
    for x in range(x_max + 1, x_min - 2, -1):
        for y in range(y_min - 1, y_max + 2):
            for z in range(z_min - 1, z_max + 2):
                if (x, y, z) not in grid and (x, y, z) not in empty_space:
                    # it's empty
                    reachable = isAdjacent(empty_space, (x, y, z))
                    if reachable:
                        empty_space.add((x, y, z))
    for y in range(y_min - 1, y_max + 2):
        for x in range(x_min - 1, x_max + 2):
            for z in range(z_min - 1, z_max + 2):
                if (x, y, z) not in grid and (x, y, z) not in empty_space:
                    # it's empty
                    reachable = isAdjacent(empty_space, (x, y, z))
                    if reachable:
                        empty_space.add((x, y, z))
    for y in range(y_max + 1, y_min - 2, -1):
        for x in range(x_min - 1, x_max + 2):
            for z in range(z_min - 1, z_max + 2):
                if (x, y, z) not in grid and (x, y, z) not in empty_space:
                    # it's empty
                    reachable = isAdjacent(empty_space, (x, y, z))
                    if reachable:
                        empty_space.add((x, y, z))
    for z in range(z_min - 1, z_max + 2):
        for x in range(x_min - 1, x_max + 2):
            for y in range(y_min - 1, y_max + 2):
                if (x, y, z) not in grid and (x, y, z) not in empty_space:
                    # it's empty
                    reachable = isAdjacent(empty_space, (x, y, z))
                    if reachable:
                        empty_space.add((x, y, z))
    for z in range(z_max + 1, z_min - 2, -1):
        for x in range(x_min - 1, x_max + 2):
            for y in range(y_min - 1, y_max + 2):
                if (x, y, z) not in grid and (x, y, z) not in empty_space:
                    # it's empty
                    reachable = isAdjacent(empty_space, (x, y, z))
                    if reachable:
                        empty_space.add((x, y, z))
    return reduce(lambda a, b: a + countExposedSurfaces(grid, empty_space, b), grid, 0)


if __name__ == "__main__":
    with open("input.txt") as file:
        grid: Set[Tuple[int, int, int]] = set({})
        lines = file.readlines()
        for line in lines:
            (x, y, z) = line.split(",")
            grid.add((int(x), int(y), int(z)))
        print(grid)
        print(f"Surface area = {findSurfaceArea(grid)}")
