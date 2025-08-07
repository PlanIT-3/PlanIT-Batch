package woojooin.planitbatch.domain.mapper;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import woojooin.planitbatch.domain.dto.UserProductQuarterData;
import woojooin.planitbatch.domain.vo.IsaTaxSavingHistoryVo;

public interface IsaTaxSavingMapper {
	List<UserProductQuarterData> selectIsaProductProfitByMember();

	int upsertIsaTaxSavingHistory(IsaTaxSavingHistoryVo vo);

	int upsertIsaTaxSavingHistoryBatch(@Param("items") List<IsaTaxSavingHistoryVo> items);

}
