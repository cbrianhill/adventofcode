#!/usr/bin/env python3
import ast
from enum import Enum
from functools import cmp_to_key

class Order(Enum):
    RIGHT = 1
    WRONG = 2
    SAME = 3

def eval(left, right) -> Order:
    if type(left) == int and type(right) == int:
        if left < right:
            return Order.RIGHT
        if right < left:
            return Order.WRONG
        else:
            return Order.SAME
    elif type(left) == list and type(right) == list:
        smaller_list = None
        if len(left) < len(right):
            smaller_list = left
        elif len(right) < len(left):
            smaller_list = right
        for i in range(0, min(len(left), len(right))):
            test = eval(left[i], right[i])
            if test != Order.SAME:
                return test
        if smaller_list is None:
            return Order.SAME
        return Order.RIGHT if smaller_list == left else Order.WRONG
    elif type(left) == list and type(right) == int:
        return eval(left, [right])
    elif type(left) == int and type(right) == list:
        return eval([left], right)
    return Order.SAME

def compare(a, b) -> int:
    o = eval(a,b)
    if o == Order.RIGHT:
        return -1
    if o == Order.WRONG:
        return 1
    return 0


with open("input.txt") as file:
    lines = file.readlines()
    left = None
    right = None
    pair = 1
    sum_inorder_indices = 0
    all_packets = []
    for line in lines:
        if left is None:
            left = ast.literal_eval(line)
            all_packets.append(left)
        elif right is None:
            right = ast.literal_eval(line)
            all_packets.append(right)
        else:
            if eval(left, right) == Order.RIGHT:
                sum_inorder_indices += pair
            left = None
            right = None
            pair += 1
    print(f"Sum of indices of in-order pairs = {sum_inorder_indices}")
    div_1 = [[2]]
    div_2 = [[6]]
    all_packets.append(div_1)
    all_packets.append(div_2)
    all_packets.sort(key=cmp_to_key(compare))
    div_1_index = None
    div_2_index = None
    for i in range(0, len(all_packets)):
        p = all_packets[i]
        if p == div_1:
            div_1_index = i + 1
        if p == div_2:
            div_2_index = i + 1
    print(f"Decoder key = {div_1_index * div_2_index}")


