import re
import numpy as np
import numpy.linalg as la
from typing import Optional

rotate_x = np.array([[1, 0, 0],[0, 0, -1],[0, 1, 0]], np.int32)
rotate_y = np.array([[0, 0, 1],[0, 1, 0],[-1, 0, 0]], np.int32)

def build_rotations():
    rotations = []
    rotations.append(la.matrix_power(rotate_x, 0))
    rotations.append(rotate_x)
    rotations.append(rotate_y)
    rotations.append(la.matrix_power(rotate_x, 2))
    rotations.append(np.matmul(rotate_x, rotate_y))
    rotations.append(np.matmul(rotate_y, rotate_x))
    rotations.append(la.matrix_power(rotate_y, 2))
    rotations.append(la.matrix_power(rotate_x, 3))
    rotations.append(np.matmul(la.matrix_power(rotate_x, 2), rotate_y))
    rotations.append(np.matmul(np.matmul(rotate_x, rotate_y), rotate_x))
    rotations.append(np.matmul(np.matmul(rotate_x, rotate_y), rotate_y))
    rotations.append(np.matmul(np.matmul(rotate_y, rotate_x), rotate_x))
    rotations.append(np.matmul(la.matrix_power(rotate_y, 2), rotate_x))
    rotations.append(la.matrix_power(rotate_y, 3))
    rotations.append(np.matmul(la.matrix_power(rotate_x, 3), rotate_y))
    rotations.append(np.matmul(np.matmul(la.matrix_power(rotate_x, 2), rotate_y), rotate_x))
    rotations.append(np.matmul(np.matmul(la.matrix_power(rotate_x, 2), rotate_y), rotate_y))
    rotations.append(np.matmul(np.matmul(np.matmul(rotate_x, rotate_y), rotate_x), rotate_x))
    rotations.append(np.matmul(np.matmul(np.matmul(rotate_x, rotate_y), rotate_y), rotate_y))
    rotations.append(np.matmul(np.matmul(np.matmul(rotate_y, rotate_x), rotate_x), rotate_x))
    rotations.append(np.matmul(la.matrix_power(rotate_y, 3), rotate_x))
    rotations.append(np.matmul(np.matmul(la.matrix_power(rotate_x, 3), rotate_y), rotate_x))
    rotations.append(np.matmul(np.matmul(np.matmul(np.matmul(rotate_x, rotate_y), rotate_x), rotate_x), rotate_x))
    rotations.append(np.matmul(np.matmul(np.matmul(np.matmul(rotate_x, rotate_y), rotate_y), rotate_y), rotate_x))
    return rotations

def rotate_grid(grid, transform):
    new_grid = []
    for point in grid:
        new_point = np.matmul(transform, point)
        new_grid.append(new_point)
    return new_grid


def check_align(aligned, offsets, candidate):
    distances = {}
    (o_x, o_y, o_z) = offsets
    for a in aligned:
        for b in candidate:
            x_dist = b.item(0) - (a.item(0) + o_x)
            y_dist = b.item(1) - (a.item(1) + o_y)
            z_dist = b.item(2) - (a.item(2) + o_z)
            if (x_dist, y_dist, z_dist) not in distances:
                distances[(x_dist, y_dist, z_dist)] = 1
            else:
                distances[(x_dist, y_dist, z_dist)] += 1
    for d in distances:
        if distances[d] > 11:
            (d_x, d_y, d_z) = d
            return (-1 * d_x, -1 * d_y, -1 * d_z)
    return None



with open("input/day19.txt") as file:
    lines = file.readlines()
    # grids = map(scannerId -> grid)
    grids = {}
    current_grid = []
    for line in lines:
        label_match = re.match(r'--- scanner (\d+) ---', line)
        if label_match:
            scanner_id = int(label_match.group(1))
            current_grid = []
            grids[scanner_id] = current_grid
        coord_match = re.match(r'(.+),(.+),(.+)', line)
        if coord_match:
            x = int(coord_match.group(1))
            y = int(coord_match.group(2))
            z = int(coord_match.group(3))
            current_grid.append(np.array([x,y,z], np.int32).T)
    transforms = build_rotations()
    # map(scannerId -> rotated grid)
    found = {}
    # map(scannerId -> offset)
    scanner_offsets = {}
    found[0] = grids[0]
    scanner_offsets[0] = (0,0,0)
    while len(found) < len(grids):
        for g in grids:
            if g not in found:
                this_grid = grids[g]
                match = False
                for t in transforms:
                    rotated = rotate_grid(this_grid, t)
                    for f in found:
                        result = check_align(found[f], scanner_offsets[f], rotated)
                        if result is not None:
                            print(f'Found match with known grid {f} and candidate grid {g}, offset {result}')
                            found[g] = rotated
                            scanner_offsets[g] = result
                            match = True
                            print(f'{scanner_offsets}')
                            break
                    if match:
                        break
    beacons = {}
    for f in found:
        (o_x, o_y, o_z) = scanner_offsets[f]
        for g in found[f]:
            x = g.item(0)
            y = g.item(1)
            z = g.item(2)

            beacons[(x + o_x, y + o_y, z + o_z)] = True
    print(f'There are {len(beacons)} beacons')

    max_distance = 0

    for i in range(len(scanner_offsets)):
        for j in range(len(scanner_offsets)):
            (i_x, i_y, i_z) = scanner_offsets[i]
            (j_x, j_y, j_z) = scanner_offsets[j]
            distance = abs(j_x - i_x) + abs(j_y - i_y) + abs(j_z - i_z)
            if distance > max_distance:
                max_distance = distance
        
    print(f'Max distance = {max_distance}')
