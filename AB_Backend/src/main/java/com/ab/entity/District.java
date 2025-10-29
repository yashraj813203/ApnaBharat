package com.ab.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
    name = "district_master",
    uniqueConstraints = @UniqueConstraint(name = "uk_district_code", columnNames = "district_code"),
    indexes = {
        @Index(name = "idx_district_code", columnList = "district_code"),
        @Index(name = "idx_district_name", columnList = "district_name")
    }
)
public class District extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "state_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_district_state")
    )
    private State state;

    @Column(name = "district_code", nullable = false, length = 10)
    private String districtCode;

    @Column(name = "district_name", nullable = false, length = 100)
    private String districtName;
}
