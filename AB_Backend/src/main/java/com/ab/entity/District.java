package com.ab.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.NaturalId;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "districts", uniqueConstraints = {
		@UniqueConstraint(name = "uk_districts_state_district_code", columnNames = { "state_id",
				"district_code" }) }, indexes = { @Index(name = "idx_districts_state", columnList = "state_id"),
						@Index(name = "idx_districts_district_code", columnList = "district_code"),
						@Index(name = "idx_districts_district_name", columnList = "district_name") })
public class District extends BaseEntity {

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "state_id", nullable = false, foreignKey = @ForeignKey(name = "fk_districts_state"))
	private State state;

	@NaturalId
	@Column(name = "district_code", nullable = false, length = 8)
	private String districtCode; // e.g. official LGD code

	@Column(name = "district_name", nullable = false, length = 128)
	private String districtName;

	@OneToMany(mappedBy = "district", cascade = CascadeType.ALL, orphanRemoval = false)
	@Builder.Default
	private Set<MgnregaMonthlyStat> monthlyStats = new LinkedHashSet<>();
}
