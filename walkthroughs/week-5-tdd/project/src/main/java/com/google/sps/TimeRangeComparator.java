package com.google.sps;

import java.util.Comparator;

public final class TimeRangeComparator implements Comparator<TimeRange> {
    public int compare(TimeRange a, TimeRange b) {
        return Long.compare(a.start(), b.start());
    }
}