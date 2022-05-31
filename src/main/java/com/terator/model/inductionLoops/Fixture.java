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
    String uid;
    @Id
    @Column(name = "rseg_id")
    Integer segmentId;
    String label;
    String main_lclass;
    String lclass;
    @Id
    BigDecimal amfrom;
    @Id
    BigDecimal amto;
    @Id
    BigDecimal amfrom_value;
    @Id
    BigDecimal amto_value;
    @Id
    BigDecimal lfr;
    BigDecimal fixture_power;
    BigDecimal lon;
    BigDecimal lat;
    @Id
    Integer fixture_id;
    String main_lgroup;
    String geom;
}
