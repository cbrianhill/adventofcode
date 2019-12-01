package org.thehills.brian.adventofcode.year2018;

import com.google.common.collect.ArrayTable;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import java.io.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day4 {

    public static void main(String args[]) throws IOException {
        InputStream is = null;
        try {
            is = new FileInputStream(args[0]);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            TreeSet<GuardEvent> events = new TreeSet<>();
            String line;
            while ((line = reader.readLine()) != null) {
                GuardEvent ge = GuardEvent.create(line);
                events.add(ge);
            }

            GuardEvent previousEvent = new GuardEvent();
            for (GuardEvent ge : events) {
                if (ge.guardId == null) {
                    ge.guardId = previousEvent.guardId;
                }
                System.out.println(ge);
                previousEvent = ge;
            }

            Table<Integer, Integer, Integer> sleepTable = HashBasedTable.create();
            Iterator<GuardEvent> eventIterator = events.iterator();
            GuardEvent savedEvent = null;
            while (eventIterator.hasNext() || savedEvent != null) {
                GuardEvent ge;
                if (savedEvent != null) {
                    ge = savedEvent;
                    savedEvent = null;
                } else {
                    ge = eventIterator.next();
                }
                if (ge.eventType == GuardEventType.FALL_ASLEEP) {
                    GuardEvent nextEvent = eventIterator.next();
                    savedEvent = nextEvent;
                    Integer stoppingPoint = nextEvent.eventTime.getMinute() - 1;
                    if (nextEvent.eventType != GuardEventType.WAKE_UP) {
                        stoppingPoint = 60;
                    }
                    for (int i = ge.eventTime.getMinute(); i < stoppingPoint; ++i) {
                        Integer currentValue;
                        if (!sleepTable.contains(ge.guardId, i)) {
                            currentValue = 0;
                        } else {
                            currentValue = sleepTable.get(ge.guardId, i);
                        }
                        sleepTable.put(ge.guardId, i, ++currentValue);
                    }

                }

            }

            Integer sleepiestGuardId = Integer.MIN_VALUE;
            Integer mostMinutesAsleep = -1;
            Integer sleepiestGuardPeakMinute = -1;

            Integer overallPeakGuardId = Integer.MIN_VALUE;
            Integer overallPeakGuardMinute = Integer.MIN_VALUE;
            Integer overallPeakGuardValue = Integer.MIN_VALUE;

            // Now we have a table of Row = guard ID, Column = minute, Value = times asleep.
            for (Map.Entry<Integer, Map<Integer, Integer>> row : sleepTable.rowMap().entrySet()) {
                Integer guardId = row.getKey();
                Integer minutesAsleep = 0;
                Integer peakMinute = -1;
                Integer peakMinuteValue = -1;
                Map<Integer, Integer> sleepMinutes = row.getValue();
                for (Map.Entry<Integer, Integer> minuteEntry : sleepMinutes.entrySet()) {
                    Integer timesSlept = minuteEntry.getValue();
                    minutesAsleep += timesSlept;
                    if (timesSlept > peakMinuteValue) {
                        peakMinuteValue = timesSlept;
                        peakMinute = minuteEntry.getKey();
                    }
                    if (timesSlept > overallPeakGuardValue) {
                        overallPeakGuardId = guardId;
                        overallPeakGuardMinute = minuteEntry.getKey();
                        overallPeakGuardValue = timesSlept;
                    }
                }
                if (minutesAsleep > mostMinutesAsleep) {
                    mostMinutesAsleep = minutesAsleep;
                    sleepiestGuardId = guardId;
                    sleepiestGuardPeakMinute = peakMinute;
                }
            }

            System.out.println("Sleepiest guard is #" + sleepiestGuardId + " and peak sleepy minute = " + sleepiestGuardPeakMinute);
            System.out.println("Result = " + sleepiestGuardId * sleepiestGuardPeakMinute);
            System.out.println("Part 2: Sleepiest ever guard minute is #" + overallPeakGuardId + " and peak sleepy minute = " + overallPeakGuardMinute + " (" + overallPeakGuardValue + ")");
            System.out.println("Result = " + overallPeakGuardId * overallPeakGuardMinute);

        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private static Pattern logPattern = Pattern.compile("\\[(.+)\\] (.+)");
    private static Pattern onDutyActionPattern = Pattern.compile("Guard #(\\d+) begins shift");
    private static Pattern asleepActionPattern = Pattern.compile("falls asleep");
    private static Pattern wakeUpActionPattern = Pattern.compile("wakes up");

    private static DateTimeFormatter eventTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static class GuardEvent implements Comparable {

        public static GuardEvent create(String inputLine) {
            Matcher m = logPattern.matcher(inputLine);
            if (!m.matches()) {
                return new GuardEvent();
            }

            GuardEvent ge = new GuardEvent();
            ge.eventTime = LocalDateTime.parse(m.group(1), eventTimeFormatter);
            String eventMessage = m.group(2);
            m = onDutyActionPattern.matcher(eventMessage);
            if (m.matches()) {
                ge.eventType = GuardEventType.ON_DUTY;
                ge.guardId = Integer.parseInt(m.group(1));
            } else {
                m = asleepActionPattern.matcher(eventMessage);
                if (m.matches()) {
                    ge.eventType = GuardEventType.FALL_ASLEEP;
                } else {
                    m = wakeUpActionPattern.matcher(eventMessage);
                    if (m.matches()) {
                        ge.eventType = GuardEventType.WAKE_UP;
                    }
                }
            }
            return ge;

        }

        public GuardEventType eventType;
        public LocalDateTime eventTime;
        public Integer guardId;

        @Override
        public int compareTo(Object o) {
            if (!(o instanceof GuardEvent)) {
                return 0;
            }

            GuardEvent other = (GuardEvent)o;
            return this.eventTime.compareTo(other.eventTime);
        }

        @Override
        public String toString() {
            return "#" + guardId + ": " + eventTime.toString() + ": " + eventType;
        }
    }

    public static enum GuardEventType {
        ON_DUTY,
        FALL_ASLEEP,
        WAKE_UP
    }
}
