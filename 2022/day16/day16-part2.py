#!/usr/bin/env python3

from itertools import chain, combinations
from re import match
from typing import Dict, List, Set, Tuple

def powerset(iterable):
    s = list(iterable)
    return chain.from_iterable(combinations(s, r) for r in range(len(s)+1))

class Valve:
    def __init__(self, name: str, flow_rate: int, tunnels: Set[str]):
        self.name = name
        self.flow_rate = flow_rate
        self.tunnels = tunnels
        self.distance: Dict[str, int] = {}
        for t in self.tunnels:
            self.distance[t] = 1
    def __str__(self):
        return f"Valve {self.name}, flow rate {self.flow_rate}, tunnels {self.tunnels}, distances {self.distance}"


def find_distances(valves):
    v_range = 2
    while sum([len(valves[v].distance) for v in valves]) < len(valves) ** 2 - len(valves):
        reachable_valves = list(valves.keys())
        new_distance: Dict[Tuple[str,str],int] = {}
        for v in reachable_valves:
            cur_distance = list(valves[v].distance.keys())
            for t in cur_distance:
                if valves[v].distance[t] == 1:
                    secondary_distance = list(valves[t].distance.keys())
                    for u in secondary_distance:
                        if u != valves[v].name and u not in valves[v].distance and valves[t].distance[u] + 1 == v_range:
                            new_distance[(v,u)] = v_range
        for k in new_distance:
            valves[k[0]].distance[k[1]] = v_range
        v_range += 1

def find_max_flow(valves: Dict[str, Valve], closed: Set[Valve], start_valve: str, minutes_left: int, path: List[str]) -> Tuple[int, List[str]]:
    if minutes_left <= 0 or len(closed) == 0:
        return 0, path
    my_location = valves[start_valve]
    my_flow = 0
    return_path = [my_location.name]
    if my_location.flow_rate > 0:
        my_flow = my_location.flow_rate * minutes_left
        closed.remove(my_location)
    max_downstream = 0
    for v in closed:
        if minutes_left - 1 > my_location.distance[v.name]:
            (flow_through_v, v_path) = find_max_flow(valves, closed, v.name, minutes_left - 1 - my_location.distance[v.name], return_path)
            if flow_through_v > max_downstream:
                max_downstream = flow_through_v
                return_path = [my_location.name]
                return_path.extend(v_path)

    closed.add(my_location)
    return (my_flow + max_downstream, return_path)




with open("input.txt") as file:
    lines = file.readlines()
    valves: Dict[str, Valve] = {}
    for line in lines:
        m = match(r"Valve (\w+) has flow rate=(\d+); tunnels? leads? to valves? (.+)", line)
        if m:
            valve_name = m.group(1)
            flow_rate = int(m.group(2))
            tunnels = set(m.group(3).split(", "))
            v = Valve(valve_name, flow_rate, tunnels)
            valves[valve_name] = v
    find_distances(valves)
    for v in valves:
        print(valves[v])

    valves_to_open = set([v for v in valves.values() if v.flow_rate > 0])
    my_sets = list(powerset(valves_to_open))
    max_flow = 0
    print(f"There are {len(my_sets)} sets for me to try.")
    for s in my_sets:
        elephant_set = set([v for v in valves_to_open if v not in s])
        (my_flow, my_path) = find_max_flow(valves, set(s), 'AA', 26, [])
        (e_flow, e_path) = find_max_flow(valves, elephant_set, 'AA', 26, [])
        flow = my_flow + e_flow
        if flow > max_flow:
            print(f"Me = {my_path}, Elephant = {e_path}, flow {my_flow} + {e_flow} = {flow}")
            max_flow = flow
    print(f"Max flow = {max_flow}")

        

