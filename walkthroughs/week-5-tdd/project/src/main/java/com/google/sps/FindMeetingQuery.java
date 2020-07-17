// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Comparator;
import java.util.Collections;
import java.util.LinkedHashMap;

public final class FindMeetingQuery {
    public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
        ArrayList<TimeRange> mandatoryAttendeesUnavailableTimes = new ArrayList<TimeRange>();
        ArrayList<TimeRange> optionalAttendeesUnavailableTimes = new ArrayList<TimeRange>();
        HashMap<String, ArrayList<TimeRange>> optionalAttendeeUnavailableTimes = new HashMap<String, ArrayList<TimeRange>>();
        for (String optionalAttendee : request.getOptionalAttendees()) {
            optionalAttendeeUnavailableTimes.put(optionalAttendee, new ArrayList<TimeRange>());
        }
        for (Event event : events) {
            HashSet<String> intersection;
            intersection = new HashSet<String>();
            intersection.addAll(event.getAttendees());
            intersection.retainAll(request.getAttendees());
            // If a mandatory attendee has an event, the time slot does not allow mandatory and optional attendees to attend.
            if (intersection.size() > 0) {
                mandatoryAttendeesUnavailableTimes.add(event.getWhen());
                optionalAttendeesUnavailableTimes.add(event.getWhen());
            }
            intersection = new HashSet<String>();
            intersection.addAll(event.getAttendees());
            intersection.retainAll(request.getOptionalAttendees());
            // If an optional attendee has an event, the time slot does not allow optional attendees to attend.
            if (intersection.size() > 0) {
                optionalAttendeesUnavailableTimes.add(event.getWhen());
            }
            for (String optionalAttendee : intersection) {
                optionalAttendeeUnavailableTimes.get(optionalAttendee).add(event.getWhen());
            }
        }
        ArrayList<TimeRange> mandatoryAttendeesAvailableTimes = findAvailableTimes(mandatoryAttendeesUnavailableTimes, request);
        ArrayList<TimeRange> optionalAttendeesAvailableTimes = findAvailableTimes(optionalAttendeesUnavailableTimes, request);
        // Find the time slot(s) that allow mandatory attendees and the greatest possible number of optional attendees to attend.
        HashMap<String, Integer> optionalAttendeesAvailableTimeValues = new HashMap<String, Integer>();
        for (int i = 0; i < optionalAttendeesAvailableTimes.size(); i++) {
            optionalAttendeesAvailableTimeValues.put(i + "0", optionalAttendeesAvailableTimes.get(i).start());
            optionalAttendeesAvailableTimeValues.put(i + "1", optionalAttendeesAvailableTimes.get(i).end());
        }
        // Sort time values.
        ArrayList<Map.Entry<String, Integer>> list = new ArrayList<Map.Entry<String, Integer>>();
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });
        HashMap<String, Integer> optionalAttendeesAvailableTimeValuesSorted = new LinkedHashMap<String, Integer>();
        for (Map.Entry<String, Integer> entry : list) {
            optionalAttendeesAvailableTimeValuesSorted.put(entry.getKey(), entry.getValue());
        }
        // Find the time values when the maximum number of attendees are available.
        int maximum = 0;
        ArrayList<Integer> optionalAttendeesOptimalAvailableTimes = new ArrayList<Integer>();
        int count = 0;
        for (Map.Entry<String, Integer> entry : optionalAttendeesAvailableTimeValuesSorted.entrySet()) {
            switch (entry.getKey().charAt(1)) {
                // If the time value is a start time, increment the number of attendees that are available.
                case '0':
                    count++;
                    break;
                // If the time value is an end time, decrement the number of attendees that are available.
                case '1':
                    count--;
                    break;
            }
            if (count > maximum) {
                maximum = count;
                optionalAttendeesOptimalAvailableTimes.clear();
                optionalAttendeesOptimalAvailableTimes.add(entry.getValue());
            } else if (count == maximum) {
                optionalAttendeesOptimalAvailableTimes.add(entry.getValue());
            }
        }
        // Find the time ranges when the maximum number of attendees are available.
        HashSet<TimeRange> optimalAvailableTimesSet = new HashSet<TimeRange>();
        for (TimeRange mandatoryAttendeesAvailableTime : mandatoryAttendeesAvailableTimes) {
            for (int optionalAttendeesOptimalAvailableTime : optionalAttendeesOptimalAvailableTimes) {
                if (mandatoryAttendeesAvailableTime.contains(optionalAttendeesOptimalAvailableTime) == true) {
                    optimalAvailableTimesSet.add(mandatoryAttendeesAvailableTime);
                }
            }
        }
        ArrayList<TimeRange> optimalAvailableTimes = new ArrayList<TimeRange>(optimalAvailableTimesSet);
        if (optionalAttendeesAvailableTimes.size() > 0) {
            return optionalAttendeesAvailableTimes;
        } else {
            if (optimalAvailableTimes.size() > 0) {
                return optimalAvailableTimes;
            } else {
                return mandatoryAttendeesAvailableTimes;
            }
        }
    }

    private ArrayList<TimeRange> findAvailableTimes(ArrayList<TimeRange> unavailableTimes, MeetingRequest request) {
        // Sort time ranges by start time.
        TimeRangeComparator comparator = new TimeRangeComparator();
        unavailableTimes.sort(comparator);
        // Combine overlapping time ranges.
        int i = 0;
        while (i < unavailableTimes.size() - 1) {
            if (unavailableTimes.get(i).overlaps(unavailableTimes.get(i + 1)) == true) {
                int start = unavailableTimes.get(i).start();
                int end;
                if (Long.compare(unavailableTimes.get(i).end(), unavailableTimes.get(i + 1).end()) > 0) {
                    end = unavailableTimes.get(i).end();
                } else {
                    end = unavailableTimes.get(i + 1).end();
                }
                unavailableTimes.set(i, TimeRange.fromStartEnd(start, end, false));
                unavailableTimes.remove(i + 1);
            } else {
                i++;
            }
        }

        ArrayList<TimeRange> availableTimes = new ArrayList<TimeRange>();
        if (unavailableTimes.size() == 0) {
            // If there are no time slots that attendees are unavailable, the whole day is available.
            int start;
            int end;
            TimeRange time;
            start = TimeRange.START_OF_DAY;
            end = TimeRange.END_OF_DAY;
            time = TimeRange.fromStartEnd(start, end, true);
            if (time.duration() >= request.getDuration()) {
                availableTimes.add(time);
            }
        } else {
            // If there are time slots that attendees are unavailable, the gaps in the schedules are available.
            for (i = 0; i < unavailableTimes.size(); i++) {
                int start;
                int end;
                TimeRange time;
                if ((i == 0) && (unavailableTimes.get(i).start() != TimeRange.START_OF_DAY)) {
                    start = TimeRange.START_OF_DAY;
                    end = unavailableTimes.get(i).start();
                    time = TimeRange.fromStartEnd(start, end, end == TimeRange.END_OF_DAY);
                    if (time.duration() >= request.getDuration()) {
                        availableTimes.add(time);
                    }
                }
                if (i != 0) {
                    start = unavailableTimes.get(i - 1).end();
                    end = unavailableTimes.get(i).start();
                    time = TimeRange.fromStartEnd(start, end, end == TimeRange.END_OF_DAY);
                    if (time.duration() >= request.getDuration()) {
                        availableTimes.add(time);
                    }
                }
                if ((i == unavailableTimes.size() - 1) && (unavailableTimes.get(i).end() - 1 != TimeRange.END_OF_DAY)) {
                    start = unavailableTimes.get(i).end();
                    end = TimeRange.END_OF_DAY;
                    time = TimeRange.fromStartEnd(start, end, end == TimeRange.END_OF_DAY);
                    if (time.duration() >= request.getDuration()) {
                        availableTimes.add(time);
                    }
                }
            }
        }
        return availableTimes;
    }
}
