route_cache = {}


def find_paths_to_end(adapters, start_index):
    if len(adapters) - start_index == 1:
        return 1
    else:
        routes = 0
        for i in range(start_index + 1, len(adapters)):
            if adapters[i] - adapters[start_index] <= 3:
                if i in route_cache:
                    result = route_cache[i]
                else:
                    result = find_paths_to_end(adapters, i)
                    route_cache[i] = result
                routes += result
            else:
                break
        return routes

with open("input/day10.txt") as file:
    lines = file.readlines()
    adapters = [0]
    adapters += [int(line) for line in lines]
    sorted_adapters = adapters.copy()
    sorted_adapters.sort()
    prev_adapter = 0
    differences = {}
    for adapter in sorted_adapters:
        difference = adapter - prev_adapter
        if difference not in differences:
            differences[difference] = 1
        else:
            differences[difference] += 1
        prev_adapter = adapter
    # Add the difference between the final adapter and the device
    differences[3] += 1
    print(f'{differences[1]} 1-jolt differences, {differences[3]} 3-jolt differences, '
          f'answer = {differences[1] * differences[3]}')
    total_routes = find_paths_to_end(sorted_adapters, 0)
    print(f'There are {total_routes} total routes.')
