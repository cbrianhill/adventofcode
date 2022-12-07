#!/usr/bin/env python

from __future__ import annotations
from functools import reduce
from re import match
from typing import Dict, List, Optional

class Directory:
    def __init__(self, name: str, parent: Optional[Directory]):
        self.name = name
        self.parent = parent
        self.size = 0
        self.children: Dict[str, Directory] = {}
        self.files: Dict[str, int] = {}

    def setContents(self, contents: List[str]):
        for line in contents:
            dirmatch = match(r"^dir (.+)", line)
            if dirmatch is not None:
                dirname = dirmatch.group(1)
                if dirname not in self.children:
                    self.children[dirname] = Directory(dirname, self)
            filematch = match(r"^(\d+) (.+)", line)
            if filematch is not None:
                filename = filematch.group(2)
                filesize = int(filematch.group(1))
                self.files[filename] = filesize

    def getTotalSize(self) -> int:
        total_size = 0
        for file in self.files:
            total_size += self.files[file]
        for dir in self.children:
            total_size += self.children[dir].getTotalSize()
        return total_size

    def findDirectoriesWithMaxSize(self, threshold: int) -> List[Directory]:
        small_dirs: List[Directory] = []
        for dir in self.children:
            small_dirs.extend(self.children[dir].findDirectoriesWithMaxSize(threshold))
        my_size = self.getTotalSize()
        if my_size <= threshold:
            small_dirs.append(self)
        return small_dirs

    def findDirectoriesWithMinSize(self, threshold: int) -> List[Directory]:
        small_dirs: List[Directory] = []
        for dir in self.children:
            small_dirs.extend(self.children[dir].findDirectoriesWithMinSize(threshold))
        my_size = self.getTotalSize()
        if my_size >= threshold:
            small_dirs.append(self)
        return small_dirs

    def getChild(self, name: str) -> Directory:
        return self.children[name]

def runCommand(command: Optional[str], output: List[str], tree: Directory, cwd: Directory) -> Directory:
    if command is None:
        return cwd
    if command == "cd /":
        return tree
    if command == "cd ..":
        return cwd.parent if cwd.parent is not None else tree
    cdmatch = match(r"^cd (.+)", command)
    if cdmatch is not None:
        return cwd.getChild(cdmatch.group(1))
    else:
        cwd.setContents(output)
        return cwd

with open("input.txt") as file:
    tree = Directory("/", None)
    cwd = tree
    command = None
    output = []
    for line in file.readlines():
        line.strip()
        commandmatch = match(r"^\$ (.+)", line)
        if commandmatch is not None:
            cwd = runCommand(command, output, tree, cwd)
            output = []
            command = commandmatch.group(1)
        else:
            output.append(line)
    small_dirs = tree.findDirectoriesWithMaxSize(100000)
    total_small_size = sum(d.getTotalSize() for d in small_dirs)
    print(f"Total size of all small directories = {total_small_size}")

    free_space = 70000000 - tree.getTotalSize()
    free_space_required = 30000000 
    amount_to_delete = free_space_required - free_space
    large_dirs = tree.findDirectoriesWithMinSize(amount_to_delete)
    smallest_eligible_size = min(d.getTotalSize() for d in large_dirs)
    print(f"Size of smallest directory eligible for deletion = {smallest_eligible_size}")

