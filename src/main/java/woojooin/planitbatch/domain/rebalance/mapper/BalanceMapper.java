package woojooin.planitbatch.domain.rebalance.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import woojooin.planitbatch.domain.rebalance.vo.Balance;

@Mapper
public interface BalanceMapper {
	List<Balance> findAllBalances();

	int countAll();

	List<Balance> findBalancePanging(@Param("offset") int offset,
		@Param("limit") int limit);

	List<Balance> findBalanceRange(@Param("startGoalId") Long startGoalId, @Param("endGoalId") Long endGoalId);

	List<Balance> findBalanceChunk(@Param("startGoalId") long startGoalId,
		@Param("endGoalId") long endGoalId,
		@Param("cursorGoalId") long cursorGoalId,
		@Param("limit") int limit);
}
