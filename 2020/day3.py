row = 0
column = 1
slopes = [(1, 3), (1, 1), (1, 5), (1, 7), (2, 1)]
trees = []
with open("input/day3.txt") as file:
    forest_map = file.readlines()
    forest_map = [line.strip() for line in forest_map]
    for slope in slopes:
        current_pos = (0, 0)
        trees_encountered = 0
        while current_pos[row] < len(forest_map):
            if forest_map[current_pos[row]][current_pos[column] % len(forest_map[0])] == '#':
                trees_encountered += 1
            current_pos = (current_pos[row] + slope[row], current_pos[column] + slope[column])
        print(f'Slope ({slope[column]},{slope[row]}) Encountered {trees_encountered} trees.')
        trees.append(trees_encountered)
    product = 1
    for tree_count in trees:
        product *= tree_count
    print(f'Product = {product}')
