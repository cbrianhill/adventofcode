
input_real = [7, 3, 9, 8, 6, 2, 5, 4, 1]
input_test = [3, 8, 9, 1, 2, 5, 4, 6, 7]
input = input_real

class Cup:
    def __init__(self, id):
        self.id = id
        self.left = None
        self.right = None


def find_destination_cup(current_cup, cups, first_removed, max_cup):
    desired_number = current_cup.id - 1
    found_cup = None
    while found_cup is None:
        if desired_number >= 1 and desired_number != first_removed.id and desired_number != first_removed.right.id and desired_number != first_removed.right.right.id:
            return cups[desired_number]
        desired_number -= 1
        if desired_number < 1:
            desired_number = max_cup


def play_game(input, turns, max_cup, trace=True):
    previous_cup = None
    first_cup = None
    cups = {}
    for i in input:
        c = Cup(i)
        cups[i] = c
        if previous_cup is not None:
            c.left = previous_cup
            previous_cup.right = c
        else:
            first_cup = c
        previous_cup = c
    c.right = first_cup
    first_cup.left = c
    current_cup = cups[input[0]]
    if trace:
        print(f'START: ({current_cup.id}) ', end='')
        x = current_cup.right
        while x != current_cup:
            print(f'{x.id} ', end='')
            x = x.right
        print('')
    for turn in range(0, turns):
        print(f'Running turn {turn}')
        # Remove three cups to the right of current one:
        first_removed = current_cup.right
        current_cup.right.right.right.right.left = current_cup
        current_cup.right = current_cup.right.right.right.right

        # Select destination cup, insert three cups to the right
        destination_cup = find_destination_cup(current_cup, cups, first_removed, max_cup)
        destination_cup.right.left = first_removed.right.right
        first_removed.right.right.right = destination_cup.right
        destination_cup.right = first_removed
        first_removed.left = destination_cup

        # Advance current cup one clockwise
        current_cup = current_cup.right
        if trace:
            print(f'After turn {turn}: ({current_cup.id}) ', end='')
            x = current_cup.right
            while x != current_cup:
                print(f'{x.id} ', end='')
                x = x.right
            print('')
    x = cups[1].right
    while x != cups[1]:
        print(x.id, end='')
        x = x.right
    print('\nDone')
    print(f'Part 2 answer = {cups[1].right.id * cups[1].right.right.id}')


play_game(input, 100, 9)
for i in range(10, 1000001):
    input.append(i)
play_game(input, 10000000, 1000000, trace=False)

