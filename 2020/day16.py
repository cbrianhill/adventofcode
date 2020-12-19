import re

def find_invalid_values(ticket, valid_fields_parsed):
    sum_invalid = 0
    found_invalid = False
    for i in ticket:
        valid = False
        for f in valid_fields_parsed:
            r = valid_fields_parsed[f]
            for x in r:
                if i >= x[0] and i <= x[1]:
                    valid = True
                    break
        if not valid:
            found_invalid = True
            sum_invalid += i
    return (found_invalid, sum_invalid)

with open('input/day16.txt') as file:
    lines = file.readlines()
    valid_fields = {}
    valid_fields_parsed = {}
    my_ticket = None
    nearby_tickets = []
    for line in lines:
        match = re.match('^(.+): (.+)$', line)
        if match:
            valid_fields[match[1]] = match[2]
        else:
            match = re.match('^[0-9,]+$', line)
            if match:
                ticket = [int(val) for val in match[0].split(',')]
                if my_ticket is None:
                    my_ticket = ticket
                else:
                    nearby_tickets.append(ticket)
    for field in valid_fields:
        ranges = valid_fields[field].split(' or ')
        valid_fields_parsed[field] = []
        for r in ranges:
            boundaries = [int(v) for v in r.split('-')]
            valid_fields_parsed[field].append((boundaries[0], boundaries[1]))
    (found_invalid, sum_invalid) = find_invalid_values(my_ticket, valid_fields_parsed)
    valid_tickets = []
    if not found_invalid:
        valid_tickets.append(my_ticket)
    for t in nearby_tickets:
        (found_invalid, invalid) = find_invalid_values(t, valid_fields_parsed)
        sum_invalid += invalid
        if not found_invalid:
            valid_tickets.append(t)
    print(f'Ticket scanning error rate = {sum_invalid}')

    # field name -> field_index -> count of matches
    field_indexes = {}
    field_values = []
    for a in range(0, len(my_ticket)):
        field_values.append([])
        for b in valid_tickets:
            field_values[a].append(b[a])
        field_values[a].sort()

    for t in range(0, len(my_ticket)):
        for n in valid_fields_parsed:
            if n not in field_indexes:
                field_indexes[n] = {}
            for v in valid_tickets:
                valid = False
                for r in valid_fields_parsed[n]:
                    if v[t] >= r[0] and v[t] <= r[1]:
                        valid = True
                        break
                if valid:
                    if t in field_indexes[n]:
                        field_indexes[n][t] += 1
                    else:
                        field_indexes[n][t] = 1
    field_possibilities = {}
    for field in field_indexes:
        possible_indexes = []
        for i in field_indexes[field]:
            if field_indexes[field][i] == len(valid_tickets):
                possible_indexes.append(i)
        field_possibilities[field] = possible_indexes
    print(field_possibilities)
    print(field_values)
    field_map = {}
    while len(field_map) < len(my_ticket):
        shortest_list_length = len(my_ticket) - len(field_map)
        shortest_field = None
        shortest_index = None
        for f in field_possibilities:
            if len(field_possibilities[f]) == 1 or (shortest_list_length > len(field_possibilities[f]) > 0):
                shortest_list_length = len(field_possibilities[f])
                shortest_field = f
        if shortest_list_length > 1:
            print("Stuck, don't know what to do.")
        else:
            field_map[shortest_field] = field_possibilities[shortest_field][0]
        for g in field_possibilities:
            if field_map[shortest_field] in field_possibilities[g]:
                field_possibilities[g].remove(field_map[shortest_field])
    print(field_map)
    answer = 1
    for field in field_map:
        if field.startswith('departure '):
            answer *= my_ticket[field_map[field]]
    print (f'Answer = {answer}')