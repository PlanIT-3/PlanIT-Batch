package woojooin.planitbatch.domain.rebalance.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import woojooin.planitbatch.domain.rebalance.mapper.BalanceMapper;
import woojooin.planitbatch.domain.rebalance.vo.Balance;

@Repository
@RequiredArgsConstructor
public class BalanceRepository {

	private final BalanceMapper balanceMapper;

	public List<Balance> findAllBalance() {
		return balanceMapper.findAllBalances();
	}

	public int countAll() {
		return balanceMapper.countAll();
	}

	public List<Balance> findBalancePanging(int offset, int limit) {
		return balanceMapper.findBalancePanging(offset, limit);
	}

}
