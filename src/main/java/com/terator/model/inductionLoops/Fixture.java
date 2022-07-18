package com.terator.model.inductionLoops;

import com.opencsv.bean.CsvBindByPosition;
import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Fixture {
    @CsvBindByPosition(position = 0)
    String uid;
    @CsvBindByPosition(position = 1)
    Integer segmentId;
    @CsvBindByPosition(position = 2)
    String label;
    @CsvBindByPosition(position = 3)
    String main_lclass;
    @CsvBindByPosition(position = 4)
    String lclass;
    @CsvBindByPosition(position = 5)
    BigDecimal amfrom;
    @CsvBindByPosition(position = 6)
    BigDecimal amto;
    @CsvBindByPosition(position = 7)
    BigDecimal amfrom_value;
    @CsvBindByPosition(position = 8)
    BigDecimal amto_value;
    @CsvBindByPosition(position = 9)
    BigDecimal lfr;
    @CsvBindByPosition(position = 10)
    BigDecimal fixture_power;
    @CsvBindByPosition(position = 11)
    BigDecimal lon;
    @CsvBindByPosition(position = 12)
    BigDecimal lat;
    @CsvBindByPosition(position = 13)
    Integer fixture_id;
    @CsvBindByPosition(position = 14)
    String main_lgroup;
    @CsvBindByPosition(position = 15)
    String geom;
}
