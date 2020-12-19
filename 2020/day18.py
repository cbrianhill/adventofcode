
def operate(s):
    values = []
    i = 0
    while i < len(s):
        c = s[i]
        if c == ' ':
            i += 1
            continue
        elif c == '(':
            end = find_matching_paren(s, i)
            values.append(operate(s[i+1:end-1]))
            i = end + 1
        elif c in [ '0', '1', '2', '3', '4', '5', '6', '7', '8', '9']:
            values.append(int(c))
            i += 1
        else:
            values.append(c)
            i += 1
    while len(values) > 1:
        op1 = values.pop(0)
        operation = values.pop(0)
        op2 = values.pop(0)
        if operation == '+':
            res = op1 + op2
        elif operation == '*':
            res = op1 * op2
        values.insert(0, res)
    return values[0]


def operate_2(s):
    values = []
    i = 0
    while i < len(s):
        c = s[i]
        if c == ' ':
            i += 1
            continue
        elif c == '(':
            end = find_matching_paren(s, i)
            values.append(operate_2(s[i+1:end-1]))
            i = end + 1
        elif c in [ '0', '1', '2', '3', '4', '5', '6', '7', '8', '9']:
            values.append(int(c))
            i += 1
        else:
            values.append(c)
            i += 1
    while '+' in values:
        op_index = values.index('+')
        op1 = values[op_index-1]
        op2 = values[op_index+1]
        res =op1 + op2
        values.pop(op_index-1)
        values.pop(op_index-1)
        values.pop(op_index-1)
        values.insert(op_index-1, res)
    while len(values) > 1:
        op1 = values.pop(0)
        operation = values.pop(0)
        op2 = values.pop(0)
        if operation == '*':
            res = op1 * op2
        values.insert(0, res)
    return values[0]


def find_matching_paren(s, i):
    parens = 1
    c = i+1
    while parens > 0:
        if s[c] == '(':
            parens += 1
        elif s[c] == ')':
            parens -= 1
        c += 1
    return c

with open('input/day18.txt') as file:
    lines = [line.strip() for line in file.readlines()]
    sum = 0
    sum2 = 0
    for line in lines:
        result = operate(line)
        result2 = operate_2(line)
        sum += result
        sum2 += result2
    print(f'Answer = {sum}')
    print(f'Answer = {sum2}')
