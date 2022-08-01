package com.terator.service.inductionLoops;

import com.terator.model.inductionLoops.AggregatedTrafficBySegment;
import com.terator.service.inductionLoops.csv.ReadAggregatedTrafficBySegmentFromCsvService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.terator.service.TeratorExecutor.printElapsedTime;
import static java.time.DayOfWeek.FRIDAY;
import static java.time.DayOfWeek.MONDAY;
import static java.time.DayOfWeek.THURSDAY;
import static java.time.DayOfWeek.TUESDAY;
import static java.time.DayOfWeek.WEDNESDAY;

@Service
@RequiredArgsConstructor
public class AggregatedTrafficBySegmentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AggregatedTrafficBySegmentService.class);

    private static final Set<DayOfWeek> WEEK_DAYS_NO_WEEKEND = Set.of(MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY);

    private final ReadAggregatedTrafficBySegmentFromCsvService readAggregatedTrafficBySegmentFromCsvService;

    public Map<Integer, Set<AggregatedTrafficBySegment>> getAggregatedTrafficInWeekdaysBySegments() {
        LOGGER.info("Starting getting aggregated data from induction loops");
        long start = System.currentTimeMillis();

        final Map<Integer, Set<AggregatedTrafficBySegment>> aggregatedData =
                StreamSupport.stream(getAll().spliterator(), false)
                        .filter(traffic -> WEEK_DAYS_NO_WEEKEND.contains(traffic.getDate().getDayOfWeek()))
                        .collect(Collectors.groupingBy(AggregatedTrafficBySegment::getSegmentId, Collectors.toSet()));

        long end = System.currentTimeMillis();
        printElapsedTime(start, end, "getting aggregated data from induction loops", LOGGER);
        return aggregatedData;
    }

    private Iterable<AggregatedTrafficBySegment> getAll() {
        return readAggregatedTrafficBySegmentFromCsvService.findAll();
    }
}
