package woojooin.planitbatch.batch.processor.totalInvestProcessor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import woojooin.planitbatch.domain.vo.DailyInvestSummaryVo;
import woojooin.planitbatch.domain.vo.totalInvestAmountVo.WeeklyInvestSummaryVo;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Component
@StepScope
@Slf4j
public class WeeklyInvestAmountProcessor implements ItemProcessor<List<DailyInvestSummaryVo>, WeeklyInvestSummaryVo> {

    @Override
    public WeeklyInvestSummaryVo process(List<DailyInvestSummaryVo> memberDailyList) throws Exception {
        if (memberDailyList == null || memberDailyList.isEmpty()) {
            log.debug("빈 멤버 일별 데이터 리스트 - 스킵");
            return null;
        }

        DailyInvestSummaryVo firstRecord = memberDailyList.get(0);
        Long memberId = firstRecord.getMemberId();

        if (memberId == null) {
            log.debug("멤버 ID가 null인 데이터 - 스킵");
            return null;
        }

        BigDecimal weeklyTotalAmount = BigDecimal.ZERO;
        BigDecimal weeklyValuationTotal = BigDecimal.ZERO;
        int weeklyTotalCount = 0;
        int dailyDataCount = 0;

        for (DailyInvestSummaryVo dailyData : memberDailyList) {
            if (dailyData.getDailyTotal() == null) {
                continue;
            }

            if (!memberId.equals(dailyData.getMemberId())) {
                continue;
            }

            weeklyTotalAmount = weeklyTotalAmount.add(dailyData.getDailyTotal());
            weeklyValuationTotal = weeklyValuationTotal.add(dailyData.getDailyValuationTotal());
            weeklyTotalCount += dailyData.getDailyCount();
            dailyDataCount++;
        }

        // 처리된 데이터가 없으면 스킵
        if (dailyDataCount == 0) {
            return null;
        }

        WeeklyInvestSummaryVo weeklyMemberSummary = new WeeklyInvestSummaryVo();
        weeklyMemberSummary.setMemberId(memberId);
        weeklyMemberSummary.setWeeklyTotalAmount(weeklyTotalAmount);
        weeklyMemberSummary.setWeeklyTotalCount(weeklyTotalCount);
        weeklyMemberSummary.setWeeklyValuationTotal(weeklyValuationTotal);

        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        weeklyMemberSummary.setCreatedAt(currentTime);
        weeklyMemberSummary.setUpdatedAt(currentTime);
        weeklyMemberSummary.setIsDeleted(false);

        return weeklyMemberSummary;
    }
}