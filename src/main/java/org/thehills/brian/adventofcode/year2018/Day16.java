package org.thehills.brian.adventofcode.year2018;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day16 {

    private static Pattern realInstructionPattern = Pattern.compile("(\\d+) (\\d+) (\\d+) (\\d+)");

    public static void main(String args[]) throws IOException {
        InputStream is = null;
        try {
            is = new FileInputStream(args[0]);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            List<SampleInstruction> instructions = new ArrayList<>();
            List<Instruction> realInstructions = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Before:")) {
                    String line2 = reader.readLine();
                    String line3 = reader.readLine();
                    String[] allLines = { line, line2, line3 };
                    instructions.add(new SampleInstruction(allLines));
                }
                Matcher m = realInstructionPattern.matcher(line);
                if (m.matches()) {
                    int[] instLine = {Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)), Integer.parseInt(m.group(3)), Integer.parseInt(m.group(4))};
                    realInstructions.add(new Instruction(instLine));
                }
            }
            System.out.println("Read " + instructions.size() + " sample instructions.");
            System.out.println("Read " + realInstructions.size() + " real instructions.");

            Operation[] ops = loadOps();

            int threeWayMatches = 0;
            for (SampleInstruction inst : instructions) {
               int matchingOps = 0;
               for (Operation o : ops) {
                  if (operationMatches(o, inst)) {
                      ++matchingOps;
                      if (o.possibleOpcodes[inst.instruction[0]] == null) {
                          o.possibleOpcodes[inst.instruction[0]] = true;
                      }
                  } else {
                      o.possibleOpcodes[inst.instruction[0]] = false;
                  }
               }
               if (matchingOps >= 3) {
                   threeWayMatches++;
               }
            }
            System.out.println(threeWayMatches + " samples matched three or more instructions.");
            Map<Integer, Operation> opcodeMap = new HashMap<>();
            List<Operation> remainingOps = new ArrayList<>();
            remainingOps.addAll(Arrays.asList(ops));
            while (opcodeMap.size() < 16) {
                for (int i = 0; i < 16; ++i) {
                    int matches = 0;
                    Operation firstMatch = null;
                    for (Operation o : remainingOps) {
                        if (o.possibleOpcodes[i]) {
                            ++matches;
                            firstMatch = o;
                        }
                    }
                    if (matches == 1) {
                        System.out.println("Opcode " + firstMatch.opName + " matched " + i);
                        opcodeMap.put(i, firstMatch);
                        remainingOps.remove(firstMatch);
                        for (Operation o : remainingOps) {
                            o.possibleOpcodes[i] = false;
                        }
                    }
                }
            }
            for (Map.Entry<Integer, Operation> e : opcodeMap.entrySet()) {
                System.out.println(e.getKey() + " --> " + e.getValue().opName);
            }

            int[] realRegisters = {0, 0, 0, 0};
            for (Instruction i : realInstructions) {
                Operation o = opcodeMap.get(i.instruction[0]);
                o.operate(i.instruction, realRegisters);
            }
            System.out.println("Register 0 contains " + realRegisters[0]);
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public static boolean operationMatches(Operation o, SampleInstruction i) {
        int[] registers = Arrays.copyOf(i.registersBefore, 4);
        o.operate(i.instruction, registers);
        return Arrays.equals(registers, i.registersAfter);
    }

    private static final Pattern firstLinePattern = Pattern.compile("Before: \\[(\\d+), (\\d+), (\\d+), (\\d+)\\]");
    private static final Pattern secondLinePattern = Pattern.compile("(\\d+) (\\d+) (\\d+) (\\d+)");
    private static final Pattern thirdLinePattern = Pattern.compile("After:  \\[(\\d+), (\\d+), (\\d+), (\\d+)\\]");

    private static abstract class Operation {
        public String opName;
        public int opId;
        public Boolean[] possibleOpcodes = new Boolean[16];

        public abstract void operate(int[] args, int[] registers);
    }

    private static class Addr extends Operation {
        public Addr() {
            opName = "addr";
        }

        @Override
        public void operate(int[] args, int[] registers) {
            int operand1 = registers[args[1]];
            int operand2 = registers[args[2]];
            registers[args[3]] = operand1 + operand2;
        }
    }
    private static class Addi extends Operation {
        public Addi() {
            opName = "addi";
        }

        @Override
        public void operate(int[] args, int[] registers) {
            int operand1 = registers[args[1]];
            int operand2 = args[2];
            registers[args[3]] = operand1 + operand2;
        }
    }
    private static class Mulr extends Operation {
        public Mulr() {
            opName = "mulr";
        }

        @Override
        public void operate(int[] args, int[] registers) {
            int operand1 = registers[args[1]];
            int operand2 = registers[args[2]];
            registers[args[3]] = operand1 * operand2;
        }
    }
    private static class Muli extends Operation {
        public Muli() {
            opName = "muli";
        }

        @Override
        public void operate(int[] args, int[] registers) {
            int operand1 = registers[args[1]];
            int operand2 = args[2];
            registers[args[3]] = operand1 * operand2;
        }
    }
    private static class Banr extends Operation {
        public Banr() {
            opName = "banr";
        }

        @Override
        public void operate(int[] args, int[] registers) {
            int operand1 = registers[args[1]];
            int operand2 = registers[args[2]];
            registers[args[3]] = operand1 & operand2;
        }
    }
    private static class Bani extends Operation {
        public Bani() {
            opName = "bani";
        }

        @Override
        public void operate(int[] args, int[] registers) {
            int operand1 = registers[args[1]];
            int operand2 = args[2];
            registers[args[3]] = operand1 & operand2;
        }
    }
    private static class Borr extends Operation {
        public Borr() {
            opName = "borr";
        }

        @Override
        public void operate(int[] args, int[] registers) {
            int operand1 = registers[args[1]];
            int operand2 = registers[args[2]];
            registers[args[3]] = operand1 | operand2;
        }
    }
    private static class Bori extends Operation {
        public Bori() {
            opName = "bori";
        }

        @Override
        public void operate(int[] args, int[] registers) {
            int operand1 = registers[args[1]];
            int operand2 = args[2];
            registers[args[3]] = operand1 | operand2;
        }
    }
    private static class Setr extends Operation {
        public Setr() {
            opName = "setr";
        }

        @Override
        public void operate(int[] args, int[] registers) {
            int operand1 = registers[args[1]];
            registers[args[3]] = operand1;
        }
    }
    private static class Seti extends Operation {
        public Seti() {
            opName = "seti";
        }

        @Override
        public void operate(int[] args, int[] registers) {
            int operand1 = args[1];
            registers[args[3]] = operand1;
        }
    }
    private static class Gtir extends Operation {
        public Gtir() {
            opName = "gtir";
        }

        @Override
        public void operate(int[] args, int[] registers) {
            int operand1 = args[1];
            int operand2 = registers[args[2]];
            if (operand1 > operand2)
                registers[args[3]] = 1;
            else
                registers[args[3]] = 0;
        }
    }
    private static class Gtri extends Operation {
        public Gtri() {
            opName = "gtri";
        }

        @Override
        public void operate(int[] args, int[] registers) {
            int operand1 = registers[args[1]];
            int operand2 = args[2];
            if (operand1 > operand2)
                registers[args[3]] = 1;
            else
                registers[args[3]] = 0;
        }
    }
    private static class Gtrr extends Operation {
        public Gtrr() {
            opName = "gtrr";
        }

        @Override
        public void operate(int[] args, int[] registers) {
            int operand1 = registers[args[1]];
            int operand2 = registers[args[2]];
            if (operand1 > operand2)
                registers[args[3]] = 1;
            else
                registers[args[3]] = 0;
        }
    }
    private static class Eqir extends Operation {
        public Eqir() {
            opName = "eqir";
        }

        @Override
        public void operate(int[] args, int[] registers) {
            int operand1 = args[1];
            int operand2 = registers[args[2]];
            if (operand1 == operand2)
                registers[args[3]] = 1;
            else
                registers[args[3]] = 0;
        }
    }
    private static class Eqri extends Operation {
        public Eqri() {
            opName = "eqri";
        }

        @Override
        public void operate(int[] args, int[] registers) {
            int operand1 = registers[args[1]];
            int operand2 = args[2];
            if (operand1 == operand2)
                registers[args[3]] = 1;
            else
                registers[args[3]] = 0;
        }
    }
    private static class Eqrr extends Operation {
        public Eqrr() {
            opName = "eqrr";
        }

        @Override
        public void operate(int[] args, int[] registers) {
            int operand1 = registers[args[1]];
            int operand2 = registers[args[2]];
            if (operand1 == operand2)
                registers[args[3]] = 1;
            else
                registers[args[3]] = 0;
        }
    }

    public static Operation[] loadOps() {
        Operation[] ops = new Operation[16];
        ops[0] = new Addr();
        ops[1] = new Addi();
        ops[2] = new Mulr();
        ops[3] = new Muli();
        ops[4] = new Banr();
        ops[5] = new Bani();
        ops[6] = new Borr();
        ops[7] = new Bori();
        ops[8] = new Setr();
        ops[9] = new Seti();
        ops[10] = new Gtir();
        ops[11] = new Gtri();
        ops[12] = new Gtrr();
        ops[13] = new Eqir();
        ops[14] = new Eqri();
        ops[15] = new Eqrr();
        return ops;
    }

    public static class SampleInstruction {
        public SampleInstruction(String[] lines) {
            Matcher m1 = firstLinePattern.matcher(lines[0]);
            m1.matches();
            registersBefore[0] = Integer.parseInt(m1.group(1));
            registersBefore[1] = Integer.parseInt(m1.group(2));
            registersBefore[2] = Integer.parseInt(m1.group(3));
            registersBefore[3] = Integer.parseInt(m1.group(4));

            Matcher m2 = secondLinePattern.matcher(lines[1]);
            m2.matches();
            instruction[0] = Integer.parseInt(m2.group(1));
            instruction[1] = Integer.parseInt(m2.group(2));
            instruction[2] = Integer.parseInt(m2.group(3));
            instruction[3] = Integer.parseInt(m2.group(4));

            Matcher m3 = thirdLinePattern.matcher(lines[2]);
            m3.matches();
            registersAfter[0] = Integer.parseInt(m3.group(1));
            registersAfter[1] = Integer.parseInt(m3.group(2));
            registersAfter[2] = Integer.parseInt(m3.group(3));
            registersAfter[3] = Integer.parseInt(m3.group(4));
        }
        public int[] registersBefore = new int[4];
        public int[] instruction = new int[4];
        public int[] registersAfter = new int[4];

    }

    public static class Instruction {

        public Instruction(int[] values) {
            this.instruction = Arrays.copyOf(values, 4);
        }

        public int[] instruction;
    }

}
