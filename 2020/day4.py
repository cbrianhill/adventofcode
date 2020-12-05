import re
required_fields = [ 'byr', 'iyr', 'eyr', 'hgt', 'hcl', 'ecl', 'pid']


def validate(doc):
    for req in required_fields:
        if req not in doc:
            return False
    return True


def validate_full(doc):
    try:
        if int(doc['byr']) < 1920 or int(doc['byr']) > 2002:
            return False
        if int(doc['iyr']) < 2010 or int(doc['iyr']) > 2020:
            return False
        if int(doc['eyr']) < 2020 or int(doc['eyr']) > 2030:
            return False
        matches = re.match('^(\d+)(cm|in)$', doc['hgt'])
        if matches is None:
            return False
        if matches[2] == 'cm':
            if int(matches[1]) < 150 or int(matches[1]) > 193:
                return False
        else:
            if int(matches[1]) < 59 or int(matches[1]) > 76:
                return False
        matches = re.match('^#[0-9a-f]{6}$', doc['hcl'])
        if matches is None:
            return False
        if doc['ecl'] not in ['amb', 'blu', 'brn', 'gry', 'grn', 'hzl', 'oth']:
            return False
        matches = re.match('^\d{9}$', doc['pid'])
        if matches is None:
            return False
        return True
    except:
        return False



with open("input/day4.txt") as file:
    docs = []
    completely_valid_docs = []
    current_doc = {}
    for line in file.readlines():
        fields = line.split()
        if len(fields) == 0:
            if validate(current_doc):
                print(f'VALID: {current_doc}')
                docs.append(current_doc)
                if validate_full(current_doc):
                    print(f'COMPLETELY VALID: {current_doc}')
                    completely_valid_docs.append(current_doc)
            else:
                print(f'INVALID: {current_doc}')
            current_doc = {}
        else:
            for field in fields:
                pair = field.split(':')
                current_doc[pair[0]] = pair[1]
    if len(current_doc) > 0:
        if validate(current_doc):
            print(f'VALID: {current_doc}')
            docs.append(current_doc)
            if validate_full(current_doc):
                print(f'COMPLETELY VALID: {current_doc}')
                completely_valid_docs.append(current_doc)
    print(f'Found {len(docs)} valid documents, {len(completely_valid_docs)} completely valid documents.')

