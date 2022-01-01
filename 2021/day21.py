import re

class Die:
    def __init__(self):
        self.next_roll = 1
        self.rolls = 0
    def roll(self):
        self.rolls += 1
        this_roll = self.next_roll
        self.next_roll += 1
        if self.next_roll == 101:
            self.next_role = 1
        return this_roll
    def roll_count(self):
        return self.rolls

class Player:
    def __init__(self, start_location):
        self.score = 0
        self.location = start_location
    def turn(self, die):
        roll1 = die.roll()
        roll2 = die.roll()
        roll3 = die.roll()
        self.location = self.location + roll1 + roll2 + roll3
        self.location = self.location % 10
        if self.location == 0:
            self.location = 10
        self.score += self.location
        if self.score > 999:
            return True
        return False
    def get_score(self):
        return self.score

roll_freq = [ (3,1), (4,3), (5,6), (6,7), (7,6), (8,3), (9,1) ]

def sim_games(p1_position, p2_position, p1_score, p2_score, next_player):
    if p1_score > 20:
        return (1, 0)
    if p2_score > 20:
        return (0, 1)
    total_p1_wins = 0
    total_p2_wins = 0
    for (roll, freq) in roll_freq:
        if next_player == "p1":
            new_p1_pos = p1_position + roll
            new_p1_pos %= 10
            if new_p1_pos == 0:
                new_p1_pos = 10
            new_p1_score = p1_score + new_p1_pos
            (p1_wins, p2_wins) = sim_games(new_p1_pos, p2_position, new_p1_score, p2_score, "p2")
            total_p1_wins += p1_wins * freq
            total_p2_wins += p2_wins * freq
        elif next_player == "p2":
            new_p2_pos = p2_position + roll
            new_p2_pos %= 10
            if new_p2_pos == 0:
                new_p2_pos = 10
            new_p2_score = p2_score + new_p2_pos
            (p1_wins, p2_wins) = sim_games(p1_position, new_p2_pos, p1_score, new_p2_score, "p1")
            total_p1_wins += p1_wins * freq
            total_p2_wins += p2_wins * freq
    return (total_p1_wins, total_p2_wins)



with open('input/day21.txt') as file:
    lines = file.readlines()
    p1_start = 0
    p2_start = 0
    p1_match = re.match(r'Player 1 starting position: (\d+)', lines[0])
    if p1_match:
        p1_start = int(p1_match.group(1))
    p2_match = re.match(r'Player 2 starting position: (\d+)', lines[1])
    if p2_match:
        p2_start = int(p2_match.group(1))
    p1 = Player(p1_start)
    p2 = Player(p2_start)
    die = Die()
    no_winner = True
    p1_won = False
    p2_won = False
    while no_winner:
        p1_won = False
        p2_won = False
        p1_won = p1.turn(die)
        if not p1_won:
            p2_won = p2.turn(die)
        no_winner = not (p1_won or p2_won)
    losing_score = 0
    if p1_won:
        losing_score = p2.get_score()
    elif p2_won:
        losing_score = p1.get_score()
    print(f'Losing score = {losing_score}, die rolled {die.roll_count()} times, answer = {losing_score * die.roll_count()}')
    (p1_wins, p2_wins) = sim_games(p1_start, p2_start, 0, 0, "p1")
    print(f'P1 wins {p1_wins} times, P2 wins {p2_wins} times')



