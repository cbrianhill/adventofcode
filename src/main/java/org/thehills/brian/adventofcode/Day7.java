package org.thehills.brian.adventofcode;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day7 {

    private static Pattern inputPattern = Pattern.compile("Step (.+) must be finished before step (.+) can begin.");

    public static void main(String args[]) throws IOException {
        InputStream is = null;
        try {
            is = new FileInputStream(args[0]);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            Map<String, Step> allSteps = new HashMap<>();
            Map<String, Step> startingSteps = new HashMap<>();
            Map<String, Step> enabledSteps = new HashMap<>();
            while((line = reader.readLine()) != null) {
                Matcher m = inputPattern.matcher(line);
                if (m.matches()) {
                    String finishedStepName = m.group(1);
                    String enabledStepName = m.group(2);
                    Step finishedStep = allSteps.get(finishedStepName);
                    if (finishedStep == null) {
                        finishedStep = new Step(finishedStepName);
                        allSteps.put(finishedStepName, finishedStep);
                    }
                    Step enabledStep = allSteps.get(enabledStepName);
                    if (enabledStep == null) {
                        enabledStep = new Step(enabledStepName);
                        allSteps.put(enabledStepName, enabledStep);
                    }
                    finishedStep.enabled.add(enabledStep);
                    enabledStep.enabledBy.add(finishedStep);
                    if (!startingSteps.containsKey(finishedStepName)) {
                        startingSteps.put(finishedStepName, finishedStep);
                    }
                    if (!enabledSteps.containsKey(enabledStepName)) {
                        enabledSteps.put(enabledStepName, enabledStep);
                    }

                }
            }
            for (Step step : enabledSteps.values()) {
                startingSteps.remove(step.name);
            }

            TreeSet<String> availableSteps = new TreeSet<>();
            availableSteps.addAll(startingSteps.keySet());
            String stepPath = "";
            HashSet<Step> completedSteps = new HashSet<>();
            while (!availableSteps.isEmpty()) {
                Step nextStep = allSteps.get(availableSteps.first());
                availableSteps.remove(nextStep.name);
                stepPath += nextStep.name;
                completedSteps.add(nextStep);
                for (Step enabledStep : nextStep.enabled) {
                    if (!completedSteps.contains(enabledStep) && completedSteps.containsAll(enabledStep.enabledBy)) {
                        availableSteps.add(enabledStep.name);
                    }
                }
            }

            System.out.println("Correct step path = " + stepPath);

            // With helpers!

            List<Worker> workers = new ArrayList<>();
            for (int i = 0; i < 5; ++i) {
                workers.add(new Worker(i));
            }
            availableSteps.addAll(startingSteps.keySet());
            completedSteps.clear();
            int simulationSecond = -1;
            boolean workDone = false;
            while(!workDone) {
                ++simulationSecond;
                System.out.println("SIM Second = " + simulationSecond);
                // handle workers completing tasks
                for (Worker w : workers) {
                    if (w.currentStep != null) {
                        if (w.completionTime == simulationSecond) {
                            // This worker has completed a task.
                            System.out.println("Worker " + w.workerId + " completed step " + w.currentStep.name);
                            completedSteps.add(w.currentStep);
                            for (Step enabledStep : w.currentStep.enabled) {
                                if (!completedSteps.contains(enabledStep) && completedSteps.containsAll(enabledStep.enabledBy)) {
                                    availableSteps.add(enabledStep.name);
                                }
                            }
                            w.currentStep = null;
                        }
                    }
                }
                // Now any idle workers can be assigned new steps
                for (Worker w : workers) {
                    if (w.currentStep == null) {
                        if (!availableSteps.isEmpty()) {
                            w.currentStep = allSteps.get(availableSteps.first());
                            w.completionTime = simulationSecond + 60 + (w.currentStep.name.charAt(0) - 'A' + 1);
                            System.out.println("Worker " + w.workerId + " starting step " + w.currentStep.name);
                            availableSteps.remove(w.currentStep.name);
                        }
                    }
                }
                boolean workersWorking = false;
                for (Worker w : workers) {
                    if (w.currentStep != null) {
                        workersWorking = true;
                        break;
                    }
                }
                workDone = !workersWorking && availableSteps.isEmpty();
            }
            System.out.println("Simulation complete at " + simulationSecond);


        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private static class Step {
        public String name;
        public List<Step> enabled = new ArrayList<>();
        public List<Step> enabledBy = new ArrayList<>();

        public Step(String name) {
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Step step = (Step) o;
            return Objects.equals(name, step.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }
    }

    private static class Worker {
        public int workerId;
        public Step currentStep;
        public int completionTime;

        public Worker(int id) { this.workerId = id; }
    }
}
