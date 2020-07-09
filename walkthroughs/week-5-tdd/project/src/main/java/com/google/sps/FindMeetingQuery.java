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
import java.util.*;

public final class FindMeetingQuery {
    public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
        //the time slots that mandatory attendees are unavailable
        ArrayList<TimeRange> mandatoryAttendeesUnavailableTimes = new ArrayList<TimeRange>();
        //the time slots that mandatory and optional attendees are unavailable
        ArrayList<TimeRange> optionalAttendeesUnavailableTimes = new ArrayList<TimeRange>();
        for (Event event : events) {
            HashSet<String> intersection;
            intersection = new HashSet<String>();
            intersection.addAll(event.getAttendees());
            intersection.retainAll(request.getAttendees());
            //if a mandatory attendee has an event
            if (intersection.size() > 0) {
                mandatoryAttendeesUnavailableTimes.add(event.getWhen());
                optionalAttendeesUnavailableTimes.add(event.getWhen());
            }
            intersection = new HashSet<String>();
            intersection.addAll(event.getAttendees());
            intersection.retainAll(request.getOptionalAttendees());
            //if an optional attendee has an event
            if (intersection.size() > 0) {
                optionalAttendeesUnavailableTimes.add(event.getWhen());
            }
        }
        //the time slots that mandatory attendees are available
        ArrayList<TimeRange> mandatoryAttendeesAvailableTimes = findAvailableTimes(mandatoryAttendeesUnavailableTimes, request);
        //the time slots that mandatory and optional attendees are available
        ArrayList<TimeRange> optionalAttendeesAvailableTimes = findAvailableTimes(optionalAttendeesUnavailableTimes, request);
        //if there are time slots that mandatory and optional attendees are available
        if (optionalAttendeesAvailableTimes.size() > 0) {
            return optionalAttendeesAvailableTimes;
        }
        else {
            return mandatoryAttendeesAvailableTimes;
        }
    }

    private ArrayList<TimeRange> findAvailableTimes(ArrayList<TimeRange> unavailableTimes, MeetingRequest request) {
        //sort time ranges by start time
        TimeRangeComparator comparator = new TimeRangeComparator();
        unavailableTimes.sort(comparator);
        //combine overlapping time ranges
        int i = 0;
        while (i < unavailableTimes.size() - 1) {
            if (unavailableTimes.get(i).overlaps(unavailableTimes.get(i + 1)) == true) {
                int start = unavailableTimes.get(i).start();
                int end = (Long.compare(unavailableTimes.get(i).end(), unavailableTimes.get(i + 1).end()) > 0) ? unavailableTimes.get(i).end() : unavailableTimes.get(i + 1).end();
                unavailableTimes.set(i, TimeRange.fromStartEnd(start, end, false));
                unavailableTimes.remove(i + 1);
            }
            else {
                i++;
            }
        }
        //the time slots that attendees are available
        ArrayList<TimeRange> availableTimes = new ArrayList<TimeRange>();
        //if there are no time slots that attendees are unavailable, the whole day is available
        if (unavailableTimes.size() == 0) {
            int start;
            int end;
            TimeRange time;
            start = TimeRange.START_OF_DAY;
            end = TimeRange.END_OF_DAY;
            time = TimeRange.fromStartEnd(start, end, true);
            if (time.duration() >= request.getDuration()) {
                availableTimes.add(time);
            }
        }
        //if there are time slots that attendees are unavailable, the gaps in the schedules are available
        else if (unavailableTimes.size() == 1) {
            int start;
            int end;
            TimeRange time;
            start = TimeRange.START_OF_DAY;
            end = unavailableTimes.get(i).start();
            time = TimeRange.fromStartEnd(start, end, false);
            if (time.duration() >= request.getDuration()) {
                availableTimes.add(time);
            }
            start = unavailableTimes.get(i).end();
            end = TimeRange.END_OF_DAY;
            time = TimeRange.fromStartEnd(start, end, true);
            if (time.duration() >= request.getDuration()) {
                availableTimes.add(time);
            }
        }
        else {
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
