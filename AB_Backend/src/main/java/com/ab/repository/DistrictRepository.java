package com.ab.repository;

import com.ab.entity.District;
import com.ab.entity.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DistrictRepository extends JpaRepository<District, UUID> {

    Optional<District> findByDistrictCode(String districtCode);

    List<District> findByState(State state);

    boolean existsByDistrictCode(String districtCode);
}
