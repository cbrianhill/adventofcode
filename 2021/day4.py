import re
def make_board(lines, start):
    grid = []
    for i in range(0, 5):
        gridline = []
        fileline = re.split(r'\s+', lines[start+i].strip())
        for j in range(0, 5):
            gridline.append(int(fileline[j]))
        grid.append(gridline)
    return grid

def print_board(board):
    template = "{0:2} {1:2} {2:2} {3:2} {4:2}"
    for line in board:
        print(template.format(*line))

def mark_board(board, mark, num):
    for row in range(0, 5):
        for col in range(0, 5):
            if board[row][col] == num:
                mark[row][col] = 1

def check_marks(mark):
    for row in range(0, 5):
        matches = 0
        for col in range(0, 5):
            if mark[row][col] == 1:
                matches += 1
        if matches == 5:
            return True
    for col in range(0, 5):
        matches = 0
        for row in range(0, 5):
            if mark[row][col] == 1:
                matches += 1
        if matches == 5:
            return True
    return False

def calc_score(board, mark, num):
    base_score = 0
    for row in range(0, 5):
        for col in range(0, 5):
            if mark[row][col] == 0:
                base_score += board[row][col]
    return base_score * num


with open('input/day4.txt') as file:
    lines = file.readlines()
    numbers_selected = [int(x) for x in lines[0].strip().split(',')]

    boards = []
    marks = []

    starting_line = 2
    while starting_line < len(lines):
        boards.append(make_board(lines, starting_line))
        marks.append([[0 for col in range(0, 5)] for row in range(0, 5)])
        starting_line += 6

    print(f'Numbers = {numbers_selected}')
    for b in range(0, len(boards)):
        print(f'Board {b}:')
        print_board(boards[b])
        print('')
    
    winners = 0
    winning_boards = []
    winning_numbers = []
    for n in range(0, len(numbers_selected)):
        number = numbers_selected[n]
        print(f'\n*** Number {number}')
        for b in range(0, len(boards)):
            if b in winning_boards:
                continue
            mark_board(boards[b], marks[b], number)
            if check_marks(marks[b]):
                print(f'Board {b} has won.  Score = {calc_score(boards[b], marks[b], number)}')
                winners += 1
                winning_boards.append(b)
                winning_numbers.append(number)
        if len(winning_boards) == len(boards) or n == len(numbers_selected) - 1:
            break
    last_winner = winning_boards[-1]
    print(f'Last winning board = {last_winner} with a score of {calc_score(boards[last_winner], marks[last_winner],winning_numbers[-1])}')
