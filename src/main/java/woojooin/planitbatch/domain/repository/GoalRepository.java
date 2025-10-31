package woojooin.planitbatch.domain.repository;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import woojooin.planitbatch.domain.mapper.GoalMapper;

@Repository
@RequiredArgsConstructor
public class GoalRepository {

	private final GoalMapper goalMapper;

	public Long findMaxId() {
		return goalMapper.findMaxId();
	}

	public Long findMinId() {
		return goalMapper.findMinId();
	}

}
