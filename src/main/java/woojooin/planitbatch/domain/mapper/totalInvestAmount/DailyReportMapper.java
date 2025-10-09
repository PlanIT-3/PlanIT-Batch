package woojooin.planitbatch.domain.mapper.totalInvestAmount;

import woojooin.planitbatch.domain.vo.DailyInvestSummaryVo;

import java.util.List;
import java.util.Map;

public interface DailyReportMapper {
    List<DailyInvestSummaryVo> selectDailyDataForWeek(Map<String, Object> params);
    List<Long> selectMemberIdsForWeek(Map<String, Object> params);
    List<DailyInvestSummaryVo> selectMemberDailyDataForWeek(Map<String, Object> params);

}
