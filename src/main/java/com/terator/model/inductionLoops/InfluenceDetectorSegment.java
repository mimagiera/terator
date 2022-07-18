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

@Entity
@Table(name = "influence_detector_segment")
@IdClass(InfluenceDetectorSegmentId.class)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class InfluenceDetectorSegment {
    @Id
    @Column(name = "detector_id")
    @CsvBindByPosition(position = 0)
    private Integer detectorId;

    @Id
    @Column(name = "segment_id")
    @CsvBindByPosition(position = 1)
    private Integer segmentId;
    @CsvBindByPosition(position = 2)
    private Integer influence;
}
