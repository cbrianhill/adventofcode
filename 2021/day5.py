import re

def print_grid(grid, minx, miny, maxx, maxy):
    for y in range(miny, maxy+1):
        for x in range(minx, maxx+1):
            if (x,y) in grid:
                print(grid[(x,y)], end='')
            else:
                print('.', end='')
        print('')

with open("input/day5.txt") as file:
    lines = file.readlines()
    entries = [line.strip() for line in lines]
    grid = {}
    diaggrid = {}
    minx = 0
    maxx = 0
    miny = 0
    maxy = 0

    for e in entries:
        match = re.match(r"(\d+),(\d+) -> (\d+),(\d+)", e)
        if match:
            x1 = int(match.group(1))
            y1 = int(match.group(2))
            x2 = int(match.group(3))
            y2 = int(match.group(4))
            if x1 < minx:
                minx = x1
            elif x1 > maxx:
                maxx = x1
            if x2 < minx:
                minx = x2
            elif x2 > maxx:
                maxx = x2
            if y1 < miny:
                miny = y1
            elif y1 > maxy:
                maxy = y1
            if y2 < miny:
                miny = y2
            elif y2 > maxy:
                maxy = y2
            if x1 == x2:
                for i in range(min(y1, y2), max(y1, y2) + 1):
                    if (x1, i) in grid:
                        grid[(x1, i)] += 1
                    else:
                        grid[(x1, i)] = 1
                    if (x1, i) in diaggrid:
                        diaggrid[(x1, i)] += 1
                    else:
                        diaggrid[(x1, i)] = 1
            elif y1 == y2:
                for i in range(min(x1, x2), max(x1, x2) + 1):
                    if (i, y1) in grid:
                        grid[(i, y1)] += 1
                    else:
                        grid[(i, y1)] = 1
                    if (i, y1) in diaggrid:
                        diaggrid[(i, y1)] += 1
                    else:
                        diaggrid[(i, y1)] = 1
            else:
                x_mult = 1
                y_mult = 1
                if x1 > x2:
                    x_mult = -1
                if y1 > y2:
                    y_mult = -1
                for i in range(0, max(x1, x2) - min(x1, x2) + 1):
                    cx = x1 + x_mult * i
                    cy = y1 + y_mult * i
                    if (cx, cy) in diaggrid:
                        diaggrid[(cx,cy)] += 1
                    else:
                        diaggrid[(cx,cy)] = 1
                

    print_grid(grid, minx, miny, maxx, maxy)
    two_or_more = 0
    for key in grid:
        if grid[key] >= 2:
            two_or_more += 1
    print(f'Not including diagonal lines, There are {two_or_more} places where lines overlap.')

    print_grid(diaggrid, minx, miny, maxx, maxy)
    two_or_more = 0
    for key in diaggrid:
        if diaggrid[key] >= 2:
            two_or_more += 1
    print(f'Including diagonal lines, There are {two_or_more} places where lines overlap.')
