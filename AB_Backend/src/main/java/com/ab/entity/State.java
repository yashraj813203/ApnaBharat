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
@Table(name = "states", uniqueConstraints = {
		@UniqueConstraint(name = "uk_states_state_code", columnNames = { "state_code" }) }, indexes = {
				@Index(name = "idx_states_state_code", columnList = "state_code"),
				@Index(name = "idx_states_state_name", columnList = "state_name") })
public class State extends BaseEntity {

	@NaturalId
	@Column(name = "state_code", nullable = false, length = 8)
	private String stateCode; // e.g. "34" or official code

	@Column(name = "state_name", nullable = false, length = 128)
	private String stateName;

	@OneToMany(mappedBy = "state", cascade = CascadeType.ALL, orphanRemoval = false)
	@Builder.Default
	private Set<District> districts = new LinkedHashSet<>();
}
