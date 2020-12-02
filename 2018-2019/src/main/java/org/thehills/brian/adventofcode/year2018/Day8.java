package org.thehills.brian.adventofcode.year2018;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Day8 {

    public static void main(String args[]) throws IOException {
        Scanner scanner = new Scanner(new File(args[0]));
        ArrayList<Integer> inputNumbers = new ArrayList<>();
        while (scanner.hasNextInt()) {
            inputNumbers.add(scanner.nextInt());
        }

        Node root = readNode(inputNumbers);

        Integer metadataSum = sumMetadata(root);

        System.out.println("Sum of metadata = " + metadataSum);

        Integer value = findValue(root);

        System.out.println("Value of root node = " + value);
    }

    private static Node readNode(ArrayList<Integer> inputNumbers) {
        int numberOfChildren = inputNumbers.remove(0);
        int numberOfMetadata = inputNumbers.remove(0);
        Node n = new Node();
        for (int i = 0; i < numberOfChildren; ++i) {
            n.children.add(readNode(inputNumbers));
        }
        for (int j = 0; j < numberOfMetadata; ++j) {
            n.metadata.add(inputNumbers.remove(0));
        }
        return n;
    }

    private static Integer sumMetadata(Node node) {
        Integer sum = 0;
        for (int i = 0; i < node.children.size(); ++i) {
            sum += sumMetadata(node.children.get(i));
        }
        for (int j = 0; j < node.metadata.size(); ++j) {
            sum += node.metadata.get(j);
        }
        return sum;
    }

    private static Integer findValue(Node node) {
        if (node.children.size() > 0) {
            int value = 0;
            for (int i = 0; i < node.metadata.size(); ++i) {
                int childIndex = node.metadata.get(i);
                if (childIndex < 1 || childIndex > node.children.size()) {
                    continue;
                }
                value += findValue(node.children.get(childIndex - 1));
            }
            return value;
        } else {
            return node.metadata.stream().reduce(0, (a, b) -> a+b);
        }
    }

    private static class Node {
        public List<Integer> metadata = new ArrayList<>();
        public List<Node> children = new ArrayList<>();
    }

}
