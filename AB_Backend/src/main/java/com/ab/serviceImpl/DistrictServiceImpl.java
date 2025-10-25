package com.ab.serviceImpl;

import com.ab.entity.District;
import com.ab.entity.State;
import com.ab.repository.DistrictRepository;
import com.ab.service.DistrictService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DistrictServiceImpl implements DistrictService {

	private final DistrictRepository districtRepository;

	@Override
	public List<District> getDistrictsByState(State state) {
		return districtRepository.findByState(state);
	}

	@Override
	public Optional<District> getDistrictByCode(String districtCode) {
		return districtRepository.findByDistrictCode(districtCode);
	}

	@Override
	@Transactional
	public District createOrUpdateDistrict(District district) {
		Optional<District> existing = districtRepository.findByDistrictCode(district.getDistrictCode());
		if (existing.isPresent()) {
			District existingDistrict = existing.get();
			existingDistrict.setDistrictName(district.getDistrictName());
			existingDistrict.setState(district.getState());
			return districtRepository.save(existingDistrict);
		}
		return districtRepository.save(district);
	}

	@Override
	public boolean existsByDistrictCodeAndState(String districtCode, State state) {
		return districtRepository.existsByDistrictCodeAndState(districtCode, state);
	}
}
