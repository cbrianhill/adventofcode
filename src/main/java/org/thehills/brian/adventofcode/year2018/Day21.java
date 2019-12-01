package org.thehills.brian.adventofcode.year2018;

import java.io.IOException;

public class Day21 {

    public static void main(String args[]) throws IOException {

        /* Running with this input, I noticed that the program would halt whenever
           instruction 28 is executed and evaluates to true, the program will halt:

           28 eqrr 5 0 1              reg1 = reg5 == reg0
           29 addr 1 4 4              reg4 = reg1 + reg4
           30 seti 5 9 4              reg4 = 5

           I noted that there's only one 'eqrr' instruction in the set, so I set a
           breakpoint, and found the value of register 5 when the first 'eqrr' instruction
           is found, and inspected the value of register 5: 2884703
         */

        Day19.main(new String[] { "day21.input.txt" });
    }
}
