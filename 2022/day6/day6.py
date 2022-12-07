#!/usr/bin/env python

with open("input.txt") as file:
    l = file.readlines()[0]
    sop_found = False
    for i in range(3, len(l)):
        last4 = set(l[i-3:i+1])
        last14 = {}
        if i > 13:
            last14 = set(l[i-13:i+1])
        if len(last4) == 4 and not sop_found:
            sop_found = True
            print(f"start-of-packet marker ends at {i+1}")
        if len(last14) == 14:
            print(f"start-of-message marker ends at {i+1}")
            break


