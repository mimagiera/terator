package com.terator.model.inductionLoops;

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
import java.time.LocalDate;

@Entity
@Table(name = "aggregated_traffic")
@IdClass(AggregatedTrafficId.class)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class AggregatedTraffic {
    @Id
    @Column(name = "detector_id")
    Integer detectorId;
    @Id
    LocalDate date;
    @Id
    Integer hour;
    Integer count;
}
