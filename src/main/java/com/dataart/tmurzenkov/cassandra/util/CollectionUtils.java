package com.dataart.tmurzenkov.cassandra.util;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Collection utils.
 *
 * @author tmurzenkov
 */
public interface CollectionUtils {
    /**
     * Finds set difference.
     *
     * @param set1 {@link Set}
     * @param set2 {@link Set}
     * @param <T>  generic type
     * @return {@link Set}
     */
    static <T> Set<T> difference(final Set<T> set1, final Set<T> set2) {
        final Set<T> larger = set1.size() > set2.size() ? set1 : set2;
        final Set<T> smaller = larger.equals(set1) ? set2 : set1;
        return larger.stream().filter(n -> !smaller.contains(n)).collect(Collectors.toSet());
    }
}
