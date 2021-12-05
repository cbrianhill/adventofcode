with open("input/day2.txt") as file:
    lines = file.readlines()
    depth = 0
    horiz = 0
    for line in lines:
        words = line.split(" ")
        if words[0] == "forward":
            horiz += int(words[1])
        elif words[0] == "up":
            depth -= int(words[1])
        elif words[0] == "down":
            depth += int(words[1])
    print(f"Depth = {depth}, Horizontal position = {horiz}, answer = {depth * horiz}")

    depth = 0
    horiz = 0
    aim = 0
    for line in lines:
        words = line.split(" ")
        if words[0] == "forward":
            horiz += int(words[1])
            depth += aim * int(words[1])
        elif words[0] == "up":
            aim -= int(words[1])
        elif words[0] == "down":
            aim += int(words[1])
    print(f"Depth = {depth}, Horizontal position = {horiz}, answer = {depth * horiz}")
