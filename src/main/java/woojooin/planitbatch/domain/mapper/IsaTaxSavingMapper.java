package woojooin.planitbatch.domain.mapper;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import woojooin.planitbatch.domain.dto.UserProductQuarterData;
import woojooin.planitbatch.domain.vo.IsaTaxSavingHistoryVo;

@Mapper
public interface IsaTaxSavingMapper {
	List<UserProductQuarterData> selectIsaProductProfitByMember();

	void insertTaxSavingHistory(IsaTaxSavingHistoryVo history);

	BigDecimal selectLatestAccumulatedSaving(@Param("memberId") Long memberId);
}
