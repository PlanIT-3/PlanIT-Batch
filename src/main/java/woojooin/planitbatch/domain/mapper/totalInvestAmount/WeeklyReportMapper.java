package woojooin.planitbatch.domain.mapper.totalInvestAmount;

import woojooin.planitbatch.domain.vo.totalInvestAmountVo.WeeklyInvestSummaryVo;

import java.util.List;
import java.util.Map;

public interface WeeklyReportMapper {
    List<WeeklyInvestSummaryVo> selectWeeklyDataForMonth(Map<String, Object> params);
    List<Long> selectMemberIdsForMonth(Map<String, Object> params);
    List<WeeklyInvestSummaryVo> selectMemberWeeklyDataForMonth(Map<String, Object> params);
}
