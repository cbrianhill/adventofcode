import re


class Instruction:
    def __init__(self, text):
        match = re.match('([NSEWLRF])(\d+)', text)
        if match:
            self.command = match[1]
            self.value = int(match[2])


def turn(direction, cur_direction):
    if direction == 'R':
        if cur_direction == 'N':
            return 'E'
        elif cur_direction == 'E':
            return 'S'
        elif cur_direction == 'S':
            return 'W'
        elif cur_direction == 'W':
            return 'N'
    elif direction == 'L':
        if cur_direction == 'N':
            return 'W'
        elif cur_direction == 'W':
            return 'S'
        elif cur_direction == 'S':
            return 'E'
        elif cur_direction == 'E':
            return 'N'
    return cur_direction


def turn_waypoint(direction, waypoint_pos):
    if direction == 'R':
        # (-1, 10) -> (10, 1) -> (1, -10) -> (-10, -1) -> (-1, 10)
        # ([1], -1 * [0])
        return waypoint_pos[1], waypoint_pos[0] * -1
    elif direction == 'L':
        # (-1, 10) ->  (-10, -1) -> (1, -10) -> (10, 1)
        # ([1] * -1, [0])
        return waypoint_pos[1] * -1, waypoint_pos[0]

with open('input/day12.txt') as file:
    lines = file.readlines()
    instructions = []
    for line in lines:
        ins = Instruction(line)
        instructions.append(ins)
    cur_pos = (0, 0)
    cur_direction = 'E'
    for i in instructions:
        if i.command == 'N':
            cur_pos = (cur_pos[0] - i.value, cur_pos[1])
        elif i.command == 'S':
            cur_pos = (cur_pos[0] + i.value, cur_pos[1])
        elif i.command == 'E':
            cur_pos = (cur_pos[0], cur_pos[1] + i.value)
        elif i.command == 'W':
            cur_pos = (cur_pos[0], cur_pos[1] - i.value)
        elif i.command == 'L' or i.command == 'R':
            turns = int(i.value / 90)
            for t in range(0, turns):
                cur_direction = turn(i.command, cur_direction)
        elif i.command == 'F':
            if cur_direction == 'N':
                cur_pos = (cur_pos[0] - i.value, cur_pos[1])
            elif cur_direction == 'S':
                cur_pos = (cur_pos[0] + i.value, cur_pos[1])
            elif cur_direction == 'E':
                cur_pos = (cur_pos[0], cur_pos[1] + i.value)
            elif cur_direction == 'W':
                cur_pos = (cur_pos[0], cur_pos[1] - i.value)
    manhattan_distance = abs(cur_pos[0]) + abs(cur_pos[1])
    print(f'Manhattan distance = {manhattan_distance}')

    cur_pos = (0, 0)
    waypoint_relative = (-1, 10)
    for i in instructions:
        if i.command == 'N':
            waypoint_relative = (waypoint_relative[0] - i.value, waypoint_relative[1])
        elif i.command == 'S':
            waypoint_relative = (waypoint_relative[0] + i.value, waypoint_relative[1])
        elif i.command == 'E':
            waypoint_relative = (waypoint_relative[0], waypoint_relative[1] + i.value)
        elif i.command == 'W':
            waypoint_relative = (waypoint_relative[0], waypoint_relative[1] - i.value)
        elif i.command == 'L' or i.command == 'R':
            turns = int(i.value / 90)
            for t in range(0, turns):
                waypoint_relative = turn_waypoint(i.command, waypoint_relative)
        elif i.command == 'F':
            for r in range(0, i.value):
                cur_pos = (cur_pos[0] + waypoint_relative[0], cur_pos[1] + waypoint_relative[1])
    manhattan_distance = abs(cur_pos[0]) + abs(cur_pos[1])
    print(f'Manhattan distance = {manhattan_distance}')

