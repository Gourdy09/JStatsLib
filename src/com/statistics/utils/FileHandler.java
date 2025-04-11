package com.statistics.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * FileHandler reads data from a file and inserts it into the RBTree.
 * 
 * Attributes:
 * - path : Path to the file
 * - delimiter : Delimiter to split file content
 * 
 * Written By:
 * - Om Patel
 */
public class FileHandler {
    private String path;
    private String delimiter;
    private RBTree tree;

    public FileHandler(String path, String delimiter, RBTree tree) {
        this.path = path;
        this.delimiter = delimiter;
        this.tree = tree;
    }

    public void readFile() {
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line = br.readLine();
            while (line != null) {
                String[] data = line.split(delimiter);
                for (String item : data) {
                    try {
                        double value = Double.parseDouble(item.trim());
                        insertParsedValue(value);
                    } catch (NumberFormatException e) {
                        System.err.println("Skipping invalid value: " + item);
                    }
                }
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void insertParsedValue(Double item) {
        tree.insert(item);
    }
}
