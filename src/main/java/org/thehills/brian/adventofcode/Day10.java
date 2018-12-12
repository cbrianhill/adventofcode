package org.thehills.brian.adventofcode;

import de.erichseifert.gral.data.DataTable;
import de.erichseifert.gral.data.Row;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.ui.InteractivePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static javax.swing.JFrame.EXIT_ON_CLOSE;

public class Day10 {

    private static final Pattern linePattern = Pattern.compile("position=<(.+), (.+)> velocity=<(.+), (.+)>");

    public static void main(String args[]) throws IOException, InterruptedException {
        InputStream is = null;
        try {
            is = new FileInputStream(args[0]);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line;
            DataTable data = new DataTable(Integer.class, Integer.class, Integer.class, Integer.class);
            while ((line = reader.readLine()) != null) {
                Matcher m = linePattern.matcher(line);
                if (m.matches()) {
                    int x = Integer.parseInt(m.group(1).trim());
                    int y = Integer.parseInt(m.group(2).trim());
                    int vx = Integer.parseInt(m.group(3).trim());
                    int vy = Integer.parseInt(m.group(4).trim());
                    data.add(x, y, vx, vy);
                }
            }
            XYPlot graph = new XYPlot(data);
            LinePlotTest frame = new LinePlotTest();
            InteractivePanel panel = new InteractivePanel(graph);
            JButton b = new JButton("Start || Stop");
            JTextField tf = new JTextField("delay in ms");
            b.addActionListener(e -> { animate = !animate; delay = Integer.parseInt(tf.getText()); });
            panel.add(b);
            panel.add(tf);
            frame.getContentPane().add(panel);
            frame.setVisible(true);
            int seconds = 0;
            while (true) {
                if (!animate) {
                    Thread.sleep(250);
                } else {
                    int direction = 1;
                    seconds++;
                    if (delay < 0) {
                        direction = -1;
                        seconds = seconds - 2;
                    }
                    Thread.sleep(Math.abs(delay));
                    advance(data, direction);
                    panel.repaint();
                    System.out.println("Displaying second " + seconds);
                }
            }
       } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private static boolean animate = false;
    private static Integer delay = 0;

    private static void advance(DataTable data, int direction) {
        for (int i = 0; i < data.getRowCount(); ++i) {
            Row r = data.getRow(i);
            Integer newX = (Integer)r.get(0) + (Integer)r.get(2) * direction;
            Integer newY = (Integer)r.get(1) + (Integer)r.get(3) * direction;
            data.set(0, i, newX);
            data.set(1, i, newY);
        }
    }

    public static class LinePlotTest extends JFrame {
        public LinePlotTest() {
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setSize(800, 600);
            // Insert rest of the code here
        }

    }
}
