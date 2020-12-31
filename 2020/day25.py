def solve_loop_size(public_key):
    loop_size = 0
    subject_number = 7
    result = 1
    while result != public_key:
        result = result * subject_number % 20201227
        loop_size += 1
    return loop_size

def transform(subject_number, loop_size):
    result = 1
    for i in range(0, loop_size):
        result = result * subject_number % 20201227
    return result

with open('input/day25.txt') as file:
    lines = file.readlines();
    card_pub_key = int(lines[0])
    door_pub_key = int(lines[1])
    card_loop_size = solve_loop_size(card_pub_key)
    door_loop_size = solve_loop_size(door_pub_key)
    print(f'Card loop size = {card_loop_size}, door loop size = {door_loop_size}')
    encryption_key = transform(door_pub_key, card_loop_size)
    print(f'Encryption key = {encryption_key}')