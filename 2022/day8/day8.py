#!/usr/bin/env python

from functools import reduce
from typing import List

def is_visible(grid: List[List[int]], row: int, col: int) -> bool:
    if row == 0 or col == 0 or row == len(grid) - 1 or col == len(grid[0]) - 1:
        return True
    if sum(1 for _ in filter(lambda x: x < grid[row][col], [grid[i][col] for i in range(0, row)])) == row:
        return True
    if sum(1 for _ in filter(lambda x: x < grid[row][col], [grid[i][col] for i in range(row+1, len(grid))])) == len(grid) - row - 1:
        return True
    if sum(1 for _ in filter(lambda x: x < grid[row][col], [grid[row][j] for j in range(0, col)])) == col:
        return True
    if sum(1 for _ in filter(lambda x: x < grid[row][col], [grid[row][j] for j in range(col+1, len(grid))])) == len(grid) - col - 1:
        return True
    else:
        return False


def scenic_score(grid: List[List[int]], row: int, col: int) -> int:
    vd: List[int] = []
    up = 0
    down = 0
    left = 0
    right = 0
    for i in range(row - 1, -1, -1):
        up += 1
        if grid[i][col] >= grid[row][col]:
            break
    vd.append(up)
    for j in range(row + 1, len(grid)):
        down += 1
        if grid[j][col] >= grid[row][col]:
            break
    vd.append(down)
    for k in range(col - 1, -1, -1):
        left += 1
        if grid[row][k] >= grid[row][col]:
            break
    vd.append(left)
    for l in range(col + 1, len(grid[0])):
        right += 1
        if grid[row][l] >= grid[row][col]:
            break
    vd.append(right)
    return reduce(lambda x, y: x * y, vd)



with open("input.txt") as f:
    lines = f.readlines()
    grid = [list(map(lambda z: int(z), list(x.strip()))) for x in lines]
    visible_trees = sum(1 for _ in filter(lambda x: x, [is_visible(grid, i, j) for i in range(0, len(grid)) for j in range(0, len(grid))]))
    print(f"There are {visible_trees} visible trees.")

    max_scenic_score = reduce(lambda x,y: max(x,y), [scenic_score(grid, i, j) for j in range(0, len(grid[0])) for i in range(0, len(grid))])
    print(f"Max scenic score = {max_scenic_score}")
