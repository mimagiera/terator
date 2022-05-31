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
import java.time.LocalDateTime;

@Entity
@Table(name = "traffic")
@IdClass(TrafficId.class)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class Traffic {
    @Id
    @Column(name = "detector_id")
    Integer detectorId;
    @Id
    LocalDateTime starttime;
    @Id
    LocalDateTime endtime;
    Integer count;
}
