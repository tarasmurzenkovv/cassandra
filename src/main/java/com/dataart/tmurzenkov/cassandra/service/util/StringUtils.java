package com.dataart.tmurzenkov.cassandra.service.util;

/**
 * Why the heck this class was not added in JDK???
 *
 * @author tmurzenkov
 */
public final class StringUtils {
    private StringUtils() {
    }

    /**
     * Is empty? True if it is.
     *
     * @param givenString {@link String}
     * @return true if it is.
     */
    public static boolean isEmpty(String givenString) {
        return (null == givenString) || "".equals(givenString);
    }
}
