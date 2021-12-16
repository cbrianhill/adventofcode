import re
from typing import Optional

def calc_score(scores):
    min_score = None
    min_letter = None
    max_score = None
    max_letter = None
    for k in scores.keys():
        n = scores[k]
        if min_score is None or n < min_score:
            min_score = n
            min_letter = k
        if max_score is None or n> max_score:
            max_score = n
            max_letter = k
    print(f'max = {max_letter} ({max_score}), min = {min_letter} ({min_score})')
    return max_score - min_score


with open('input/day14.txt') as file:
    lines = file.readlines()
    recipe = list(lines[0].strip())
    first_letter = recipe[0]
    last_letter = recipe[-1]

    rules = {}
    pair_counts = {}
    for line in lines:
        match = re.match(r'(\w\w) -> (\w)', line)
        if match:
            rules[match.group(1)] = [match.group(1)[0] + match.group(2), match.group(2) + match.group(1)[1]]

    for i in range(0, len(recipe) - 1):
        pair = "".join(recipe[i:i+2])
        if pair in pair_counts:
            pair_counts[pair] += 1
        else:
            pair_counts[pair] = 1
    print(f'Pair counts = {pair_counts}')
    print(f'Rules = {rules}')

    for s in range(0, 40):
        new_pair_counts = {}
        for k in pair_counts:
            subs = rules[k]
            for u in subs:
                if u in new_pair_counts:
                    new_pair_counts[u] += pair_counts[k]
                else:
                    new_pair_counts[u] = pair_counts[k]
        pair_counts = new_pair_counts
        print(f'Step {s} complete.')
        print(f'Pair counts = {pair_counts}')

    scores = {}
    for pair in pair_counts:
        for p in range(0, 2):
            if pair[p] in scores:
                scores[pair[p]] += pair_counts[pair]
            else:
                scores[pair[p]] = pair_counts[pair]
    for l in scores:
        if l == first_letter or l == last_letter:
            scores[l] = int(scores[l] / 2) + 1
        else:
            scores[l] = int(scores[l] / 2)
    print(f'Final score = {calc_score(scores)}')
