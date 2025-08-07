package woojooin.planitbatch.domain.mapper;

import java.util.List;


import woojooin.planitbatch.domain.dto.UserProductQuarterData;
import woojooin.planitbatch.domain.vo.IsaTaxSavingHistoryVo;

public interface IsaTaxSavingMapper {
	List<UserProductQuarterData> selectIsaProductProfitByMember();

	int upsertIsaTaxSavingHistory(IsaTaxSavingHistoryVo vo);

}
