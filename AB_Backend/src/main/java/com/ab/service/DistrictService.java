package com.ab.service;

import com.ab.entity.District;
import com.ab.entity.State;
import java.util.List;
import java.util.Optional;

public interface DistrictService {

	List<District> getDistrictsByState(State state);

	Optional<District> getDistrictByCode(String districtCode);

	District createOrUpdateDistrict(District district);

	boolean existsByDistrictCodeAndState(String districtCode, State state);
}
