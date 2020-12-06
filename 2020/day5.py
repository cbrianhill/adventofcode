def find_seat_id(line):
    row = 0
    for i in range(0, 7):
        c = line[6-i]
        if c == 'B':
            row += 2 ** i
    seat = 0
    for j in range(0, 3):
        c = line[7+2-j]
        if c == 'R':
            seat += 2 ** j
    seat_id = row * 8 + seat
    print(f'{line} = row {row}, seat {seat}, seat id {seat_id}')
    return seat_id

with open("input/day5.txt") as file:
    lines = file.readlines()
    lines = [line.strip() for line in lines]
    highest_seat_id = 0
    seat_ids = []
    for line in lines:
        seat_id = find_seat_id(line)
        seat_ids.append(seat_id)
        if seat_id > highest_seat_id:
            highest_seat_id = seat_id
    print(f'Highest seat ID = {highest_seat_id}')
    seat_ids.sort()
    for index in range(0, len(seat_ids)-1):
        if seat_ids[index+1] - seat_ids[index] == 2:
            print(f'Found my seat, ID {seat_ids[index]+1}')
