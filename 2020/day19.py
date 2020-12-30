import re


def expand_rule(rule, rule_num):
    if rule_num in expanded_rules:
        return expanded_rules[rule_num]

    match = re.match('^[ab]+$', rule)
    if match:
        expanded_rules[rule_num] = rule
        return rule
    expanded_rule = '('
    first = True
    options = rule.split(' | ')
    for opt in options:
        opt.strip()
        if not first:
            expanded_rule = expanded_rule + '|'
        else:
            first = False
        for r in opt.split():
            expanded_rule = expanded_rule + expand_rule(rules[int(r)], int(r))
    expanded_rule = expanded_rule + ')'
    expanded_rules[rule_num] = expanded_rule
    return expanded_rule


with open('input/day19.txt') as file:
    lines = [line.strip() for line in file.readlines()]
    rules = {}
    expanded_rules = {}
    messages = []
    for line in lines:
        match = re.match('^(\d+): "?(.+?)"?$', line)
        if match:
            rules[int(match[1])] = match[2]
        match = re.match('^[ab]+$', line)
        if match:
            messages.append(match[0])
    regular_expression = expand_rule(rules[0], 0)
    matching_string = 0
    for m in messages:
        match = re.match(f'^{regular_expression}$', m)
        if match:
            matching_string += 1
    print(f'Answer = {matching_string}')
    # expanded_rules = { 8: '(' + expanded_rules[8] + '+)', 11: expanded_rules[11][0:749] + '(' + expanded_rules[11][1:749] + expanded_rules[11][749:-1] + ')*' + expanded_rules[11][749:]}
    # expanded_rules = { 8: '(' + expanded_rules[42] + '{2,})', 11: expanded_rules[42] + '(' + expanded_rules[42] + expanded_rules[31] + ')*' + expanded_rules[31]}
    new_regex = f'({expanded_rules[42]}+)({expanded_rules[31]}+)'
    matching_string = 0
    for m in messages:
        match = re.match(f'^{new_regex}$', m)
        if match:
            matches_a = re.findall(f'{expanded_rules[42]}', match[1])
            matches_b = re.findall(f'{expanded_rules[31]}', match[150])
            print(f'{len(matches_a)} :: {len(matches_b)}')
            if len(matches_a) > len(matches_b):
                print(m)
                matching_string += 1
    print(f'Answer = {matching_string}')
