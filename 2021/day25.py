def copy_grid(grid):
    ngrid = []
    for row in grid:
        ngrid.append(row.copy())
    return ngrid

def move_east(grid, new_grid):
    moves = 0
    for r in range(len(grid)):
        for c in range(len(grid[r])):
            if grid[r][c] == '>':
                next_space = (c + 1) % len(grid[r])
                if grid[r][next_space] == '.':
                    new_grid[r][c] = '.'
                    new_grid[r][next_space] = '>'
                    moves += 1
    return moves

def move_south(grid, new_grid):
    moves = 0
    for r in range(len(grid)):
        for c in range(len(grid[r])):
            if grid[r][c] == 'v':
                next_space = (r + 1) % len(grid)
                if grid[next_space][c] == '.':
                    new_grid[r][c] = '.'
                    new_grid[next_space][c] = 'v'
                    moves += 1
    return moves

def step(grid):
    new_grid = copy_grid(grid)
    moves = 0
    moves += move_east(grid, new_grid)
    new_grid_2 = copy_grid(new_grid)
    moves += move_south(new_grid, new_grid_2)
    return (new_grid_2, moves)

with open('input/day25.txt') as file:
    lines = file.readlines()
    grid = []
    for line in lines:
        line = line.strip()
        grid.append(list(line))
    moves = 0
    steps = 0
    while steps == 0 or moves > 0:
        steps += 1
        (grid, moves) = step(grid)
        print(f'Step {steps} resulted in {moves} moves')
    print(f'Reached 0 moves after step {steps}')


