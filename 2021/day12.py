class Node:
    def __init__(self, name) -> None:
        self.tunnels = {}
        self.name = name
    def add_tunnel(self, neighbor):
        self.tunnels[neighbor.name] = neighbor
    def count_paths(self, end, visited) -> int:
        paths = 0
        visited.append(self.name)
        if self.name == end:
            visited.pop()
            return 1
        for t in self.tunnels:
            if list(t)[0].isupper() or t not in visited:
                paths += self.tunnels[t].count_paths(end, visited)
        visited.pop()
        return paths
    def count_paths_2(self, end, v_once, v_twice) -> int:
        paths = 0
        twice = False
        if self.name == end:
            return 1
        if self.name in v_once:
            v_twice.append(self.name)
            twice = True
        else:
            v_once.append(self.name)

        for t in self.tunnels:
            filtered = filter(lambda n: list(n)[0].islower(), v_twice)
            if t != 'start' and (list(t)[0].isupper() or (t not in v_twice and len(list(filtered)) < 2)):
                paths += self.tunnels[t].count_paths_2(end, v_once, v_twice)

        if twice:
            v_twice.pop()
        else:
            v_once.pop()
        return paths

with open('input/day12.txt') as file:
    lines = file.readlines()
    all_nodes = {}
    for line in lines:
        line = line.strip()
        (first, second) = line.split('-')
        fn = all_nodes.get(first)
        if fn is None:
            fn = Node(first)
            all_nodes[first] = fn
        sn = all_nodes.get(second)
        if sn is None:
            sn = Node(second)
            all_nodes[second] = sn
        fn.add_tunnel(sn)
        sn.add_tunnel(fn)

    paths = all_nodes['start'].count_paths('end', [])
    print(f'There are {paths} paths.')
    paths = all_nodes['start'].count_paths_2('end', [], [])
    print(f'There are {paths} paths.')

