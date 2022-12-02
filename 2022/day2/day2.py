#!/usr/bin/env python

from typing import Dict

code_1: Dict[str, str] = {
    "A": "rock",
    "B": "paper",
    "C": "scissors",
    "X": "rock",
    "Y": "paper",
    "Z": "scissors"
}

code_2: Dict[str, str] = {
    "X": "loss",
    "Y": "tie",
    "Z": "win"
}

wins: Dict[str, str] = {
    "rock":     "scissors",
    "paper":    "rock",
    "scissors": "paper"
}
losses: Dict[str, str] = { v: k for k, v in wins.items() }

outcomes: Dict[str, Dict[str, str]] = {
    "win":  losses,
    "loss": wins
}


scores: Dict[str, int] = {
    "rock":     1,
    "paper":    2,
    "scissors": 3,
    "win":      6,
    "loss":     0,
    "tie":      3
}

def who_won(elf_move: str, my_move: str) -> str:
    if my_move == elf_move:
        return "tie"
    elif elf_move == wins[my_move]:
        return "win"
    else:
        return "loss"

def what_move(elf_move: str, desired_outcome: str) -> str:
    if desired_outcome == "tie":
        return elf_move
    else:
        return outcomes[desired_outcome][elf_move]

with open ("input.txt") as file:
    lines = file.readlines()
    my_score_1 = 0
    my_score_2 = 0
    for line in lines:
        moves = line.split()
        elf_move = code_1[moves[0]]
        my_move_1 = code_1[moves[1]]
        my_score_1 += scores[my_move_1]
        outcome_1 = who_won(elf_move, my_move_1)
        my_score_1 += scores[outcome_1]
        print(f"Elf move (part 1): {elf_move}, My move: {my_move_1}, Outcome: {outcome_1}")
        desired_outcome = code_2[moves[1]]
        my_move_2 = what_move(elf_move, desired_outcome)
        outcome_2 = who_won(elf_move, my_move_2)
        my_score_2 += scores[my_move_2]
        my_score_2 += scores[outcome_2]
        print(f"Elf move (part 2): {elf_move}, My move: {my_move_2}, Desired Outcome: {desired_outcome} Outcome: {outcome_2}")

    print(f"Part 1: My total score is {my_score_1}")
    print(f"Part 2: My total score is {my_score_2}")
