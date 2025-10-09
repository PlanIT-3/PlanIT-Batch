package woojooin.planitbatch.domain.mapper.totalInvestAmount;

import org.apache.ibatis.annotations.Param;
import woojooin.planitbatch.domain.vo.DailyInvestSummaryVo;

import java.util.Date;
import java.util.List;

public interface DailyInvestSummaryMapper {
    int insert(DailyInvestSummaryVo dailyInvestSummaryVo);

    int batchInsert(@Param("dailyInvestSummaryList") List<DailyInvestSummaryVo> dailyInvestSummaryList);
}
