def count_adjacent(dimension, tuple):
    x = tuple[0]
    y = tuple[1]
    z = tuple[2]
    check_locations = []
    for i in range(x-1,x+2):
        for j in range(y-1,y+2):
            for k in range(z-1,z+2):
                if i != x or j != y or k != z:
                    check_locations.append((i,j,k))
    adjacent = 0
    for l in check_locations:
        if l in dimension:
            adjacent += 1
    return adjacent

def count_adjacent_w(dimension, tuple):
    x = tuple[0]
    y = tuple[1]
    z = tuple[2]
    w = tuple[3]
    check_locations = []
    for i in range(x-1,x+2):
        for j in range(y-1,y+2):
            for k in range(z-1,z+2):
                for l in range(w-1,w+2):
                    if i != x or j != y or k != z or l != w:
                        check_locations.append((i,j,k, l))
    adjacent = 0
    for l in check_locations:
        if l in dimension:
            adjacent += 1
    return adjacent

def iterate(dimension):
    max_x = 0
    min_x = 0
    max_y = 0
    min_y = 0
    max_z = 0
    min_z = 0
    for l in dimension:
        if l[0] > max_x:
            max_x = l[0]
        if l[0] < min_x:
            min_x = l[0]
        if l[1] > max_y:
            max_y = l[1]
        if l[1] < min_y:
            min_y = l[1]
        if l[2] > max_z:
            max_z = l[2]
        if l[2] < min_z:
            min_z = l[2]
    new_dimension = dimension.copy()
    for x in range(min_x - 1, max_x + 2):
        for y in range(min_y - 1, max_y + 2):
            for z in range(min_z - 1, max_z + 2):
                adjacent = count_adjacent(dimension, (x, y, z))
                if (x,y,z) in dimension:
                    # Location is active
                    if adjacent != 2 and adjacent != 3:
                        new_dimension.pop((x,y,z))
                else:
                    # Location is inactive
                    if adjacent == 3:
                        new_dimension[(x,y,z)] = '#'
    return new_dimension


def iterate_w(dimension):
    max_x = 0
    min_x = 0
    max_y = 0
    min_y = 0
    max_z = 0
    min_z = 0
    max_w = 0
    min_w = 0
    for l in dimension:
        if l[0] > max_x:
            max_x = l[0]
        if l[0] < min_x:
            min_x = l[0]
        if l[1] > max_y:
            max_y = l[1]
        if l[1] < min_y:
            min_y = l[1]
        if l[2] > max_z:
            max_z = l[2]
        if l[2] < min_z:
            min_z = l[2]
        if l[3] > max_w:
            max_w = l[3]
        if l[3] < min_w:
            min_w = l[3]
    new_dimension = dimension.copy()
    for x in range(min_x - 1, max_x + 2):
        for y in range(min_y - 1, max_y + 2):
            for z in range(min_z - 1, max_z + 2):
                for w in range(min_w - 1, max_w + 2):
                    adjacent = count_adjacent_w(dimension, (x, y, z, w))
                    if (x,y,z,w) in dimension:
                        # Location is active
                        if adjacent != 2 and adjacent != 3:
                            new_dimension.pop((x,y,z,w))
                    else:
                        # Location is inactive
                        if adjacent == 3:
                            new_dimension[(x,y,z,w)] = '#'
    return new_dimension

with open('input/day17.txt') as file:
    # (x, y, z) -> '#'
    dimension = {}
    w_dimension = {}
    z_coord = 0
    w_coord = 0
    y_coord = 0
    for line in file.readlines():
        x_coord = 0
        for c in line:
            if c == '#':
                dimension[(x_coord, y_coord, z_coord)] = c
                w_dimension[(x_coord, y_coord, z_coord, w_coord)] = c
            x_coord += 1
        y_coord += 1
    for i in range(0, 6):
        dimension = iterate(dimension)
        w_dimension = iterate_w(w_dimension)
    print(f'Answer = {len(dimension)}')
    print(f'Answer = {len(w_dimension)}')
