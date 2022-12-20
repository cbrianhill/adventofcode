#!/usr/bin/env python3

from typing import Dict, List, Tuple

# mask[0] is the bottom of the piece
class Piece:
    def __init__(self, name: str, mask: List[List[str]]):
        self.mask: List[List[str]] = mask

h_line = Piece("h_line", [["@","@","@","@"]])
plus   = Piece("plus",   [[".","@","."],
                          ["@","@","@"],
                          [".","@","."]])
corner = Piece("corner", [["@","@","@"],
                          [".",".","@"],
                          [".",".","@"]])
v_line = Piece("v_line", [["@"],
                          ["@"],
                          ["@"],
                          ["@"]])
box    = Piece("box",    [["@","@"],
                          ["@","@"]])

pieces: List[Piece] = [h_line, plus, corner, v_line, box]

class Board:
    def __init__(self, jets: str):
        self.height = 0
        # (row, col) of bottom left of piece -> piece mask
        self.piece_positions: Dict[Tuple[int,int], Piece] = {}
        self.jets = jets
        self.jet_index = 0

    def display(self):
        grid = [["|",".",".",".",".",".",".",".","|"] for _ in range(0, self.height + 10)]
        for p in self.piece_positions:
            (row, col) = p
            piece = self.piece_positions[p]
            for r in range(0, len(piece.mask)):
                for c in range(0, len(piece.mask[r])):
                    if piece.mask[r][c] == "@":
                        grid[row+r][col+c+1] = "@"
        for r in range(len(grid)-1, -1, -1):
            print("".join(grid[r]))
        print("+-------+")
        print(f"Height = {self.height}")

    def drop_piece(self, piece) -> Tuple[int,int,int]:
        position = (self.height + 3, 2)
        # print(f"Start position = {position}")
        stuck = False
        while not stuck:
            position = self.apply_jet(piece, position)
            down = (position[0] - 1, position[1])
            if self.can_fit(piece, down):
                # print(f"Moving down to {down}")
                position = down
            else:
                stuck = True
        # print(f"Final position = {position}")
        self.piece_positions[position] = piece
        top_of_piece_height = position[0] + len(piece.mask)
        if top_of_piece_height > self.height:
            # print(f"New height = {top_of_piece_height}")
            self.height = top_of_piece_height
        obsolete_positions = filter(lambda p: p[0] < position[0] - 100, list(self.piece_positions.keys()))
        for p in obsolete_positions:
            del self.piece_positions[p]
        return position[1], self.jet_index % len(self.jets), self.height

    def apply_jet(self, piece, position: Tuple[int,int]) -> Tuple[int,int]:
        this_jet = self.jets[self.jet_index % len(self.jets)]
        # print(f"Applying {this_jet}")
        proposed_position = position
        if this_jet == ">":
            proposed_position = (position[0], position[1]+1)
        else:
            proposed_position = (position[0], position[1]-1)
        if not self.can_fit(piece, proposed_position):
            proposed_position = position
        self.jet_index += 1
        # print(f"New position = {proposed_position}")
        return proposed_position

    def can_fit(self, piece, position) -> bool:
        if position[1] < 0:
            # print("Left boundary collision")
            return False
        if position[1] + len(piece.mask[0]) -1 > 6:
            # print("Right boundary collision")
            return False
        if position[0] < 0:
            # print("Bottom boundary collision")
            return False
        for p in self.piece_positions:
            if self.overlaps(p, self.piece_positions[p], position, piece):
                # print("Overlap with piece at {p}")
                return False
        return True

    def overlaps(self, a_position, a_piece, b_position, b_piece) -> bool:
        if abs(a_position[0] - b_position[0]) > 4:
            return False
        a_points = set([])
        for r in range(0, len(a_piece.mask)):
            for c in range(0, len(a_piece.mask[0])):
                if a_piece.mask[r][c] == "@":
                    a_points.add((a_position[0] + r, a_position[1] + c))
        b_points = set([])
        for r in range(0, len(b_piece.mask)):
            for c in range(0, len(b_piece.mask[0])):
                if b_piece.mask[r][c] == "@":
                    b_points.add((b_position[0] + r, b_position[1] + c))
        return len(a_points.intersection(b_points)) > 0


with open("input.txt") as file:
    lines = file.readlines()
    jets = lines[0].strip()
    b = Board(jets)
    i = 0
    stop = False
    print(f"There are {len(b.jets)} jets")
    for i in range(0, 2022):
        b.drop_piece(pieces[i % len(pieces)])
    print(f"Height = {b.height}")
    b = Board(jets)
    i = 0
    heights: Dict[int,int] = {}
    cycles: Dict[Tuple[int,int,int],Tuple[int,int]] = {}
    while not stop:
        (h_pos, jet, height) = b.drop_piece(pieces[i % len(pieces)])
        heights[i] = height
        if (i % len(pieces), h_pos, jet) in cycles and i > 10000:
            print(f"At turn {i}, found piece {i % len(pieces)} with h_pos {h_pos} and jet {jet}, height {height} in history.")
            (h, prev_turn) = cycles[(i % len(pieces), h_pos, jet)]
            print(f"previous turn {prev_turn}, previous height {h}")
            cycle_length = i - prev_turn
            cycle_height = height - h
            print(f"Cycle length = {cycle_length}, cycle height = {cycle_height}")
            num_cycles = int((999999999999 - i) / cycle_length)
            print(f"Full cycles = {num_cycles}")
            height_at_cycle_end = height + num_cycles * cycle_height
            remaining_pieces = (999999999999 - i) % cycle_length
            extra_height = heights[prev_turn + remaining_pieces] - heights[prev_turn]
            final_height = height_at_cycle_end + extra_height
            print(f"Height at cycle end = {height_at_cycle_end}")
            print(f"Remaining pieces = {remaining_pieces}")
            print(f"Remaining hight = {extra_height}")
            print(f"Final height = {final_height}")
            stop = True
        else:
            cycles[(i % len(pieces), h_pos, jet)] = (height, i)
        if i % 100 == 0:
            print(f"Completed {i} turns, height = {b.height}")
        i += 1
        # b.display()
        # input("Press Enter to continue...")
