package woojooin.planitbatch.domain.product.repository;

import java.util.List;

import org.springframework.stereotype.Repository;

import lombok.AllArgsConstructor;
import woojooin.planitbatch.domain.product.mapper.EtfDailyHistoryMapper;
import woojooin.planitbatch.domain.product.vo.EtfDailyHistory;

@Repository
@AllArgsConstructor
public class EtfDailyHistoryRepository {

	private final EtfDailyHistoryMapper etfDailyHistoryMapper;

	public int saveAll(List<EtfDailyHistory> etfDailyHistories) {
		return etfDailyHistoryMapper.saveAll(etfDailyHistories);
	}
}
