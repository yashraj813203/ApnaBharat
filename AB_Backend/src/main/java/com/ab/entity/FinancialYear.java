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
    name = "financial_year_master",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_district_year",
        columnNames = {"district_id", "financial_year"}
    ),
    indexes = {
        @Index(name = "idx_financial_year", columnList = "financial_year")
    }
)
public class FinancialYear extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "district_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_year_district")
    )
    private District district;

    @Column(name = "financial_year", nullable = false, length = 9)
    private String financialYear; // e.g., "2024-2025"
}
