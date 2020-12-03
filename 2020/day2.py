import re
with open("input/day2.txt") as file:
    lines = file.readlines();
    valid_passwords_1 = 0
    valid_passwords_2 = 0
    for line in lines:
        line = line.strip()
        match = re.match('^(\d+)-(\d+) (\w): (.+)$', line)
        if match is not None:
            min_count = int(match.group(1))
            max_count = int(match.group(2))

            letter = match.group(3)
            password = match.group(4)
            occurrences = 0
            for l in password:
                if l == letter:
                    occurrences += 1
            if occurrences >= min_count and occurrences <= max_count:
                valid_passwords_1 += 1
            if (password[min_count - 1] == letter and password[max_count - 1] != letter) or \
                (password[min_count - 1] != letter and password[max_count - 1] == letter):
                valid_passwords_2 += 1
    print(f'Part 1 interpretation: There are {valid_passwords_1} valid passwords.')
    print(f'Part 2 interpretation: There are {valid_passwords_2} valid passwords.')
