def check_candidate(buses, last_bus_pos, time):
    for i in range(0, last_bus_pos):
        bus_number = int(buses[i])
        candidate_time_valid = time % bus_number == i
        if not candidate_time_valid:
            return False
    return True

with open('input/day13.txt') as file:
    lines = file.readlines();
    current_time = int(lines[0])
    buses = lines[1].split(',')
    earliest_stop = current_time * 2
    best_bus = -1
    for bus in buses:
        if bus != 'x':
            bus_number = int(bus)
            next_stop = current_time + bus_number - (current_time % bus_number)
            if next_stop < earliest_stop:
                earliest_stop = next_stop
                best_bus = bus_number
    print(f'Answer = {best_bus * (earliest_stop - current_time)}')

    increment = 1
    candidate_time = 0
    bus_index = 0
    while bus_index < len(buses):
        if buses[bus_index] == 'x':
            bus_index += 1
            continue
        candidate_time += increment
        print(f'Trying {candidate_time}')
        bus_number = int(buses[bus_index])
        if (candidate_time + bus_index) % bus_number == 0:
            print(f'Found match for bus {bus_index}: {candidate_time}')
            increment = increment * bus_number
            bus_index += 1
    print(f'Answer = {candidate_time}')