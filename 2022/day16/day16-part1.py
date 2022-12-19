#!/usr/bin/env python3

from re import match
from typing import Dict, List, Set, Tuple

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
    print(f"At {start_valve} with {len(closed)} closed valves, {minutes_left} minutes left")
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
    (flow, path) = find_max_flow(valves, set(v for v in valves.values() if v.flow_rate > 0), 'AA', 30, [])
    print(f"Maximum flow = {flow}, {path}")
