with open("input/day6.txt") as file:
    groups = []
    group = {}
    group_count = 0
    for line in file.readlines():
        line = line.strip()
        if line == "":
            group['count'] = group_count
            groups.append(group)
            group_count = 0
            group = {}
        else:
            group_count += 1
            for c in line:
                if c in group:
                    group[c] += 1
                else:
                    group[c] = 1
    group['count'] = group_count
    groups.append(group)
    anyone_total = 0
    everyone_total = 0
    for g in groups:
        anyone_total += len(g)
        for item in g:
            if item != 'count' and g[item] == g['count']:
                everyone_total += 1
    print(f'Sum of items anyone in groups declared = {anyone_total}')
    print(f'Sum of items everyone in groups declared = {everyone_total}')
