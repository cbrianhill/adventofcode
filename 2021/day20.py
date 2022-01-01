def print_grid(grid, min_row, max_row, min_col, max_col):
    print(f'Rows: {min_row}-{max_row}, Columns: {min_col}-{max_col}')
    for i in range(min_row, max_row + 1):
        for j in range(min_col, max_col + 1):
            if (i,j) in grid:
                print(f'{grid[(i,j)]}', end='')
            else:
                print(f'.', end='')
        print(f'')
    lit_pixels = list(filter(lambda c: grid[c] == '#', grid))
    print(f'{len(lit_pixels)} pixels lit')

def get_code(grid, row, col, invert):
    coords = [(row-1,col-1), (row-1,col), (row-1,col+1),(row,col-1),(row,col),(row,col+1),(row+1,col-1),(row+1,col),(row+1,col+1)]
    value = 0
    for c in coords:
        value *= 2
        if c in grid:
            if grid[(c)] == '#':
                value += 1
        elif invert:
            value += 1
    return value

def enhance_grid(grid, code, min_row, max_row, min_col, max_col, invert):
    new_grid = {}
    for i in range(min_row-3, max_row + 4):
        for j in range(min_col-3, max_col + 4):
            value = code[get_code(grid, i, j, invert)]
            if value == '#':
                new_grid[(i, j)] = value
            else:
                new_grid[(i, j)] = '.'
    return (new_grid, min_row-3, max_row+3, min_col-3, max_col+3)


with open('input/day20.txt') as file:
    lines = file.readlines()
    codes = {}
    code = lines[0].strip()
    grid = {}
    min_row = 0
    max_row = len(lines) - 3
    min_col = 0
    max_col = len(lines[2]) - 2
    for l in range(len(code)):
        codes[l] = code[l]
    for l in range(len(lines)):
        if l < 2:
            continue
        line = list(lines[l])
        row = l - 2
        for col in range(len(line)):
            grid[(row,col)] = line[col]
    print_grid(grid, min_row, max_row, min_col, max_col)
    for i in range(50):
        print(f'Enhancing {i}')
        (grid, min_row, max_row, min_col, max_col) = enhance_grid(grid, codes, min_row, max_row, min_col, max_col, False if i % 2 == 0 else True)
        print_grid(grid, min_row, max_row, min_col, max_col)
