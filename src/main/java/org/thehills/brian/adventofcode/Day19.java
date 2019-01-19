package org.thehills.brian.adventofcode;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day19 {

    private static Pattern realInstructionPattern = Pattern.compile("(.+) (\\d+) (\\d+) (\\d+)");
    private static Pattern instructionPointerPattern = Pattern.compile("#ip (\\d+)");

    public static void main(String args[]) throws IOException {
        InputStream is = null;
        try {
            is = new FileInputStream(args[0]);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            List<Day19.Instruction> realInstructions = new ArrayList<>();
            int instructionPointerRegister = 0;
            while ((line = reader.readLine()) != null) {
                Matcher m = realInstructionPattern.matcher(line);
                Matcher m2 = instructionPointerPattern.matcher(line);
                if (m.matches()) {
                    String instName = m.group(1);
                    int[] instLine = {Integer.parseInt(m.group(2)), Integer.parseInt(m.group(3)), Integer.parseInt(m.group(4))};
                    realInstructions.add(new Day19.Instruction(instName, instLine));
                } else if(m2.matches()) {
                    instructionPointerRegister = Integer.parseInt(m2.group(1));
                }
            }
            System.out.println("Read " + realInstructions.size() + " real instructions.");

            Map<String, Day19.Operation> opcodeMap = new HashMap<>();

            opcodeMap.put("addr", new Addr());
            opcodeMap.put("addi", new Addi());
            opcodeMap.put("mulr", new Mulr());
            opcodeMap.put("muli", new Muli());
            opcodeMap.put("banr", new Banr());
            opcodeMap.put("bani", new Bani());
            opcodeMap.put("borr", new Borr());
            opcodeMap.put("bori", new Bori());
            opcodeMap.put("setr", new Setr());
            opcodeMap.put("seti", new Seti());
            opcodeMap.put("gtir", new Gtir());
            opcodeMap.put("gtri", new Gtri());
            opcodeMap.put("gtrr", new Gtrr());
            opcodeMap.put("eqir", new Eqir());
            opcodeMap.put("eqri", new Eqri());
            opcodeMap.put("eqrr", new Eqrr());

            int[] realRegisters = {0, 0, 0, 0, 0, 0};

            runMachine(realInstructions, instructionPointerRegister, opcodeMap, realRegisters);
            System.out.println("Register 0 contains " + realRegisters[0]);

            // Part 2
            realRegisters[0] = 1;
            realRegisters[1] = 0;
            realRegisters[2] = 0;
            realRegisters[3] = 0;
            realRegisters[4] = 0;
            realRegisters[5] = 0;

            // WARNING:  This will take a very long time.  Analyzed the instructions, the program computes the sum
            // of all of the factors of the number 10,551,298.
            // runMachine(realInstructions, instructionPointerRegister, opcodeMap, realRegisters);
            realRegisters[0] = 10551298+5275649+9566+4783+2206+1103+2+1;
            System.out.println("Register 0 contains " + realRegisters[0]);

        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private static void runMachine(List<Instruction> realInstructions, int instructionPointerRegister, Map<String, Operation> opcodeMap, int[] realRegisters) {
        int nextInstruction = realRegisters[instructionPointerRegister];
        boolean halt = false;
        int instructionNumber = 0;
        while (!halt) {
            // Write value of instruction pointer to bound register
            realRegisters[instructionPointerRegister] = nextInstruction;
            // Execute an operation
            Instruction i = realInstructions.get(nextInstruction);
            Operation o = opcodeMap.get(i.instName);
            o.operate(i.instruction, realRegisters);
            // Write value of bound register to instruction pointer
            nextInstruction = realRegisters[instructionPointerRegister];
            // Increment instruction pointer
            ++nextInstruction;
            if (nextInstruction >= realInstructions.size() || nextInstruction < 0) {
                halt = true;
            }
            ++instructionNumber;
        }
    }

    private static abstract class Operation {
        public String opName;

        public abstract void operate(int[] args, int[] registers);
    }

    private static class Addr extends Day19.Operation {
        public Addr() {
            opName = "addr";
        }

        @Override
        public void operate(int[] args, int[] registers) {
            int operand1 = registers[args[0]];
            int operand2 = registers[args[1]];
            registers[args[2]] = operand1 + operand2;
        }
    }
    private static class Addi extends Day19.Operation {
        public Addi() {
            opName = "addi";
        }

        @Override
        public void operate(int[] args, int[] registers) {
            int operand1 = registers[args[0]];
            int operand2 = args[1];
            registers[args[2]] = operand1 + operand2;
        }
    }
    private static class Mulr extends Day19.Operation {
        public Mulr() {
            opName = "mulr";
        }

        @Override
        public void operate(int[] args, int[] registers) {
            int operand1 = registers[args[0]];
            int operand2 = registers[args[1]];
            registers[args[2]] = operand1 * operand2;
        }
    }
    private static class Muli extends Day19.Operation {
        public Muli() {
            opName = "muli";
        }

        @Override
        public void operate(int[] args, int[] registers) {
            int operand1 = registers[args[0]];
            int operand2 = args[1];
            registers[args[2]] = operand1 * operand2;
        }
    }
    private static class Banr extends Day19.Operation {
        public Banr() {
            opName = "banr";
        }

        @Override
        public void operate(int[] args, int[] registers) {
            int operand1 = registers[args[0]];
            int operand2 = registers[args[1]];
            registers[args[2]] = operand1 & operand2;
        }
    }
    private static class Bani extends Day19.Operation {
        public Bani() {
            opName = "bani";
        }

        @Override
        public void operate(int[] args, int[] registers) {
            int operand1 = registers[args[0]];
            int operand2 = args[1];
            registers[args[2]] = operand1 & operand2;
        }
    }
    private static class Borr extends Day19.Operation {
        public Borr() {
            opName = "borr";
        }

        @Override
        public void operate(int[] args, int[] registers) {
            int operand1 = registers[args[0]];
            int operand2 = registers[args[1]];
            registers[args[2]] = operand1 | operand2;
        }
    }
    private static class Bori extends Day19.Operation {
        public Bori() {
            opName = "bori";
        }

        @Override
        public void operate(int[] args, int[] registers) {
            int operand1 = registers[args[0]];
            int operand2 = args[1];
            registers[args[2]] = operand1 | operand2;
        }
    }
    private static class Setr extends Day19.Operation {
        public Setr() {
            opName = "setr";
        }

        @Override
        public void operate(int[] args, int[] registers) {
            int operand1 = registers[args[0]];
            registers[args[2]] = operand1;
        }
    }
    private static class Seti extends Day19.Operation {
        public Seti() {
            opName = "seti";
        }

        @Override
        public void operate(int[] args, int[] registers) {
            int operand1 = args[0];
            registers[args[2]] = operand1;
        }
    }
    private static class Gtir extends Day19.Operation {
        public Gtir() {
            opName = "gtir";
        }

        @Override
        public void operate(int[] args, int[] registers) {
            int operand1 = args[0];
            int operand2 = registers[args[1]];
            if (operand1 > operand2)
                registers[args[2]] = 1;
            else
                registers[args[2]] = 0;
        }
    }
    private static class Gtri extends Day19.Operation {
        public Gtri() {
            opName = "gtri";
        }

        @Override
        public void operate(int[] args, int[] registers) {
            int operand1 = registers[args[0]];
            int operand2 = args[1];
            if (operand1 > operand2)
                registers[args[2]] = 1;
            else
                registers[args[2]] = 0;
        }
    }
    private static class Gtrr extends Day19.Operation {
        public Gtrr() {
            opName = "gtrr";
        }

        @Override
        public void operate(int[] args, int[] registers) {
            int operand1 = registers[args[0]];
            int operand2 = registers[args[1]];
            if (operand1 > operand2)
                registers[args[2]] = 1;
            else
                registers[args[2]] = 0;
        }
    }
    private static class Eqir extends Day19.Operation {
        public Eqir() {
            opName = "eqir";
        }

        @Override
        public void operate(int[] args, int[] registers) {
            int operand1 = args[0];
            int operand2 = registers[args[1]];
            if (operand1 == operand2)
                registers[args[2]] = 1;
            else
                registers[args[2]] = 0;
        }
    }
    private static class Eqri extends Day19.Operation {
        public Eqri() {
            opName = "eqri";
        }

        @Override
        public void operate(int[] args, int[] registers) {
            int operand1 = registers[args[0]];
            int operand2 = args[1];
            if (operand1 == operand2)
                registers[args[2]] = 1;
            else
                registers[args[2]] = 0;
        }
    }
    private static class Eqrr extends Day19.Operation {
        public Eqrr() {
            opName = "eqrr";
        }

        private static HashSet<Integer> valueHistory = new HashSet<>();

        @Override
        public void operate(int[] args, int[] registers) {
            int operand1 = registers[args[0]];
            int operand2 = registers[args[1]];
            /*  -- Added to solve Day 21, part 2
            System.out.println("Comparing " + operand1 + " and " + operand2);
            if (valueHistory.contains(operand1)) {
                System.out.println("Found duplicate: " + operand1);
                System.exit(0);
            }
            valueHistory.add(operand1);
            */
            if (operand1 == operand2)
                registers[args[2]] = 1;
            else
                registers[args[2]] = 0;
        }
    }

    public static Day19.Operation[] loadOps() {
        Day19.Operation[] ops = new Day19.Operation[16];
        ops[0] = new Day19.Addr();
        ops[1] = new Day19.Addi();
        ops[2] = new Day19.Mulr();
        ops[3] = new Day19.Muli();
        ops[4] = new Day19.Banr();
        ops[5] = new Day19.Bani();
        ops[6] = new Day19.Borr();
        ops[7] = new Day19.Bori();
        ops[8] = new Day19.Setr();
        ops[9] = new Day19.Seti();
        ops[10] = new Day19.Gtir();
        ops[11] = new Day19.Gtri();
        ops[12] = new Day19.Gtrr();
        ops[13] = new Day19.Eqir();
        ops[14] = new Day19.Eqri();
        ops[15] = new Day19.Eqrr();
        return ops;
    }

    public static class Instruction {

        public Instruction(String instName, int[] values) {
            this.instName = instName;
            this.instruction = Arrays.copyOf(values, 3);
        }

        public String instName;
        public int[] instruction;
    }
}
