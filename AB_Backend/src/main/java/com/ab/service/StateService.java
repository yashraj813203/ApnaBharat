package com.ab.service;

import com.ab.entity.State;
import java.util.List;
import java.util.Optional;

public interface StateService {

	List<State> getAllStates();

	Optional<State> getStateByCode(String stateCode);

	State createOrUpdateState(State state);

	boolean existsByStateCode(String stateCode);
}
