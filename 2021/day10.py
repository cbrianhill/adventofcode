scores = { ')': 3, ']': 57, '}': 1197, '>': 25137 }
completion_scores = { ')': 1, ']': 2, '}': 3, '>': 4 }
closers = { '(': ')', '<': '>', '[': ']', '{': '}' }
with open('input/day10.txt') as file:
    lines = file.readlines()
    score = 0
    incomplete = []
    cscores = []
    for line in lines:
        chunks = []
        line = line.strip()
        code = list(line)
        corrupt = False
        completion_score = 0
        for i in range(0, len(code)):
            if code[i] in [ '[', '(', '{', '<' ]:
                chunks.append(code[i])
            else:
                previous = chunks.pop()
                if code[i] != closers[previous]:
                    corrupt = True
                    score += scores[code[i]]
                    break
        if not corrupt:
            while len(chunks) > 0:
                x = chunks.pop()
                c = closers[x]
                completion_score *= 5
                completion_score += completion_scores[c]
            cscores.append(completion_score)
    cscores.sort()
    winner = int(len(cscores) / 2)
    print(f'Score = {score}')
    print(f'Completion scores: {cscores}')
    print(f'Completion Score = {cscores[winner]}')

