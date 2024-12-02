#!/usr/bin/env python

with open("input.txt") as file:
    vals = []
    for line in file.readlines():
        vals.append([int(x) for x in line.strip().split()])
    list_one = [x[0] for x in vals]
    list_two = [x[1] for x in vals]
    list_two_freq = {}
    for i in list_two:
        if i not in list_two_freq:
            list_two_freq[i] = 1
        else:
            list_two_freq[i] = list_two_freq[i] + 1
    similarity_score = 0
    for i in list_one:
        similarity_score += i * list_two_freq[i] if i in list_two_freq else 0
    print(similarity_score)
