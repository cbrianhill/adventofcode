def to_decimal(val: str):
    binval = val.strip()
    ret = 0
    for i in range(0, len(binval)):
        ret *= 2
        if binval[i] == '1':
            ret += 1
    return ret

with open('input/day3.txt') as file:
    lines = file.readlines()

    columns = len(lines[0])
    gamma = ''
    epsilon = ''
    for c in range(0, columns - 1):
        zeros = 0
        ones = 0
        for line in lines:
            if line[c] == '0':
                zeros += 1
            if line[c] == '1':
                ones += 1
        if zeros > ones:
            gamma = gamma + '0'
            epsilon = epsilon + '1'
        else:
            gamma = gamma + '1'
            epsilon = epsilon + '0'
    print(f'Gamma = {gamma}, Epsilon = {epsilon}')
    g_int = to_decimal(gamma)
    e_int = to_decimal(epsilon)
    print(f'Gamma = {g_int}, Epsilon = {e_int}, Answer = {g_int * e_int}')
    
    # Oxygen generator rating
    candidates = lines
    for c in range(0, columns - 1):
        zerolines = []
        onelines = []
        zeros = 0
        ones = 0
        for line in candidates:
            if line[c] == '0':
                zeros += 1
                zerolines.append(line)
            if line[c] == '1':
                ones += 1
                onelines.append(line)
        if zeros > ones:
            candidates = zerolines
        else:
            candidates = onelines
        if len(candidates) == 1:
            break
    
    oxygen_bin = candidates[0]
    oxygen_rating = to_decimal(candidates[0])
    
    # scrubber rating
    candidates = lines
    for c in range(0, columns - 1):
        zerolines = []
        onelines = []
        zeros = 0
        ones = 0
        for line in candidates:
            if line[c] == '0':
                zeros += 1
                zerolines.append(line)
            if line[c] == '1':
                ones += 1
                onelines.append(line)
        if zeros <= ones:
            candidates = zerolines
        else:
            candidates = onelines
        if len(candidates) == 1:
            break
    scrubber_bin = candidates[0]
    scrubber_rating = to_decimal(candidates[0])

    print(f'oxygen rating = {oxygen_bin}, scrubber rating = {scrubber_bin}')
    print(f'oxygen rating = {oxygen_rating}, scrubber rating = {scrubber_rating}')
    print(f'answer = {oxygen_rating * scrubber_rating}')
