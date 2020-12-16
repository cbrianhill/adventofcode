def iterate(steps, starting_numbers):
    global last_spoken
    spoken_twice_ago = {}
    spoken_numbers = {}
    last_spoken = None
    for i in range(0, steps):
        if i %
        if len(starting_numbers) > 0:
            number = starting_numbers.pop(0)
            last_spoken = number
            if number in spoken_numbers:
                spoken_twice_ago[number] = spoken_numbers[number]
            spoken_numbers[number] = i
        else:
            if last_spoken not in spoken_twice_ago:
                number = 0
                last_spoken = 0
                spoken_twice_ago[0] = spoken_numbers[0]
                spoken_numbers[0] = i
            else:
                number = i - 1 - spoken_twice_ago[last_spoken]
                last_spoken = number
                if number in spoken_numbers:
                    spoken_twice_ago[number] = spoken_numbers[number]
                spoken_numbers[number] = i
    return last_spoken


with open('input/day15.txt') as file:
    starting_numbers = [int(num) for num in file.readlines()[0].strip().split(',')]
    final_answer = iterate(2020, starting_numbers.copy())
    print(f'2020th number is {final_answer}')
    final_answer = iterate(30000000, starting_numbers.copy())
    print(f'30000000th number is {final_answer}')

