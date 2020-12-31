import copy


class Tile:
    def __init__(self, row, col):
        self.flipped = False
        self.row = row
        self.col = col

    def flip(self):
        self.flipped = not self.flipped

odd_row_offsets = {
    'ne': (-1,1),
    'nw': (-1,0),
    'e': (0,1),
    'w': (0,-1),
    'se': (1,1),
    'sw': (1, 0)
}

even_row_offsets = {
    'ne': (-1,0),
    'nw': (-1,-1),
    'e': (0,1),
    'w': (0,-1),
    'se': (1,0),
    'sw': (1, -1)
}

# grid uses an odd-r layout with rows increasing as we go southward
def move(tile, direction, grid, tiles):
    cur_row = tile.row
    cur_col = tile.col
    new_row = cur_row
    new_col = cur_col
    if cur_row % 2:
        # odd row
        new_row += odd_row_offsets[direction][0]
        new_col += odd_row_offsets[direction][1]
    else:
        # even row
        new_row += even_row_offsets[direction][0]
        new_col += even_row_offsets[direction][1]
    if (new_row,new_col) not in grid:
        t = Tile(new_row, new_col)
        tiles.append(t)
        grid[(new_row, new_col)] = t
        return t
    else:
        return grid[(new_row, new_col)]


def count_adjacent_tiles(grid, tile, flipped):
    adjacent_matches = 0
    offsets = even_row_offsets
    if tile.row % 2:
        offsets = odd_row_offsets
    for o in offsets:
        this_row = tile.row + offsets[o][0]
        this_col = tile.col + offsets[o][1]
        if (this_row, this_col) in grid and grid[(this_row, this_col)].flipped == flipped:
            adjacent_matches += 1
    return adjacent_matches


def run_step(grid):
    new_grid = copy.deepcopy(grid)
    for coords in grid:
        tile = grid[coords]
        adjacent_black = count_adjacent_tiles(grid, tile, True)
        if tile.flipped:
            if adjacent_black == 0 or adjacent_black > 2:
                new_grid[coords].flipped = False
        else:
            if adjacent_black == 2:
                new_grid[coords].flipped = True
    return new_grid


def expand_grid(grid, tiles):
    min_row, max_row, min_col, max_col = 0, 0, 0, 0
    for (row, col) in grid:
        min_row = min(min_row, row)
        max_row = max(max_row, row)
        min_col = min(min_col, col)
        max_col = max(max_col, col)
    min_row -=1
    min_col -= 1
    max_row += 1
    max_col += 1
    for row in range(min_row, max_row + 1):
        for col in range(min_col, max_col + 1):
            if (row, col) not in grid.keys():
                tile = Tile(row, col)
                grid[(row, col)] = tile
                tiles.append(tile)
    return grid

with open('input/day24.txt') as file:
    tiles = []
    home_tile = Tile(0, 0)
    grid = {(0, 0): home_tile}
    for line in file.readlines():
        line = line.strip()
        cur_tile = home_tile
        cur_pos = 0
        while cur_pos < len(line):
            if line[cur_pos] == 'e':
                cur_tile = move(cur_tile, 'e', grid, tiles)
                cur_pos += 1
            elif line[cur_pos] == 'w':
                cur_tile = move(cur_tile, 'w', grid, tiles)
                cur_pos += 1
            elif line[cur_pos] == 'n' and line[cur_pos+1] == 'w':
                cur_tile = move(cur_tile, 'nw', grid, tiles)
                cur_pos += 2
            elif line[cur_pos] == 'n' and line[cur_pos+1] == 'e':
                cur_tile = move(cur_tile, 'ne', grid, tiles)
                cur_pos += 2
            elif line[cur_pos] == 's' and line[cur_pos+1] == 'w':
                cur_tile = move(cur_tile, 'sw', grid, tiles)
                cur_pos += 2
            elif line[cur_pos] == 's' and line[cur_pos+1] == 'e':
                cur_tile = move(cur_tile, 'se', grid, tiles)
                cur_pos += 2
        cur_tile.flip()
    black_side_up = 0
    for t in tiles:
        if t.flipped:
            black_side_up += 1
    print(f'{black_side_up} tiles are black side up.')

    for n in range(0, 100):
        grid = expand_grid(grid, tiles)
        grid = run_step(grid)
    black_side_up = 0
    for t in grid.values():
        if t.flipped:
            black_side_up += 1
    print(f'{black_side_up} tiles are black side up.')
