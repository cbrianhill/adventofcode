package org.thehills.brian.adventofcode.year2019;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Day8
{

    private static final int LAYER_COLUMNS = 25;
    private static final int LAYER_ROWS = 6;

    public static void main(String args[]) throws IOException
    {
        InputStream is = null;
        try {
            is = new FileInputStream(args[0]);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            Optional<String> input = reader.lines().findFirst();
            if (input.isPresent()) {
                char[] inputData = input.get().toCharArray();
                int rowIndex = 0, colIndex = 0;
                Layer l = null;
                List<Layer> allLayers = new ArrayList<>();
                int layerIndex = 0;
                for (char x : inputData) {
                    if (rowIndex == 0 && colIndex == 0) {
                        l = new Layer(layerIndex++);
                        allLayers.add(l);
                    }
                    l.data[rowIndex][colIndex++] = x;
                    if (colIndex >= LAYER_COLUMNS && rowIndex >= LAYER_ROWS - 1) {
                        rowIndex = 0;
                        colIndex = 0;
                    } else if (colIndex >= LAYER_COLUMNS) {
                        ++rowIndex;
                        colIndex = 0;
                    }
                }
                Layer bestLayer = null;
                int fewestZeroes = Integer.MAX_VALUE;
                for (Layer x : allLayers) {
                    int zeroes = x.countChars('0');
                    if (zeroes < fewestZeroes) {
                        bestLayer = x;
                        fewestZeroes = zeroes;
                    }
                }

                System.out.println("Layer " + bestLayer.index + " has the fewest zeroes (" + fewestZeroes + ").");
                System.out.println("Answer: " + bestLayer.countChars('1') * bestLayer.countChars('2'));

                Optional<Layer> result = Optional.empty();
                for (int z = allLayers.size() - 1; z >= 0; --z) {
                    if (!result.isPresent()) {
                        result = Optional.of(allLayers.get(z));
                    } else {
                        result = Optional.of(allLayers.get(z).putInFrontOf(result.get()));
                    }
                }
                System.out.print(result.get());
            }
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public static class Layer {

        public Layer(int index) {
            this.index = index;
        }

        public int index;
        public char[][] data = new char[LAYER_ROWS][LAYER_COLUMNS];

        public int countChars(char m) {
            int matches = 0;
            for (int y = 0; y < LAYER_ROWS; ++y) {
                for (int x = 0; x < LAYER_COLUMNS; ++x) {
                    if (data[y][x] == m) {
                        ++matches;
                    }
                }
            }
            return matches;
        }

        public Layer putInFrontOf(Layer back) {
            Layer result = new Layer(0);
            for (int y = 0; y < LAYER_ROWS; ++y) {
                for (int x = 0; x < LAYER_COLUMNS; ++x) {
                    if (data[y][x] == '2') {
                        result.data[y][x] = back.data[y][x];
                    } else {
                        result.data[y][x] = data[y][x];
                    }
                }
            }
            return result;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (int y = 0; y < LAYER_ROWS; ++y)
            {
                for (int x = 0; x < LAYER_COLUMNS; ++x)
                {
                    if (data[y][x] != '0') sb.append(data[y][x]);
                    else sb.append(' ');
                }
                sb.append('\n');
            }
            return sb.toString();

        }
    }

}
