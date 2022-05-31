package com.terator.model.inductionLoops;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class InfluenceDetectorSegmentId implements Serializable {
    private Integer detectorId;
    private Integer segmentId;
}
