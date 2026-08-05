package com.repsync.util;

import com.opencsv.CSVWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Utility class for exporting data to CSV files.
 */
public class CSVExporter {

    /**
     * Export data to a CSV file.
     * 
     * @param filePath the output file path
     * @param headers column headers
     * @param data rows of data (each row is a String array)
     * @return true if export was successful
     */
    public static boolean exportToCSV(String filePath, String[] headers, List<String[]> data) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            // Write header row
            writer.writeNext(headers);

            // Write data rows
            for (String[] row : data) {
                writer.writeNext(row);
            }

            System.out.println("CSV exported to: " + filePath);
            return true;

        } catch (IOException e) {
            System.err.println("Error exporting CSV: " + e.getMessage());
            return false;
        }
    }
}
