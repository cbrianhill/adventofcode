import math
codes = {
    '0': [0,0,0,0],
    '1': [0,0,0,1],
    '2': [0,0,1,0],
    '3': [0,0,1,1],
    '4': [0,1,0,0],
    '5': [0,1,0,1],
    '6': [0,1,1,0],
    '7': [0,1,1,1],
    '8': [1,0,0,0],
    '9': [1,0,0,1],
    'A': [1,0,1,0],
    'B': [1,0,1,1],
    'C': [1,1,0,0],
    'D': [1,1,0,1],
    'E': [1,1,1,0],
    'F': [1,1,1,1],
        }

def get_int(packet):
    value = 0
    for d in packet:
        value *= 2
        if d == 1:
            value += 1
    return value

def parse_packet(packet, packet_count, versions, prefix):
    packet_start = 0
    packets_parsed = 0
    end = 0
    new_prefix = prefix + " "
    literals = []
    while packet_start < len(packet) and (packet_count is None or packets_parsed < packet_count):
        version = get_int(packet[packet_start:packet_start+3])
        type_id = get_int(packet[packet_start+3:packet_start+6])
        print(f'{prefix}packet_start = {packet_start}, version {version}, type {type_id}')
        versions.append(version)
        packets_parsed += 1
        if type_id == 4:
            (literal, this_end) = read_literal(packet[packet_start+6:])
            literals.append(literal)
            end = this_end + packet_start + 6
            print(f'{prefix}literal {literal}, end {end}')
        else:
            # It's an operator
            length_type_id = packet[packet_start+6]
            if length_type_id == 0:
                sub_packet_length = get_int(packet[packet_start+7:packet_start+22])
                print (f'{prefix}Operator, length type 0 sub-packets length {sub_packet_length}')
                (this_end, new_literals) = parse_packet(packet[packet_start+22:packet_start+22+sub_packet_length], None, versions, new_prefix)
                this_end += packet_start + 22
                end = packet_start + sub_packet_length + 22
                print (f'{prefix}Operator complete.  End = {end}')
            else:
                sub_packet_count = get_int(packet[packet_start+7:packet_start+18])
                print (f'{prefix}Operator, length type 1, {sub_packet_count} sub-packets')
                (end, new_literals) = parse_packet(packet[packet_start+18:], sub_packet_count, versions, new_prefix)
                end += packet_start + 18
                print (f'{prefix}Operator complete.  End = {end}')
            if type_id == 0:
                literals.append(sum(new_literals))
            elif type_id == 1:
                literals.append(math.prod(new_literals))
            elif type_id == 2:
                literals.append(min(new_literals))
            elif type_id == 3:
                literals.append(max(new_literals))
            elif type_id == 5:
                if new_literals[0] > new_literals[1]:
                    literals.append(1)
                else:
                    literals.append(0)
            elif type_id == 6:
                if new_literals[0] < new_literals[1]:
                    literals.append(1)
                else:
                    literals.append(0)
            elif type_id == 7:
                if new_literals[0] == new_literals[1]:
                    literals.append(1)
                else:
                    literals.append(0)

        packet_start = end
    return (end, literals)

def read_literal(packet):
    literal = 0
    end = 0
    for i in range(0, len(packet), 5):
        literal *= 16
        keep_going = packet[i] == 1
        digit = get_int(packet[i+1:i+5])
        end = i+5
        literal += digit
        if not keep_going:
            break
    return (literal, end)


with open('input/day16.txt') as file:
    lines = file.readlines()
    text = lines[0].strip()
    packet = []
    for c in text:
        if c in codes:
            packet += codes[c]
    versions = []
    print(f'{"".join([ str(x) for x in packet])}')
    (_, literals) = parse_packet(packet, 1, versions, "")
    print(f'Version sum = {sum(versions)}, result = {literals}')

