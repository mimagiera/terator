package com.terator.service.inductionLoops;

import com.opencsv.bean.CsvToBeanBuilder;
import com.terator.model.inductionLoops.AggregatedTrafficBySegment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.List;


@Service
public class ReadAggregatedTrafficBySegmentFromCsvService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReadAggregatedTrafficBySegmentFromCsvService.class);

    public Iterable<AggregatedTrafficBySegment> getAllFromCsv() {
        URL resource =
                ReadAggregatedTrafficBySegmentFromCsvService.class.getClassLoader()
                        .getResource("aggregated_traffic_by_segment.csv");
        try {
            File file = Paths.get(resource.toURI()).toFile();
            List<AggregatedTrafficBySegment> aggregatedTrafficBySegments = new CsvToBeanBuilder(new FileReader(file))
                    .withType(AggregatedTrafficBySegment.class)
                    .build()
                    .parse();

            return aggregatedTrafficBySegments;
        } catch (FileNotFoundException | URISyntaxException e) {
            LOGGER.error("Cannot read from csv", e);
            return List.of();
        }
    }

}
