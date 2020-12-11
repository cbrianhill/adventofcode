def iterate(seats, check_function, threshold):
    new_seats = seats.copy()
    for row in range(0, len(seats)):
        for seat in range(0, len(seats[row])):
            if seats[row][seat] == 'L':
                adjacent_seats = check_function(seats, row, seat)
                if adjacent_seats == 0:
                    updated_row = list(new_seats[row])
                    updated_row[seat] = '#'
                    new_seats[row] = ''.join(updated_row)
            if seats[row][seat] == '#':
                adjacent_seats = check_function(seats, row, seat)
                if adjacent_seats >= threshold:
                    updated_row = list(new_seats[row])
                    updated_row[seat] = 'L'
                    new_seats[row] = ''.join(updated_row)
    return new_seats


def count_adjacent_occupied(seats, row, seat):
    occupied = 0
    if row > 0 and seats[row-1][seat] == '#':
        occupied += 1
    if row < len(seats) - 1 and seats[row+1][seat] == '#':
        occupied += 1
    if seat > 0 and seats[row][seat-1] == '#':
        occupied += 1
    if seat < len(seats[row]) - 1 and seats[row][seat+1] == '#':
        occupied += 1
    if row > 0 and seat > 0 and seats[row-1][seat-1] == '#':
        occupied += 1
    if row > 0 and seat < len(seats[row]) - 1 and seats[row-1][seat+1] == '#':
        occupied += 1
    if row < len(seats) - 1 and seat > 0 and seats[row+1][seat-1] == '#':
        occupied += 1
    if row < len(seats) - 1 and seat< len(seats[row]) - 1 and seats[row+1][seat+1] == '#':
        occupied += 1
    return occupied

def can_see_seat(seats, row, seat, row_inc, seat_inc):
    cur_row = row + row_inc
    cur_seat = seat + seat_inc
    while 0 <= cur_row < len(seats) and 0 <= cur_seat < len(seats[cur_row]):
        if seats[cur_row][cur_seat] == '#':
            return True
        elif seats[cur_row][cur_seat] == 'L':
            return False
        cur_row = cur_row + row_inc
        cur_seat = cur_seat + seat_inc
    return False


def count_visible_occupied(seats, row, seat):
    occupied = 0
    if can_see_seat(seats, row, seat, 1, 0): occupied += 1
    if can_see_seat(seats, row, seat, 0, 1): occupied += 1
    if can_see_seat(seats, row, seat, -1, 0): occupied += 1
    if can_see_seat(seats, row, seat, 0, -1): occupied += 1
    if can_see_seat(seats, row, seat, 1, 1): occupied += 1
    if can_see_seat(seats, row, seat, 1, -1): occupied += 1
    if can_see_seat(seats, row, seat, -1, 1): occupied += 1
    if can_see_seat(seats, row, seat, -1, -1): occupied += 1
    return occupied

def count_total_occupied(seats):
    occupied = 0
    for row in seats:
        for seat in row:
            if seat == '#':
                occupied += 1
    return occupied

def print_chart(seats):
    for line in seats:
        print(line)


with open("input/day11.txt") as file:
    lines = file.readlines()
    for (func, threshold) in [(count_adjacent_occupied, 4), (count_visible_occupied, 5)]:
        seats = [line.strip() for line in lines]
        new_seats = []
        step = 0
        while new_seats != seats:
            old_seats = seats
            new_seats = iterate(seats, func, threshold)
            step += 1
            # print(f'Step {step} completed.')
            # print_chart(new_seats)
            seats = new_seats
            new_seats = old_seats
        occupied = count_total_occupied(new_seats)
        print(f'There are {occupied} seats occupied.')