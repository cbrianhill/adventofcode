import sys
with open('input/day6.txt') as file:
    lines = file.readlines()
    timer_values = [int(x) for x in lines[0].strip().split(',')]
    fish_count = len(timer_values)
    spawn_days = {}
    for t in timer_values:
        if t in spawn_days:
            spawn_days[t] += 1
        else:
            spawn_days[t] = 1
    print(f'Initial state: {spawn_days}')
    num_days = int(sys.argv[1])
    for  d in range(0, num_days):
        if d in spawn_days:
            to_spawn = spawn_days[d]
            fish_count += to_spawn
            old_spawn_date = d + 7
            if old_spawn_date < num_days:
                if old_spawn_date in spawn_days:
                    spawn_days[old_spawn_date] += to_spawn
                else:
                    spawn_days[old_spawn_date] = to_spawn
            new_spawn_date = d + 9
            if new_spawn_date < num_days:
                if new_spawn_date in spawn_days:
                    spawn_days[new_spawn_date] += to_spawn
                else:
                    spawn_days[new_spawn_date] = to_spawn
        # print(f'After {d+1} days, there are {fish_count} fish.')
        # print(f'After {d+1} days, state = {spawn_days}')

    print(f'After {num_days} days, there are {fish_count} fish.')
