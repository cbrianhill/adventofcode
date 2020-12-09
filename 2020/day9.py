PREAMBLE_LENGTH = 25

def verify_sum(data, preamble_length):
    preamble = []
    for i in range(0, preamble_length):
        preamble.append(data[i])
    for j in data[preamble_length:]:
        if not search_preamble(preamble, j):
            return j
        preamble.append(j)
        preamble = preamble[1:]


def search_preamble(preamble, j):
    for i in range(0, len(preamble)):
        for n in range(i+1, len(preamble)):
            if preamble[i] + preamble[n] == j:
                return True
    return False


with open('input/day9.txt') as file:
    lines = file.readlines()
    data = [int(number) for number in lines]
    value = verify_sum(data, PREAMBLE_LENGTH)
    print(f'Value = {value}')

    for anchor in range(0, len(data)):
        sum = data[anchor]
        begin = anchor
        end = anchor
        while (sum < value) and (begin != 0 or end != len(data) - 1):
            if begin > 0:
                begin -= 1
                sum += data[begin]
                if sum >= value:
                    break
            if end < len(data) - 1:
                end += 1
                sum += data[end]
                if sum >= value:
                    break
        if sum == value:
            print(f'Range found ({begin},{end})')
            smallest = data[begin]
            largest = data[begin]
            for x in range(begin, end+1):
                if data[x] < smallest:
                    smallest = data[x]
                if data[x] > largest:
                    largest = data[x]
            print(f'Weakness = {smallest} + {largest} = {smallest + largest}')
            break
