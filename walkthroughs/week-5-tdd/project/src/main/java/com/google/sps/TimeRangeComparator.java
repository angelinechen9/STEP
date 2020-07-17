package com.google.sps;

import java.util.Comparator;

/**
 * A comparator for sorting ranges by their start time in ascending order.
 */
public final class TimeRangeComparator implements Comparator<TimeRange> {
    public int compare(TimeRange a, TimeRange b) {
        return Long.compare(a.start(), b.start());
    }
}