import re

def apply_mask(mask, input):
    input_b = format(input, '036b')
    output = []
    for i in range(0,36):
        if mask[i] == '0':
            output.append('0')
        elif mask[i] == '1':
            output.append('1')
        else:
            output.append(input_b[i])
    return int(''.join(output), 2)


def has_floaters(number_list):
    for entry in number_list:
        if 'X' in entry:
            return True
    return False


def apply_mask_2(mask, input):
    input_b = format(input, '036b')
    initial_output = []
    for i in range(0,36):
        if mask[i] == '0':
            initial_output.append(input_b[i])
        elif mask[i] == '1':
            initial_output.append('1')
        else:
            initial_output.append('X')
    output = [''.join(initial_output)]
    while has_floaters(output):
        entry = output.pop(0)
        floater_location = entry.find('X')
        output.append(entry[:floater_location] + '0' + entry[floater_location+1:])
        output.append(entry[:floater_location] + '1' + entry[floater_location+1:])
    final_output = [int(''.join(entry), 2) for entry in output]
    return final_output

with open("input/day14.txt") as file:
    lines = file.readlines()
    mask = 'XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX'
    memory = {}
    for line in lines:
        match = re.match('^mask = (.+)$', line)
        if match:
            mask = match[1]
        else:
            match = re.match('^mem\[(\d+)\] = (\d+)', line)
            memory[match[1]] = apply_mask(mask, int(match[2]))
    sum = 0
    for address in memory:
        sum += memory[address]
    print(f'Sum = {sum}')

    memory = {}
    for line in lines:
        match = re.match('^mask = (.+)$', line)
        if match:
            mask = match[1]
        else:
            match = re.match('^mem\[(\d+)\] = (\d+)', line)
            memory_locations = apply_mask_2(mask, int(match[1]))
            for loc in memory_locations:
                memory[loc] = int(match[2])
    sum = 0
    for address in memory:
        sum += memory[address]
    print(f'Sum = {sum}')
