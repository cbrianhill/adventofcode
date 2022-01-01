import re
from math import trunc

int_re = r'^[-+]?\d+$'

def p(value):
    return (value, value)

def run_instruction(ins, memory):
    (command, dest, arg) = ins
    if command == "add":
        if re.match(int_re, arg):
            memory[dest] = (memory[dest][0] + int(arg), memory[dest][1] + int(arg))
        else:
            memory[dest] = (memory[dest][0] + memory[arg][0], memory[dest][1] + memory[arg][1])
    elif command == "mul":
        if re.match(int_re, arg):
            parg = p(int(arg))
        else:
            parg = memory[arg]
        if memory[dest][0] >= 0 and parg[0] >= 0:
            memory[dest] = (memory[dest][0] * parg[0], memory[dest][1] * parg[1])
        else:
            bounds = [ax * bx for ax in memory[dest] for bx in parg]
            memory[dest] = (min(bounds), max(bounds))
    elif command == "div":
        if re.match(int_re, arg):
            memory[dest] = (trunc(memory[dest][0] / int(arg)), trunc(memory[dest][1] / int(arg)))
    elif command == "mod":
        if re.match(int_re, arg):
            (a, b) = memory[dest]
            if b-a +1 >= 26 or a % 26 > b % 26:
                memory[dest] = (0, 25)
            else:
                memory[dest] = (a % 26, b % 26)
    elif command == "eql":
        if re.match(int_re, arg):
            parg = p(int(arg))
        else:
            parg = memory[arg]
        (a, b) = memory[dest]
        if b < parg[0] or parg[1] < a:
            memory[dest] = (0, 0)
        elif a == b == parg[0] == parg[1]:
            memory[dest] = (1, 1)
        else:
            memory[dest] = (0, 1)
    # print(f'ins: {ins}, mem: {memory}')

def could_work(instructions, digits):
    # print(f'could_work({digits}')
    next_digit = 0
    memory = { 'w': p(0), 'x': p(0), 'y': p(0), 'z': p(0) }
    for i in instructions:
        (op, dest, arg) = i
        if op == 'inp':
            memory[dest] = digits[next_digit]
            # print(f'ins: {op} {dest}, mem: {memory}')
            next_digit += 1
        else:
            run_instruction(i, memory)
    # print(f'memory[z] = {memory["z"]}')
    return memory['z'][0] <= 0 and memory['z'][1] >= 0

def find_max(instructions, direction, prefix=()):
    print(f'find_max({prefix})')
    if direction > 0:
        start = 9
        stop = 0
        step = -1
    elif direction < 0:
        start = 1
        stop = 10
        step = 1
    for digit in range(start, stop, step):
        next_prefix = (*prefix, p(digit))
        if could_work(instructions, next_prefix + ((1,9),) * (14-len(next_prefix))):
            if len(next_prefix) == 14:
                return int("".join(str(a) for (a, b) in next_prefix))
            result = find_max(instructions, direction, next_prefix)
            if result == None:
                continue
            return result



with open('input/day24.txt') as file:
    lines = file.readlines()
    instructions = []
    for line in lines:
        line = line.strip()
        if match := re.match(f'([a-z]+) ([wxyz123456789-]+) ?(.+)?', line):
            instructions.append((match.group(1), match.group(2), match.group(3)))
    
    print(find_max(instructions, 1))
    print(find_max(instructions, -1))
