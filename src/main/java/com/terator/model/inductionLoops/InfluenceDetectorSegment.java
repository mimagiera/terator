package com.terator.model.inductionLoops;

import com.opencsv.bean.CsvBindByPosition;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class InfluenceDetectorSegment {
    @CsvBindByPosition(position = 0)
    private Integer detectorId;
    @CsvBindByPosition(position = 1)
    private Integer segmentId;
    @CsvBindByPosition(position = 2)
    private Integer influence;
}
