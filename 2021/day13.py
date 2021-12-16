import re

def print_grid(grid, max_x, max_y):
    for y in range(0, max_y + 1):
        for x in range(0, max_x + 1):
            if (x, y) in grid:
                print(grid[(x, y)], end='')
            else:
                print('.', end='')
        print('')

def fold_x(grid, location, max_x, max_y):
    new_max_x = location - 1
    for x in range(1, max_x - location + 1):
        for y in range(0, max_y + 1):
            if (location + x, y) in grid:
                grid[(location - x, y)] = grid[(location + x, y)]
                grid.pop((location + x, y))
    return new_max_x

def fold_y(grid, location, max_x, max_y):
    new_max_y = location - 1
    for y in range(1, max_y - location + 1):
        for x in range(0, max_x + 1):
            if (x, location + y) in grid:
                grid[(x, location - y)] = grid[(x, location + y)]
                grid.pop((x, location + y))
    return new_max_y

with open('input/day13.txt') as file:
    lines = file.readlines()
    max_x = 0
    max_y = 0
    grid = {}
    printed_initial = False
    for line in lines:
        line = line.strip()
        coordmatch = re.match(r'(\d+),(\d+)', line)
        foldmatch = re.match(r'fold along (\w)=(\d+)', line)
        if coordmatch:
            x_coord = int(coordmatch.group(1))
            y_coord = int(coordmatch.group(2))
            if x_coord > max_x:
                max_x = x_coord
            if y_coord > max_y:
                max_y = y_coord
            grid[(x_coord, y_coord)] = '#'
        elif foldmatch:
            if not printed_initial:
                printed_initial = True
                print(f'Initial state:')
                print_grid(grid, max_x, max_y)
                print('')
            axis = foldmatch.group(1)
            coord = int(foldmatch.group(2))
            print(f'Folding along {axis}={coord}')
            if axis == 'x':
                max_x = fold_x(grid, coord, max_x, max_y)
            elif axis == 'y':
                max_y =fold_y(grid, coord, max_x, max_y)
            print_grid(grid, max_x, max_y)
            print(f'Dots: {len(grid)}')


