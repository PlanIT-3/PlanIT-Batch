package woojooin.planitbatch.domain.product.mapper;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import woojooin.planitbatch.domain.product.vo.EtfDailyHistory;

@Mapper
public interface EtfDailyHistoryMapper {
	EtfDailyHistory findById(long id);

	int save(EtfDailyHistory etfDailyHistory);

	int saveAll(List<EtfDailyHistory> etfDailyHistories);

	List<EtfDailyHistory> findByProductAfterStart(@Param("shortenCode") String shortenCode,
		@Param("startDate") LocalDate startDate);

	int update(EtfDailyHistory etfDailyHistory);

	List<EtfDailyHistory> findAll();
}
