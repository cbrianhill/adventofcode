#!/usr/bin/env python

with open("input.txt") as file:
    vals = []
    for line in file.readlines():
        vals.append([int(x) for x in line.strip().split()])
    list_one = [x[0] for x in vals]
    list_two = [x[1] for x in vals]
    list_one.sort()
    list_two.sort()
    diff_sum = 0
    for i in range(0, len(list_one)):
        diff_sum += abs(list_one[i] - list_two[i])
    print(diff_sum)
