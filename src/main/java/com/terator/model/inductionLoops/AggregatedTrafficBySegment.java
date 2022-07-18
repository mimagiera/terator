package com.terator.model.inductionLoops;

import com.opencsv.bean.CsvBindByPosition;
import com.opencsv.bean.CsvCustomBindByPosition;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class AggregatedTrafficBySegment {
    @CsvBindByPosition(position = 0)
    Integer segmentId;
    @CsvCustomBindByPosition(position = 1, converter = LocalDateConverter.class)
    LocalDate date;
    @CsvBindByPosition(position = 2)
    Integer hour;
    @CsvBindByPosition(position = 3)
    Integer count;
}
