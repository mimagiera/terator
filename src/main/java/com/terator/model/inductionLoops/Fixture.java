package com.terator.model.inductionLoops;

import com.opencsv.bean.CsvBindByPosition;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "fixtures")
@IdClass(FixtureId.class)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Fixture {
    @CsvBindByPosition(position = 0)
    String uid;
    @Id
    @Column(name = "rseg_id")
    @CsvBindByPosition(position = 1)
    Integer segmentId;
    @CsvBindByPosition(position = 2)
    String label;
    @CsvBindByPosition(position = 3)
    String main_lclass;
    @CsvBindByPosition(position = 4)
    String lclass;
    @Id
    @CsvBindByPosition(position = 5)
    BigDecimal amfrom;
    @Id
    @CsvBindByPosition(position = 6)
    BigDecimal amto;
    @Id
    @CsvBindByPosition(position = 7)
    BigDecimal amfrom_value;
    @Id
    @CsvBindByPosition(position = 8)
    BigDecimal amto_value;
    @Id
    @CsvBindByPosition(position = 9)
    BigDecimal lfr;
    @CsvBindByPosition(position = 10)
    BigDecimal fixture_power;
    @CsvBindByPosition(position = 11)
    BigDecimal lon;
    @CsvBindByPosition(position = 12)
    BigDecimal lat;
    @Id
    @CsvBindByPosition(position = 13)
    Integer fixture_id;
    @CsvBindByPosition(position = 14)
    String main_lgroup;
    @CsvBindByPosition(position = 15)
    String geom;
}
