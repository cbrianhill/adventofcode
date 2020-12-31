import re


def calc_score(deck):
    score = 0
    for card in range(len(deck) - 1, -1, -1):
        score += deck[card] * (len(deck) - card)
    return score


# Return (winning player, score)
def play_game(deck1, deck2):
    previous_states = []
    while len(deck1) > 0 and len(deck2) > 0:
        if [deck1, deck2] in previous_states:
            return (1, calc_score(deck1))
        previous_states.append([deck1.copy(), deck2.copy()])
        p1card = deck1.pop(0)
        p2card = deck2.pop(0)
        if len(deck1) >= p1card and len(deck2) >= p2card:
            new_deck1 = deck1[0:p1card]
            new_deck2 = deck2[0:p2card]
            (winner, wscore) = play_game(new_deck1, new_deck2)
        else:
            if p1card > p2card:
                winner = 1
            else:
                winner = 2
        if winner == 1:
            deck1.extend([p1card, p2card])
        else:
            deck2.extend([p2card, p1card])
    if len(deck1) > 0:
        return 1, calc_score(deck1)
    else:
        return 2, calc_score(deck2)


with open('input/day22.txt') as file:
    player_decks = {}
    cur_player = None
    cur_deck = None
    for line in file.readlines():
        m = re.match('^Player (\d+):$', line)
        if m:
            if cur_player is not None:
                player_decks[cur_player] = cur_deck
            cur_player = int(m[1])
            cur_deck = []
        else:
            m = re.match('^(\d+)$', line)
            if m:
                cur_deck.append(int(m[1]))
    if cur_player is not None:
        player_decks[cur_player] = cur_deck
    cur_turn = 0
    starting_decks = [player_decks[1][:], player_decks[2][:]]
    while 0 not in [len(d) for d in player_decks.values()]:
        cur_turn += 1
        next_cards = [player_decks[p][0] for p in player_decks]
        round_winner = None
        for p in player_decks:
            if player_decks[p][0] == max(next_cards):
                round_winner = p
            player_decks[p].pop(0)
        next_cards.sort(reverse=True)
        player_decks[round_winner].extend(next_cards)
        # print(f'Player {round_winner} wins turn {cur_turn}')
    max_cards = 0
    winner = None
    for p in player_decks:
        if len(player_decks[p]) > max_cards:
            winner = p
    score = calc_score(player_decks[winner])
    print(f'Player {winner} wins with score {score}')

    (winner, score) = play_game(starting_decks[0], starting_decks[1])
    print(f'Player {winner} wins with score {score}')