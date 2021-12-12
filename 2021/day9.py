def print_grid(grid):
    for i in range(0, len(grid)):
        for j in range(0, len(grid[i])):
            print(grid[i][j], end='')
        print('')

with open('input/day9.txt') as file:
    grid = []
    lines = file.readlines()
    for line in lines:
        line = line.strip()
        grid.append([ int(x) for x in list(line) ])

    low_points = []

    for r in range(0, len(grid)):
        for c in range(0, len(grid[r])):
            if r > 0 and grid[r-1][c] <= grid[r][c]:
                continue
            if c > 0 and grid[r][c-1] <= grid[r][c]:
                continue
            if r < len(grid) - 1 and grid[r+1][c] <= grid[r][c]:
                continue
            if c < len(grid[r]) - 1 and grid[r][c+1] <= grid[r][c]:
                continue
            low_points.append((r, c))
    sum = 0
    for (r, c) in low_points:
        sum += grid[r][c] + 1
    print(f'Sum of risk levels = {sum}')

    basin_sizes = []

    for (r, c) in low_points:
        points_to_check = [(r, c)]
        basin_size = 0
        checked = []
        mask = [r[:] for r in grid]
        while len(points_to_check) > 0:
            (y, x) = points_to_check.pop(0)
            if grid[y][x] < 9 and (y,x) not in checked:
                basin_size += 1
                mask[y][x] = 'b'
                if y > 0 and (y-1, x) not in checked:
                    points_to_check.append((y-1, x))
                if x > 0 and (y, x-1) not in checked:
                    points_to_check.append((y, x-1))
                if y < len(grid) - 1 and (y+1, x) not in checked:
                    points_to_check.append((y+1, x))
                if x < len(grid[y]) - 1 and (y, x+1) not in checked:
                    points_to_check.append((y, x+1))
            checked.append((y,x))
        basin_sizes.append(basin_size)
        print(f'Low point ({r},{c}) basin size {basin_size}')
        print_grid(mask)
    basin_sizes.sort(reverse=True)
    print(f'Basin sizes: {basin_sizes}')
    answer = basin_sizes[0] * basin_sizes[1] * basin_sizes[2]
    print(f'Product of 3 largest basins: {answer}')
