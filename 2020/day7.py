import re


class Bag:

    def __init__(self, color):
        # color -> int
        self.contains = {}
        # list of color
        self.found_in = []
        self.color = color


bag_colors = {}

with open("input/day7.txt") as file:
    lines = [line.strip() for line in file.readlines()]
    for line in lines:
        match = re.match('^(.+) bags contain (.+)$', line)
        if match:
            this_color = match[1]
            if this_color in bag_colors:
                this_bag = bag_colors[this_color]
            else:
                this_bag = Bag(this_color)
                bag_colors[this_color] = this_bag
            contents = match[2].split(', ')
            for inner_bag in contents:
                inner_bag = inner_bag.strip('.')
                contents_match = re.match('^(\d+) (.+) bag', inner_bag)
                if contents_match:
                    this_bag.contains[contents_match[2]] = int(contents_match[1])
                    if contents_match[2] in bag_colors:
                        bag_colors[contents_match[2]].found_in.append(this_color)
                    else:
                        new_bag = Bag(contents_match[2])
                        new_bag.found_in.append(this_color)
                        bag_colors[contents_match[2]] = new_bag
    known_colors = []
    colors_to_check = bag_colors["shiny gold"].found_in
    while len(colors_to_check) > 0:
        check_color = colors_to_check[0]
        known_colors.append(check_color)
        colors_to_check = colors_to_check[1:]
        known_colors.extend(bag_colors[check_color].found_in)
        colors_to_check.extend(bag_colors[check_color].found_in)
    print(f'Shiny gold bags can be found in {len(list(dict.fromkeys(known_colors)))} other bag colors')

    bags_to_check = [(bag_colors["shiny gold"], 1)]
    inner_bags = 0
    while len(bags_to_check) > 0:
        (bag, multiplier) = bags_to_check[0]
        bags_to_check = bags_to_check[1:]
        for inner in bag.contains:
            inner_bags += bag.contains[inner] * multiplier
            bags_to_check.append((bag_colors[inner], multiplier * bag.contains[inner]))
    print(f'Inside the shiny gold bag there are {inner_bags} bags.')