import re

def next_step(cur_x, cur_y, vel_x, vel_y):
    x_pos = cur_x + vel_x
    y_pos = cur_y + vel_y
    new_vel_x = vel_x
    if vel_x > 0:
        new_vel_x = vel_x - 1
    elif vel_x < 0:
        new_vel_x = vel_x + 1
    new_vel_y = vel_y - 1
    return (x_pos, y_pos, new_vel_x, new_vel_y)

def find_max_height(init_vel_x, init_vel_y, target_x_min, target_x_max, target_y_min, target_y_max):
    x_pos = 0
    y_pos = 0
    vel_x = init_vel_x
    vel_y = init_vel_y
    max_height = 0
    in_target_area = False
    x_stopped_short = False
    while y_pos >= target_y_min and x_pos <= target_x_max and not in_target_area and not x_stopped_short:
        (x_pos, y_pos, vel_x, vel_y) = next_step(x_pos, y_pos, vel_x, vel_y)
        if y_pos > max_height:
            max_height = y_pos
        if target_x_min <= x_pos <= target_x_max and target_y_min <= y_pos <= target_y_max:
            in_target_area = True
        if vel_x == 0 and x_pos < target_x_min:
            x_stopped_short = True
    return (in_target_area, max_height, x_stopped_short)

def search_max_height(target_x_min, target_x_max, target_y_min, target_y_max):
    max_height = 0
    target_hits = []

    for x in range(1, target_x_max * 2):
        for y in range(target_y_min, 5000):
            #print(f'Trying ({x},{y})')
            (in_target_area, this_height, x_stopped_short) = find_max_height(x, y, target_x_min, target_x_max, target_y_min, target_y_max)
            if in_target_area and this_height > max_height:
                print(f'New max height achieved: with velocity ({x},{y}): {this_height}')
                max_height = this_height
            if in_target_area:
                target_hits.append((x,y))
    return (max_height, target_hits)




with open('input/day17.txt') as file:
    lines = file.readlines()
    text = lines[0].strip()
    match = re.match(r'target area: x=(.+)\.\.(.+), y=(.+)\.\.(.+)', text)
    if match:
        target_x_min = int(match.group(1))
        target_x_max = int(match.group(2))
        target_y_min = int(match.group(3))
        target_y_max = int(match.group(4))
        print(f' x = {target_x_min} - {target_x_max}, y = {target_y_min} - {target_y_max}')

        (max_height, target_hits) = search_max_height(target_x_min, target_x_max, target_y_min, target_y_max)
        print(f'Max height = {max_height}.  Target hits = {len(target_hits)}')
