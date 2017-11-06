package com.dataart.tmurzenkov.cassandra.util;

import java.time.LocalDate;

import static java.time.format.DateTimeFormatter.ofPattern;

/**
 * {@link LocalDate} util methods.
 *
 * @author tmurzenkov
 */
public interface DateUtils {
    /**
     * Formats the given {@link LocalDate} using the following pattern 'yyyy-MM-dd'.
     *
     * @param date {@link LocalDate}
     * @return {@link String} from the given date
     */
    static String format(LocalDate date) {
        return ofPattern("yyyy-MM-dd").format(date);
    }
}
