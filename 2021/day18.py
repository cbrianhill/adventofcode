import re
import math
import typing
class Pair:
    def __init__(self):
        self.left: typing.Optional[Pair] = None
        self.right: typing.Optional[Pair] = None
        self.parent: typing.Optional[Pair] = None
    def set_left(self, left):
        self.left = left
    def set_right(self, right):
        self.right = right
    def set_parent(self, parent):
        self.parent = parent
    def get_left(self):
        return self.left
    def get_right(self):
        return self.right
    def to_string(self):
        val = '['
        if isinstance(self.left, int):
            val += str(self.left) + ','
        elif self.left is not None:
            val += self.left.to_string() + ','
        if isinstance(self.right, int):
            val += str(self.right)
        elif self.right is not None:
            val += self.right.to_string()
        val += ']'
        return val


def parse_pair(value):
    p = Pair()
    left_match = re.match(r'^\[(\d+),', value)
    if left_match:
        p.set_left(int(left_match.group(1)))
    else:
        p.set_left(parse_pair(value[1:find_right(value[1:])]))
        p.get_left().set_parent(p)
    right_match = re.match(r'.+,(\d+)]$', value)
    if right_match:
        p.set_right(int(right_match.group(1)))
    else:
        p.set_right(parse_pair(value[find_left(value[:-1]):-1]))
        p.get_right().set_parent(p)
    return p

def find_right(value):
    brackets = 0
    for i in range(0, len(value)):
        if value[i] == '[':
            brackets += 1
        if value[i] == ']':
            brackets -= 1
        if brackets == 0:
            return i+2

def find_left(value):
    brackets = 0
    for i in range(len(value) - 1, -1, -1):
        if value[i] == ']':
            brackets += 1
        if value[i] == '[':
            brackets -= 1
        if brackets == 0:
            return i

def needs_explode(value):
    brackets = 0
    for i in range(0, len(value)):
        if value[i] == '[':
            brackets += 1
        if value[i] == ']':
            brackets -= 1
        if brackets == 5:
            return i
    return -1

def needs_split(value):
    consec_ints = 0
    for i in range(0, len(value)):
        if value[i] >= '0' and value[i] <= '9':
            consec_ints += 1
        else:
            consec_ints = 0
        if consec_ints > 1:
            return i - 1
    return -1

def add_pairs(left_pair, right_pair):
    result = Pair()
    result.set_left(left_pair)
    result.set_right(right_pair)
    left_pair.set_parent(result)
    right_pair.set_parent(result)
    action_taken = True
    # print(f'Intermediate: {result.to_string()}')
    while action_taken:
        action_taken = find_and_explode(result)
        if not action_taken:
            action_taken = find_and_split(result)
        # if action_taken:
            # print(f'Intermediate: {result.to_string()}')
    return result

def split(value, parent):
    # print(f'Splitting {value}')
    result = Pair()
    result.set_left(math.floor(value / 2))
    result.set_right(math.ceil(value / 2))
    result.set_parent(parent)
    return result

def explode(pair):
    # print(f'Exploding {pair.to_string()}')
    add_left(pair.parent, pair.left, pair)
    add_right(pair.parent, pair.right, pair)
    return 0

def add_left(pair, value, start_node):
    if pair is None:
        return False
    # print(f'add_left({pair.to_string()})')
    if isinstance(pair.right, int) and start_node != pair.left and start_node != pair.right:
        pair.right += value
        return True
    if isinstance(pair.right, Pair) and start_node != pair.left and start_node != pair.right:
        left_tree = add_left(pair.right, value, start_node)
        if left_tree:
            return True
    if isinstance(pair.left, int):
        pair.left += value
        return True
    if isinstance(pair.left, Pair) and start_node != pair.left:
        left_tree = add_left(pair.left, value, start_node)
        if left_tree:
            return True
    return add_left(pair.parent, value, pair)

def add_right(pair, value, start_node):
    if pair is None:
        return False
    # print(f'add_right({pair.to_string()})')
    if isinstance(pair.left, int) and start_node != pair.right and start_node != pair.left:
        pair.left += value
        return True
    if isinstance(pair.left, Pair) and start_node != pair.right and start_node != pair.left:
        right_tree = add_right(pair.left, value, start_node)
        if right_tree:
            return True
    if isinstance(pair.right, int):
        pair.right += value
        return True
    if isinstance(pair.right, Pair) and start_node != pair.right:
        right_tree = add_right(pair.right, value, start_node)
        if right_tree:
            return True
    return add_right(pair.parent, value, pair)

def find_and_explode(pair, depth=0):
    if isinstance(pair, int):
        return False
    if depth > 2:
        if isinstance(pair.left, Pair):
            pair.set_left(explode(pair.left))
            return True
        if isinstance(pair.right, Pair):
            pair.set_right(explode(pair.right))
            return True
        return False
    if not find_and_explode(pair.left, depth+1):
        return find_and_explode(pair.right, depth+1)
    return True

def find_and_split(pair):
    if isinstance(pair.left, int):
        if pair.left > 9:
            pair.left = split(pair.left, pair)
            return True
    elif pair.left is not None:
        left_tree = find_and_split(pair.left)
        if left_tree:
            return True
    if isinstance(pair.right, int):
        if pair.right > 9:
            pair.right = split(pair.right, pair)
            return True
    elif pair.right is not None:
        right_tree = find_and_split(pair.right)
        if right_tree:
            return True
    return False

def find_magnitude(pair):
    if isinstance(pair, int):
        return pair
    return 3 * find_magnitude(pair.left) + 2 * find_magnitude(pair.right)


with open('input/day18.txt') as file:
    lines = file.readlines()
    pairs = []
    for l in lines:
        p = parse_pair(l.strip())
        print(f'Input: {p.to_string()}')
        pairs.append(p)
    current_value = pairs[0]
    for i in range(1, len(pairs)):
        print(f'Adding {current_value.to_string()} + {pairs[i].to_string()}')
        current_value = add_pairs(current_value, pairs[i])
        print(f'Result: {current_value.to_string()}')
        print(f'Magnitude: {find_magnitude(current_value)}')

    max_magnitude = 0
    for x in range(len(pairs)):
        for y in range(len(pairs)):
            if x == y:
                continue
            l_pair = parse_pair(lines[x].strip())
            r_pair = parse_pair(lines[y].strip())
            result = add_pairs(l_pair, r_pair)
            magnitude = find_magnitude(result)
            if magnitude > max_magnitude:
                max_magnitude = magnitude

    print(f'Max Magnitude = {max_magnitude}')
