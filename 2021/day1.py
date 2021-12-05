with open("input/day1.txt") as file:
    lines = file.readlines()
    entries = [int(line.strip()) for line in lines]
    previous = entries[0]
    increases = 0
    for e1 in entries:
        if e1 > previous:
            increases += 1
        previous = e1
    print(f'Number of depth increases: {increases}')

    previous = entries[0] + entries[1] + entries[2]
    increases = 0
    for index in range(2, len(entries)):
        value = entries[index] + entries[index-1] + entries[index-2]
        if value > previous:
            increases += 1
        previous = value
    print(f'Number of depth increases: {increases}')

