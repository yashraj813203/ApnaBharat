package com.ab.repository;

import com.ab.entity.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StateRepository extends JpaRepository<State, UUID> {

    Optional<State> findByStateCode(String stateCode);

    Optional<State> findByStateNameIgnoreCase(String stateName);

    boolean existsByStateCode(String stateCode);
}
