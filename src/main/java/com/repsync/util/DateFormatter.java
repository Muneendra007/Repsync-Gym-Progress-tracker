package com.repsync.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for formatting dates consistently across the application.
 */
public class DateFormatter {

    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy");
    public static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");
    public static final DateTimeFormatter SHORT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Format a LocalDate to "dd MMM yyyy" (e.g., "15 Jan 2024").
     */
    public static String formatDate(LocalDate date) {
        if (date == null) return "N/A";
        return date.format(DATE_FORMAT);
    }

    /**
     * Format a LocalDateTime to "dd MMM yyyy HH:mm".
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return "N/A";
        return dateTime.format(DATE_TIME_FORMAT);
    }

    /**
     * Format a LocalDate to short format "dd/MM/yyyy".
     */
    public static String formatShortDate(LocalDate date) {
        if (date == null) return "N/A";
        return date.format(SHORT_DATE);
    }

    /**
     * Format minutes to a readable duration string.
     * e.g., 90 → "1h 30m"
     */
    public static String formatDuration(int minutes) {
        if (minutes < 60) {
            return minutes + "m";
        }
        int hours = minutes / 60;
        int mins = minutes % 60;
        if (mins == 0) {
            return hours + "h";
        }
        return hours + "h " + mins + "m";
    }
}
