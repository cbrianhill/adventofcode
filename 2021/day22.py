import re
from collections import Counter

class Cube:
    def __init__(self, min_x, max_x, min_y, max_y, min_z, max_z, switch):
        self.switch = switch
        self.min_x = min_x
        self.max_x = max_x
        self.min_y = min_y
        self.max_y = max_y
        self.min_z = min_z
        self.max_z = max_z

    def find_volume(self):
        return (self.max_x - self.min_x + 1) * (self.max_y - self.min_y + 1) * (self.max_z - self.min_z + 1)

    def find_intersection(self, other):
        i = Cube(max(self.min_x, other.min_x),
                 min(self.max_x, other.max_x),
                 max(self.min_y, other.min_y),
                 min(self.max_y, other.max_y),
                 max(self.min_z, other.min_z),
                 min(self.max_z, other.max_z),
                 other.switch)
        if i.min_x <= i.max_x and i.min_y <= i.max_y and i.min_z <= i.max_z:
            return i



class Instruction:
    def __init__(self, line):
        match = re.match(f'(on|off) x=(.+)\\.\\.(.+),y=(.+)\\.\\.(.+),z=(.+)\\.\\.(.+)', line)
        if match:
            self.line = line
            self.switch = match.group(1)
            self.min_x = int(match.group(2))
            self.max_x = int(match.group(3))
            self.min_y = int(match.group(4))
            self.max_y = int(match.group(5))
            self.min_z = int(match.group(6))
            self.max_z = int(match.group(7))
    def apply(self):
        c = Cube(self.min_x, self.max_x, self.min_y, self.max_y, self.min_z, self.max_z, self.switch)
        return c
        

with open('input/day22.txt') as file:
    lines = file.readlines()
    instructions = []
    grid = {}
    for line in lines:
        instructions.append(Instruction(line))
    cubes = Counter()
    for i in instructions:
        print(i.line)
        # Uncomment to do part 1.
        # if i.min_x < -50 or i.max_x > 50 or i.min_y < -50 or i.max_y > 50 or i.min_z < -50 or i.max_z > 50:
            # continue
        new = Counter()
        new_cube = i.apply()
        for c, count in cubes.items():
            if intersect := c.find_intersection(new_cube):
                new[intersect] -= count
        cubes.update(new)

        if i.switch == 'on':
            cubes[new_cube] += 1
    total_cubes = sum(c.find_volume() * count for c, count in cubes.items())
    print(f'{total_cubes}')


