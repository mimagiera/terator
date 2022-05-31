package com.terator.model.inductionLoops;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class FixtureId implements Serializable {
    private Integer segmentId;
    private BigDecimal amfrom;
    private BigDecimal amto;
    private BigDecimal amfrom_value;
    private BigDecimal amto_value;
    private BigDecimal lfr;
    private Integer fixture_id;
}
