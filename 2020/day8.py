from copy import deepcopy

instructions = []

def run_program(inst_set):
    executed = {}
    accumulator = 0
    inst_pointer = 0
    change_candidates = []
    while inst_pointer not in executed and inst_pointer != len(inst_set):
        executed[inst_pointer] = True
        (op, value) = inst_set[inst_pointer]
        if op == "acc":
            accumulator += value
            inst_pointer += 1
        if op == "nop":
            change_candidates.append(inst_pointer)
            inst_pointer += 1
        if op == "jmp":
            change_candidates.append(inst_pointer)
            inst_pointer += value
    return accumulator, inst_pointer, change_candidates

with open("input/day8.txt") as file:
    lines = file.readlines()
    for line in lines:
        fields = line.split()
        instructions.append((fields[0], int(fields[1])))
    (accumulator, inst_pointer, change_candidates) = run_program(instructions)
    print(f'Accumulator = {accumulator}')

    for inst in change_candidates:
        new_inst = deepcopy(instructions)
        if new_inst[inst][0] == 'nop':
            new_inst[inst] = ('jmp', new_inst[inst][1])
        elif new_inst[inst][0] == 'jmp':
            new_inst[inst] = ('nop', new_inst[inst][1])
        (accumulator, inst_pointer, ignore) = run_program(new_inst)
        if inst_pointer == len(instructions):
            print(f'Program terminated successfully with accumulator {accumulator}')
            break
