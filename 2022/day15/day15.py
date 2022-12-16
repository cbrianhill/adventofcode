#!/usr/bin/env python3

from re import match
import sys
from typing import Dict, Set, Tuple

def find_sensor_range_at(sensor: Tuple[int,int], beacon: Tuple[int,int], y: int) -> Set[int]:
    result = set()
    md = mhd(sensor, beacon)
    x_dist = md - abs(sensor[1]-y)
    if x_dist > 0:
        result.update(range(sensor[0], sensor[0]-1-x_dist, -1))
        result.update(range(sensor[0], sensor[0]+1+x_dist, 1))
    return result

def mhd(sensor: Tuple[int,int], beacon: Tuple[int,int]) -> int:
    return abs(sensor[0]-beacon[0]) + abs(sensor[1]-beacon[1])

def find_boundary(sensor: Tuple[int, int], beacon: Tuple[int, int]) -> Set[Tuple[int,int]]:
    boundary = set()
    md = mhd(sensor, beacon) + 1
    boundary.add((sensor[0],sensor[1] - md))
    for y in range(sensor[1] - md + 1, sensor[1] + md):
        delta_x = md - mhd(sensor, (sensor[0],y))
        boundary.add((sensor[0]-delta_x, y))
        boundary.add((sensor[0]+delta_x, y))
    boundary.add((sensor[0],sensor[1] + md))

    return boundary

def in_range(sensor: Tuple[int, int], beacon: Tuple[int, int], point: Tuple[int, int]) -> bool:

    return mhd(sensor, point) <= mhd(sensor, beacon)


with open("input.txt") as file:
    lines = file.readlines()
    sensors: Dict[Tuple[int,int], Tuple[int,int]] = {}
    for line in lines:
        m = match(r"^Sensor at x=(-?\d+), y=(-?\d+): closest beacon is at x=(-?\d+), y=(-?\d+)", line)
        if m:
            sensor = (int(m.group(1)), int(m.group(2)))
            beacon = (int(m.group(3)), int(m.group(4)))
            sensors[sensor] = beacon
    query = 2000000
    x_positions = set()
    for s in sensors:
        x_positions.update(find_sensor_range_at(s, sensors[s], query))
    for s in sensors:
        beacon = sensors[s]
        if beacon[1] == query and beacon[0] in x_positions:
            x_positions.remove(beacon[0])
    print(f"At y = {query} there cannot be beacons in {len(x_positions)} positions.")

    for s in sensors:
        print(f"Sensor at {s} has beacon at {sensors[s]} with Manhattan distance {mhd(s, sensors[s])}")

    boundary_points = set()
    intersecting = set()
    for s in sensors:
        print(f"Examining sensor {s}")
        boundary = find_boundary(s, sensors[s])
        if len(boundary_points) == 0:
            boundary_points = boundary
        else:
            boundary_points = boundary_points.intersection(boundary)
            if len(boundary_points) > 0:
                intersecting.update(boundary_points)
        print(f"Boundary points: ({len(boundary_points)})")
    print(f"There are {len(intersecting)} intersecting points")
    for p in intersecting:
        possible = True
        for s in sensors:
            if in_range(s, sensors[s], p):
                possible = False
                break
        if possible:
            print(f"Found distress signal at {p}")
            print(f"Tuning frequency = {4000000 * p[0] + p[1]}")

