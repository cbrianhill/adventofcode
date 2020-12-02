with open("input/day1.txt") as file:
    lines = file.readlines()
    entries = [int(line.strip()) for line in lines]
    for e1 in entries:
        for e2 in entries:
            if e1 + e2 == 2020:
                product = e1 * e2
                print(f'{e1} + {e2} = 2020, {e1} * {e2} = {product}')
    for e1 in entries:
        for e2 in entries:
            for e3 in entries:
                if e1 + e2 + e3 == 2020:
                    product = e1 * e2 * e3
                    print(f'{e1} + {e2} + {e3} = 2020, {e1} * {e2} * {e3} = {product}')
