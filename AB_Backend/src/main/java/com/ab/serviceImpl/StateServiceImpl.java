package com.ab.serviceImpl;

import com.ab.entity.State;
import com.ab.repository.StateRepository;
import com.ab.service.StateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StateServiceImpl implements StateService {

	private final StateRepository stateRepository;

	@Override
	public List<State> getAllStates() {
		return stateRepository.findAll();
	}

	@Override
	public Optional<State> getStateByCode(String stateCode) {
		return stateRepository.findByStateCode(stateCode);
	}

	@Override
	@Transactional
	public State createOrUpdateState(State state) {
		Optional<State> existing = stateRepository.findByStateCode(state.getStateCode());
		if (existing.isPresent()) {
			State existingState = existing.get();
			existingState.setStateName(state.getStateName());
			return stateRepository.save(existingState);
		}
		return stateRepository.save(state);
	}

	@Override
	public boolean existsByStateCode(String stateCode) {
		return stateRepository.existsByStateCode(stateCode);
	}
}
