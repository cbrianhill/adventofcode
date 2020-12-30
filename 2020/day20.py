import copy
import math
import re

class Tile:
    def __init__(self, id, data):
        self.id = id
        self.data = data
        self.orig_tile_id = id
        self.__reset()

    def __get_edge_number(self, edge):
        number = 0
        for x in range(0, len(edge)):
            if edge[x] == '#':
                number += 1 * 2 ** x
        return number

    def __reset(self):
        self.top_edge = self.__get_edge_number(self.data[0])
        self.bottom_edge = self.__get_edge_number(self.data[9])
        self.left_edge = self.__get_edge_number(''.join([s[0] for s in self.data]))
        self.right_edge = self.__get_edge_number(''.join([s[9] for s in self.data]))
        self.top_edge_count = 0
        self.bottom_edge_count = 0
        self.left_edge_count = 0
        self.right_edge_count = 0

    def fill_match_counts(self, edges):
        self.top_edge_count = len(edges[self.top_edge])
        self.bottom_edge_count = len(edges[self.bottom_edge])
        self.left_edge_count = len(edges[self.left_edge])
        self.right_edge_count = len(edges[self.right_edge])

    def flip_horizontal(self):
        for i in range(0, len(self.data)):
            self.data[i] = self.data[i][::-1]
        self.__reset()

    def flip_vertical(self):
        new_data = []
        for i in range(len(self.data) - 1, -1, -1):
            new_data.append(self.data[i])
        self.data = new_data
        self.__reset()

    def rotate(self):
        new_data = []
        for i in range(0, len(self.data)):
            new_data.append(''.join([self.data[len(self.data)-j-1][i] for j in range(0, len(self.data))]))
        self.data = new_data
        self.__reset()

    def get_matches(self):
        matches = 0
        if self.top_edge_count == 2:
            matches += 1
        if self.bottom_edge_count == 2:
            matches += 1
        if self.left_edge_count == 2:
            matches += 1
        if self.right_edge_count == 2:
            matches += 1
        return matches

    def get_image_row(self, row):
        return ''.join(self.data[row][1:-1])

    def print(self):
        print(f'Tile {self.id}:')
        for row in self.data:
            print(row)
        print('')


def add_to_edges(tile, edges):
    if tile.left_edge not in edges:
        edges[tile.left_edge] = []
    edges[tile.left_edge].append(tile)
    if tile.right_edge not in edges:
        edges[tile.right_edge] = []
    edges[tile.right_edge].append(tile)
    if tile.top_edge not in edges:
        edges[tile.top_edge] = []
    edges[tile.top_edge].append(tile)
    if tile.bottom_edge not in edges:
        edges[tile.bottom_edge] = []
    edges[tile.bottom_edge].append(tile)


def build_edge_list(tiles):
    edges = {}
    new_tiles = []
    for t in tiles.values():
        add_to_edges(t, edges)

        t_prime = copy.deepcopy(t)
        t_prime.flip_vertical()
        t_prime.id = t.id + 'v'
        new_tiles.append(t_prime)
        add_to_edges(t_prime, edges)

        t_prime = copy.deepcopy(t_prime)
        t_prime.rotate()
        t_prime.id = t.id + 'v1r'
        new_tiles.append(t_prime)
        add_to_edges(t_prime, edges)

        t_prime = copy.deepcopy(t_prime)
        t_prime.rotate()
        t_prime.rotate()
        t_prime.id = t.id + 'v3r'
        new_tiles.append(t_prime)
        add_to_edges(t_prime, edges)

        t_prime = copy.deepcopy(t)
        t_prime.flip_horizontal()
        t_prime.id = t.id + 'h'
        new_tiles.append(t_prime)
        add_to_edges(t_prime, edges)

        t_prime = copy.deepcopy(t_prime)
        t_prime.flip_vertical()
        t_prime.id = t.id + 'hv'
        new_tiles.append(t_prime)
        add_to_edges(t_prime, edges)

        t_prime = copy.deepcopy(t_prime)
        t_prime.rotate()
        t_prime.id = t.id + 'r3'
        new_tiles.append(t_prime)
        add_to_edges(t_prime, edges)

        t_prime = copy.deepcopy(t)
        t_prime.rotate()
        t_prime.id = t.id + 'r1'
        new_tiles.append(t_prime)
        add_to_edges(t_prime, edges)

    for t in new_tiles:
        tiles[t.id] = t
    return edges


def get_two_match_tiles(tiles, edges):
    tiles_with_two_matches = []
    for tile in tiles:
        t = tiles[tile]
        t.fill_match_counts(edges)
        if t.left_edge_count + t.top_edge_count + t.right_edge_count + t.bottom_edge_count == 6:
            tiles_with_two_matches.append(t)
    return tiles_with_two_matches

sea_monster_squares = [
    (0, 0),
    (1, 1),
    (1, 4),
    (0, 5),
    (0, 6),
    (1, 7),
    (1, 10),
    (0, 11),
    (0, 12),
    (1, 13),
    (1, 16),
    (0, 17),
    (-1,18),
    (0, 18),
    (0, 19)
]


def check_for_sea_monster(image, row, col):
    if row < 1 or row > len(image) - 2:
        return False
    if col > len(image[row]) - 20:
        return False
    for (row_offset, col_offset) in sea_monster_squares:
        c = image[row+row_offset][col+col_offset]
        if c != '#' and c != 'O':
            return False
    for (row_offset, col_offset) in sea_monster_squares:
        row_string = list(image[row+row_offset])
        row_string[col+col_offset] = 'O'
        image[row+row_offset] = ''.join(row_string)
    return True

def scan_for_monsters(combined_image):
    monsters_found = 0
    for r in range(0, len(combined_image)):
        for c in range(0, len(combined_image[r])):
            if check_for_sea_monster(combined_image, r, c):
                monsters_found += 1
    rough_waters = 0
    for r in range(0, len(combined_image)):
        for c in range(0, len(combined_image[r])):
            if combined_image[r][c] == '#':
                rough_waters += 1
    print(f'There are {monsters_found} monsters, {rough_waters} rough waters.')


def flip_horizontal(image):
    new_data = []
    for i in range(0, len(image)):
        new_data.append(image[i][::-1])
    return new_data


def flip_vertical(image):
    new_data = []
    for i in range(len(image) - 1, -1, -1):
        new_data.append(image[i])
    return new_data


def rotate(image):
    new_data = []
    for i in range(0, len(image)):
        new_data.append(''.join([image[len(image) - j - 1][i] for j in range(0, len(image))]))
    return new_data


def print_image(image):
    for i in image:
        print(i)


with open('input/day20.txt') as file:

    tiles = {}
    cur_tile_id = None
    cur_tile_data = None
    tile_count = 0
    for line in file.readlines():
        line = line.strip()
        m = re.match('^Tile (\d+):', line)
        if m:
            cur_tile_id = m[1]
            cur_tile_data = []
        elif len(line) == 0:
            tiles[cur_tile_id] = Tile(cur_tile_id, cur_tile_data)
            tile_count += 1
        else:
            cur_tile_data.append(line)
    tiles[cur_tile_id] = Tile(cur_tile_id, cur_tile_data)
    tile_count += 1
    edges = build_edge_list(tiles)
    unmatched_edges = {}
    for x in edges:
        e = edges[x]
        if len(e) == 4:
            unmatched_edges[e[0].id] = 1 if e[0].id not in unmatched_edges else unmatched_edges[e[0].id] + 1

    answer = 1
    for id in unmatched_edges:
        if unmatched_edges[id] == 2:
            print(f'{id} -> {unmatched_edges[id]}')
            answer = answer * int(id)
    print(f'Answer = {answer}')

    grid = []
    next_tile_possibilities = list(unmatched_edges.keys())
    first_tile_possibilities = []
    for x in next_tile_possibilities:
        if len(edges[tiles[x].top_edge]) == 4 and len(edges[tiles[x].left_edge]) == 4:
            first_tile_possibilities.append(tiles[x])
    for initial_tile in first_tile_possibilities:
        grid = []
        grid.append([])
        grid[0].append(initial_tile)
        used_tiles = [initial_tile.orig_tile_id]
        for row in range(0, int(math.sqrt(tile_count))):
            stop = False
            if row > 0:
                grid.append([])
            for col in range(0, int(math.sqrt(tile_count))):
                found = False
                if col > 0:
                    next_tile_possibilities = edges[grid[row][col-1].right_edge]
                    for t in next_tile_possibilities:
                        if t.left_edge == grid[row][col-1].right_edge and t.orig_tile_id not in used_tiles:
                            grid[row].append(t)
                            used_tiles.append(t.orig_tile_id)
                            found = True
                            break
                elif row > 0:
                    next_tile_possibilities = edges[grid[row-1][col].bottom_edge]
                    for t in next_tile_possibilities:
                        if t.top_edge == grid[row-1][col].bottom_edge and t.orig_tile_id not in used_tiles:
                            grid[row].append(t)
                            used_tiles.append(t.orig_tile_id)
                            found = True
                            break
                else:
                    found = True
                if not found:
                    print(f'Unable to find next tile at {row},{col}')
                    stop = True
                    break
            if stop:
                break
    print('Grid built.')
    for r in grid:
        for t in r:
            print(f'{t.id} ', end='')
        print('')
    combined_image = []
    for tile_row in range(0, len(grid)):
        for bit_row in range(1, 9):
            combined_image.append("")
            for tile in range(0, len(grid[tile_row])):
                combined_image[tile_row * 8 + (bit_row - 1)] += ''.join(grid[tile_row][tile].get_image_row(bit_row))
    print('Combined image built')

    images_to_scan = {}
    images_to_scan['original'] = combined_image
    images_to_scan['v'] = flip_vertical(combined_image)
    images_to_scan['h'] = flip_horizontal(combined_image)
    images_to_scan['hv'] = flip_horizontal(images_to_scan['v'])
    images_to_scan['1r'] = rotate(combined_image)
    images_to_scan['3r'] = rotate(rotate(images_to_scan['1r']))
    images_to_scan['v1r'] = rotate(images_to_scan['v'])
    images_to_scan['v3r'] = rotate(rotate(rotate(images_to_scan['v'])))
    for k in images_to_scan:
        print(f'Scanning image {k}:')
        # print_image(images_to_scan[k])
        scan_for_monsters(images_to_scan[k])
    print('Done.')
