import regex

def string_contains(string, contains):
    for i in range(0, len(contains)):
        found = False
        for j in range(0, len(string)):
            if list(string)[j] == list(contains)[i]:
                found = True
                break
        if not found:
            return False
    return True

def sort_chars(string):
    s = list(string)
    s.sort()
    return "".join(s)


with open('input/day8.txt') as file:
    lines = file.readlines()
    digits = []
    codes = []
    for line in lines:
        line = line.strip()
        matcher = regex.match(r'(([a-g]{2,7}) ?){10} \| (([a-g]{2,7}) ?){4}', line)
        if matcher:
            digits.append(list(matcher.captures(2)))
            codes.append(list(matcher.captures(4)))

    easycodes = 0
    for i in range(0, len(digits)):
        code = codes[i]
        for j in code:
            if len(j) == 2 or len(j) == 3 or len(j) == 4 or len(j) == 7:
                easycodes += 1
    print(f'There are a total of {easycodes} easy numbers')

    for i in range(0, len(digits)):
        for j in range(0, 10):
            digits[i][j] = sort_chars(digits[i][j])

    for i in range(0, len(codes)):
        for j in range(0, 4):
            codes[i][j] = sort_chars(codes[i][j])

    result = 0

    for i in range(0, len(digits)):
        assignments = {}
        for j in range(0, 10):
            if len(digits[i][j]) == 2:
                assignments[1] = digits[i][j]
                break
        for j in range(0, 10):
            if len(digits[i][j]) == 3:
                assignments[7] = digits[i][j]
                break
        for j in range(0, 10):
            if len(digits[i][j]) == 4:
                assignments[4] = digits[i][j]
                break
        for j in range(0, 10):
            if len(digits[i][j]) == 7:
                assignments[8] = digits[i][j]
                break
        for j in range(0, 10):
            if len(digits[i][j]) == 6 and string_contains(digits[i][j], assignments[4]):
                assignments[9] = digits[i][j]
                break
        for j in range(0, 10):
            if len(digits[i][j]) == 6 and digits[i][j] != assignments[9] and string_contains(digits[i][j], assignments[1]):
                assignments[0] = digits[i][j]
                break
        for j in range(0, 10):
            if len(digits[i][j]) == 6 and digits[i][j] != assignments[9] and digits[i][j] != assignments[0]:
                assignments[6] = digits[i][j]
                break
        for j in range(0, 10):
            if len(digits[i][j]) == 5 and string_contains(digits[i][j], assignments[1]):
                assignments[3] = digits[i][j]
                break
        for j in range(0, 10):
            if len(digits[i][j]) == 5 and digits[i][j] != assignments[3] and string_contains(assignments[9], digits[i][j]):
                assignments[5] = digits[i][j]
                break
        for j in range(0, 10):
            if len(digits[i][j]) == 5 and digits[i][j] != assignments[3] and digits[i][j] != assignments[5]:
                assignments[2] = digits[i][j]
                break
        numbers = codes[i]
        this_number = 0
        for n in numbers:
            for m in range(0, 10):
                if assignments[m] == n:
                    this_number *= 10
                    this_number += m
                    break
        result += this_number 
        print(f'Row {i+1}: {this_number}')
    print(f'Sum = {result}')



# 2 -> 1
# 3 -> 7
# 4 -> 4
# 7 -> 8
# 6 and contains(4) -> 9
# 6 and contains(1) -> 0
# 6 -> 6
# 5 and contains(1) -> 3
# 5 and containedby(9) -> 5
# 5 -> (2)
