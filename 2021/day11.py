def inc_adjacent(coords, grid, queue):
    (r, c) = coords
    if r > 0 and c > 0:
        inc(r-1, c-1, grid, queue)
    if r > 0:
        inc(r-1, c, grid, queue)
    if r > 0 and c < len(grid[r]) - 1:
        inc(r-1, c+1, grid, queue)
    if c < len(grid[r]) - 1:
        inc(r, c+1, grid, queue)
    if c < len(grid[r]) - 1 and r < len(grid) - 1:
        inc(r+1, c+1, grid, queue)
    if r < len(grid) - 1:
        inc(r+1, c, grid, queue)
    if r < len(grid) - 1 and c > 0:
        inc(r+1, c-1, grid, queue)
    if c > 0:
        inc(r, c-1, grid, queue)

def inc(row, col, grid, queue):
    grid[row][col] += 1
    if grid[row][col] == 10:
        queue.append((row, col))

with open('input/day11.txt') as file:
    lines = file.readlines()
    grid = []
    for line in lines:
        octopi = [int(x) for x in line.strip()]
        grid.append(octopi)
    total_flashes = 0
    flashes = 0
    s = 0
    while flashes < 100:
        s += 1
        to_flash = []
        flashes = 0
        for r in range(0, len(grid)):
            for c in range(0, len(grid[r])):
                grid[r][c] += 1
                if grid[r][c] == 10:
                    to_flash.append((r,c))
        while len(to_flash) > 0:
            o = to_flash.pop(0)
            flashes += 1
            inc_adjacent(o, grid, to_flash)
        total_flashes += flashes
        print(f'Step {s}: {flashes} flashes, total = {total_flashes}')
        for r in range(0, len(grid)):
            for c in range(0, len(grid[r])):
                if grid[r][c] > 9:
                    grid[r][c] = 0

