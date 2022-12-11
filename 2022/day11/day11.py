#!/usr/bin/env python3

from __future__ import annotations
from collections import deque
from copy import deepcopy
from re import match
from typing import Callable, Dict, List, Optional


header_regex = r"^Monkey (\d+):"
items_regex = r"^  Starting items: ([\d, ]+)"
op_regex = r"^  Operation: new = old (.) (.+)"
test_regex = r"^  Test: divisible by (\d+)"
true_regex = r"^    If true: throw to monkey (\d+)"
false_regex = r"^    If false: throw to monkey (\d+)"

def mult_relief(a: int, b: int) -> int:
    return a * b

def add_relief(a: int, b: int) -> int:
    return a + b

def power_relief(a: int, b: int) -> int:
    return a ** b

ops_relief: Dict[str, Callable[[int, int], int]] = {
    "*": mult_relief,
    "+": add_relief,
    "^": power_relief
}

def mult(a: Dict[int, int], b: int) -> Dict[int, int]:
    result = {}
    for m in a:
        result[m] = (a[m] * b ) % m
    return result

def add(a: Dict[int, int], b: int) -> Dict[int, int]:
    result = {}
    for m in a:
        result[m] = (a[m] + b) % m
    return result

def power(a: Dict[int, int], _: int) -> Dict[int, int]:
    result = {}
    for m in a:
        result[m] = (a[m] * a[m]) % m
    return result

ops: Dict[str, Callable[[Dict[int, int], int], Dict[int, int]]] = {
    "*": mult,
    "+": add,
    "^": power
}

def is_divisible(mods: Dict[int, int], divisor: int) -> bool:
    if mods[divisor] == 0:
            return True
    return False

def is_divisible_relief(value: int, divisor: int) -> bool:
    if value % divisor == 0:
            return True
    return False

class Monkey:
    def __init__(self, id):
        self.id = id
        self.items: deque = deque([])
        self.op = ""
        self.param = 0
        self.divisible_by = 1
        self.true_monkey = id
        self.false_monkey = id
        self.inspected = 0
        self.relief = True

    def setItems(self, items: List[int], modulos: List[int]):
        if self.relief:
            self.items.extend(items)
        else:
            for i in items:
                item_mods = {}
                for m in modulos:
                    item_mods[m] = i % m
                self.items.append(item_mods)

    def setOp(self, op):
        self.op = op

    def setParam(self, param):
        self.param = param

    def setDivisibleBy(self, divisible_by):
        self.divisible_by = divisible_by

    def setTrueMonkey(self, true_monkey):
        self.true_monkey = true_monkey

    def setFalseMonkey(self, false_monkey):
        self.false_monkey = false_monkey

    def inspect(self):
        self.inspected += 1

    def getInspected(self):
        return self.inspected

    def getId(self):
        return self.id

    def getDivisibleBy(self):
        return self.divisible_by

    def setRelief(self, relief: bool):
        self.relief = relief

    def __str__(self):
        output = f"Monkey {self.id}:\n"
        output += f" Items: {self.items}\n"
        output += f" Op: {self.op}\n"
        output += f" Param: {self.param}\n"
        output += f" Divisor: {self.divisible_by}\n"
        output += f" True: {self.true_monkey}\n"
        output += f" False: {self.false_monkey}\n"
        output += f" Inspected: {self.inspected}\n"
        output += f" Relief: {self.relief}\n"
        return output

    def turn(self, monkeys: Dict[int, Monkey]):
        while len(self.items) > 0:
            i = self.items.popleft()
            self.inspect()
            if self.relief:
                worry = ops_relief[self.op](i, self.param)
                worry = int(worry / 3)
                if is_divisible_relief(worry, self.divisible_by):
                    monkeys[self.true_monkey].items.append(worry)
                else:
                    monkeys[self.false_monkey].items.append(worry)
            else:
                worry = ops[self.op](i, self.param)
                if is_divisible(worry, self.divisible_by):
                    monkeys[self.true_monkey].items.append(worry)
                else:
                    monkeys[self.false_monkey].items.append(worry)

def sim(rounds: int, monkeys: Dict[int, Monkey]) -> int:
    for _ in range(0, rounds):
        for monkey in range(0, len(monkeys)):
            monkeys[monkey].turn(monkeys)
    l = list(monkeys.values())
    l.sort(reverse=True, key=Monkey.getInspected)
    return l[0].getInspected() * l[1].getInspected()

with open("input.txt") as file:
    lines = file.readlines()
    monkeys: Dict[int, Monkey] = {}
    cur_monkey: Optional[Monkey] = None
    item_worries: Dict[int, List[int]] = {}
    for line in lines:
        m = match(header_regex, line)
        if m:
            id = int(m.group(1))
            cur_monkey = Monkey(id)
            monkeys[id] = cur_monkey
            continue
        m = match(items_regex, line)
        if m and cur_monkey:
            items = [int(x) for x in m.group(1).split(", ")]
            item_worries[cur_monkey.getId()] = items
            continue
        m = match(op_regex, line)
        if m and cur_monkey:
            op = m.group(1)
            param = m.group(2)
            if op == "*" and param == "old":
                op = "^"
                param = 2
            cur_monkey.setOp(op)
            cur_monkey.setParam(int(param))
            continue
        m = match(test_regex, line)
        if m and cur_monkey:
            divisible_by = int(m.group(1))
            cur_monkey.setDivisibleBy(divisible_by)
            continue
        m = match(true_regex, line)
        if m and cur_monkey:
            true_monkey = int(m.group(1))
            cur_monkey.setTrueMonkey(true_monkey)
            continue
        m = match(false_regex, line)
        if m and cur_monkey:
            false_monkey = int(m.group(1))
            cur_monkey.setFalseMonkey(false_monkey)
            continue
    monkeys2 = deepcopy(monkeys)

    modulos = [x.getDivisibleBy() for x in monkeys.values()]
    for m in monkeys:
        monkeys[m].setItems(item_worries[m], modulos)
        print(monkeys[m])

    for m in monkeys2:
        monkeys2[m].setRelief(False)
        monkeys2[m].setItems(item_worries[m], modulos)
        print(monkeys2[m])

    part1_monkey_business = sim(20, monkeys)
    print(f"Part 1 Monkey business level = {part1_monkey_business}")
    part2_monkey_business = sim(10000, monkeys2)
    print(f"Part 2 Monkey business level = {part2_monkey_business}")

