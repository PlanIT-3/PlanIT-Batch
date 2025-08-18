package woojooin.planitbatch.domain.rebalance.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import woojooin.planitbatch.domain.rebalance.vo.Rebalance;

@Mapper
public interface RebalanceMapper {

	/** 단일 리밸런싱 내역 저장 */
	int insert(Rebalance rebalance);

	/** 여러 리밸런싱 내역 배치 저장 */
	int insertAll(@Param("list") List<Rebalance> rebalances);

	/** goal_id 로 조회 */
	List<Rebalance> selectByGoalId(@Param("goalId") Long goalId);
}