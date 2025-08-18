package woojooin.planitbatch.domain.rebalance.repository;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import woojooin.planitbatch.domain.rebalance.mapper.RebalanceMapper;
import woojooin.planitbatch.domain.rebalance.vo.Rebalance;

@Repository
@RequiredArgsConstructor
public class RebalanceRepository {

	private final RebalanceMapper rebalanceMapper;

	/** 단일 리밸런싱 내역 저장 */
	public int insert(Rebalance rebalance) {
		return rebalanceMapper.insert(rebalance);
	}

	/** 여러 리밸런싱 내역 배치 저장 */
	public int insertAll(@Param("list") List<Rebalance> rebalances) {
		return rebalanceMapper.insertAll(rebalances);
	}

	/** goal_id 로 조회 */
	public List<Rebalance> selectByGoalId(@Param("goalId") Long goalId) {
		return rebalanceMapper.selectByGoalId(goalId);
	}
}
