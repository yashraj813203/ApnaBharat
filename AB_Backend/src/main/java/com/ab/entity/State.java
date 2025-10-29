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
    name = "state_master",
    uniqueConstraints = @UniqueConstraint(name = "uk_state_code", columnNames = "state_code")
)
public class State extends BaseEntity {

    @Column(name = "state_code", nullable = false, length = 10)
    private String stateCode;

    @Column(name = "state_name", nullable = false, length = 100)
    private String stateName;
}
