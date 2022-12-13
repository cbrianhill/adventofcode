#!/usr/bin/env python3

import sys
from typing import List, Tuple

distance: List[List[int]]

def sortNodes(i: Tuple[int, int]) -> int: return distance[i[0]][i[1]]

def elevation(c: str) -> int:
    if c == 'S':
        return ord('a')
    if c == 'E':
        return ord('z')
    return ord(c)

def neighbors(i: Tuple[int, int], grid: List[List[str]], to_visit: List[Tuple[int, int]]) -> List[Tuple[int, int]]:
    result = []
    (y,x) = i
    if y > 0 and elevation(grid[y-1][x]) < elevation(grid[y][x]) + 2 and (y-1,x) in to_visit:
        result.append((y-1,x))
    if y < len(grid) - 1 and elevation(grid[y+1][x]) < elevation(grid[y][x]) + 2 and (y+1,x) in to_visit:
        result.append((y+1,x))
    if x > 0 and elevation(grid[y][x-1]) < elevation(grid[y][x]) + 2 and (y,x-1) in to_visit:
        result.append((y,x-1))
    if x < len(grid[0]) - 1 and elevation(grid[y][x+1]) < elevation(grid[y][x]) + 2 and (y,x+1) in to_visit:
        result.append((y,x+1))
    return result


with open("input.txt") as file:
    lines = file.readlines()
    grid = []
    start = (0, 0)
    end = (0, 0)
    for i in range(0, len(lines)):
        l = lines[i].strip()
        grid.append(list(l))

        if l.find('S') != -1:
            start = (i, l.find('S'))
        if l.find('E') != -1:
            end = (i, l.find('E'))

    to_visit = [ (y,x) for x in range(0, len(grid[0])) for y in range(0, len(grid)) ]
    distance = [[ sys.maxsize for _ in range(0, len(grid[0]))] for _ in range(0, len(grid))]
    previous: List[List[Tuple[int,int]]] = [[ (0, 0) for _ in range(0, len(grid[0]))] for _ in range(0, len(grid))]

    distance[start[0]][start[1]] = 0
    while len(to_visit) > 0:
        to_visit.sort(key=sortNodes)
        u = to_visit[0]
        del to_visit[0]

        for node in neighbors(u, grid, to_visit):
            new_dist = distance[u[0]][u[1]] + 1
            if new_dist < distance[node[0]][node[1]]:
                distance[node[0]][node[1]] = new_dist
                previous[node[0]][node[1]] = u
    print(f"Distance from S to E = {distance[end[0]][end[1]]}")

