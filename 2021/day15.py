import sys
from sortedcontainers import SortedDict

def find_risk(grid, stopx, stopy):
    risks = [[float('inf') for i in range(len(grid[0]))] for j in range(len(grid))]
    risks[0][0] = 0

    to_visit = SortedDict()
    for i in range(len(grid)):
        for j in range(len(grid[0])):
            to_visit[(j,i)] = float('inf')
    to_visit[(0,0)] = 0

    while (stopx-1, stopy-1) in to_visit: #len(to_visit) > 0:
        ((x, y), _) = to_visit.popitem(0)
        if y > 0:# and (x,y-1) in to_visit:
            if risks[y][x] + grid[y-1][x] < risks[y-1][x]:
                risks[y-1][x] = risks[y][x] + grid[y-1][x]
                to_visit[(x,y-1)] = risks[y-1][x]
        if y < len(risks) - 1:# and (x,y+1) in to_visit:
            if risks[y][x] + grid[y+1][x] < risks[y+1][x]:
                risks[y+1][x] = risks[y][x] + grid[y+1][x]
                to_visit[(x,y+1)] = risks[y+1][x]
        if x > 0:# and (x-1, y) in to_visit:
            if risks[y][x] + grid[y][x-1] < risks[y][x-1]:
                risks[y][x-1] = risks[y][x] + grid[y][x-1]
                to_visit[(x-1,y)] = risks[y][x-1]
        if x < len(risks[y]) - 1:# and (x+1,y) in to_visit:
            if risks[y][x] + grid[y][x+1] < risks[y][x+1]:
                risks[y][x+1] = risks[y][x] + grid[y][x+1]
                to_visit[(x+1,y)] = risks[y][x+1]
    return risks[stopy-1][stopx-1]






def copy_grid(grid, increment):

    new_grid = []
    for row in grid:
        new_row = []
        for col in row:
            if col + increment <= 9:
                new_row.append(col+increment)
            else:
                new_row.append(col+increment-9)
        new_grid.append(new_row)
    return new_grid

def print_grid(grid):
    total = 0
    for i in range(len(grid)):
        for j in range(len(grid[i])):
            print(grid[i][j], end='')
            total += grid[i][j]
        print('')
    print(f'total = {total}')


with open('input/day15.txt') as file:
    lines = file.readlines()
    grid = []
    for line in lines:
        line = line.strip()
        grid.append([int(x) for x in list(line)])
    risks = [[0 for i in range(len(grid[0]))] for j in range(len(grid))]
    min_risk = find_risk(grid, 100, 100)
    print(f'Min risk = {min_risk}')

    big_grid = []
    # for vt in range(0, 5):
        # for ht in range(0, 5):
            # print(f'Tile y={vt},x={ht}')
            # if ht == 0:
                # new_tile = copy_grid(grid, vt)
                # for row in new_tile:
                    # big_grid.append(row)
            # else:
                # new_tile = copy_grid(grid, vt + ht)
                # for rownum in range(vt*len(grid), vt*len(grid)+len(grid)):
                    # big_grid[rownum] += new_tile[rownum-vt*len(grid)]

    for c in range(5):
        new_section = copy_grid(grid, c)
        if c == 0:
            for r in new_section:
                big_grid.append(r)
        else:
            for r in range(len(new_section)):
                big_grid[r] += new_section[r]
    first_row = copy_grid(big_grid, 0)
    for r in range(1, 5):
        new_section = copy_grid(first_row, r)
        for row in new_section:
            big_grid.append(row)

    print(f'g = {len(grid)}x{len(grid[0])}, bg = {len(big_grid)}x{len(big_grid[0])}')

    risks = [[0 for i in range(len(big_grid[0]))] for j in range(len(big_grid))]
    print_grid(big_grid)
    min_risk = find_risk(big_grid, 500, 500)
    print(f'Min risk = {min_risk}')
