with open('input/day7.txt') as file:
    lines = file.readlines()
    horizontal_positions = [int(x) for x in lines[0].strip().split(',')]
    min_position = min(horizontal_positions)
    max_position = max(horizontal_positions)
    min_fuel = None
    best_candidate = None

    for i in range(min_position, max_position + 1):
        fuel = 0
        for p in horizontal_positions:
            fuel += abs(p - i)
        if min_fuel is None or fuel < min_fuel:
            min_fuel = fuel
            best_candidate = i
    print(f'Best position = {best_candidate}, fuel = {min_fuel}')

    # 1 ->  1
    # 2 ->  3
    # 3 ->  6
    # 4 -> 10
    # 5 -> 15

    min_fuel = None
    best_candidate = None

    for i in range(min_position, max_position + 1):
        fuel = 0
        for p in horizontal_positions:
            distance = abs(p - i)
            fuel += sum(range(1, distance + 1))
        if min_fuel is None or fuel < min_fuel:
            min_fuel = fuel
            best_candidate = i
    print(f'Best position = {best_candidate}, fuel = {min_fuel}')
