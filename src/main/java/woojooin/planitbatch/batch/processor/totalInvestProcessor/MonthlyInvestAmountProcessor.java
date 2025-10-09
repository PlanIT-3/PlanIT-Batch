package woojooin.planitbatch.batch.processor.totalInvestProcessor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import woojooin.planitbatch.domain.vo.totalInvestAmountVo.MonthlyInvestSummaryVo;
import woojooin.planitbatch.domain.vo.totalInvestAmountVo.WeeklyInvestSummaryVo;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Component
@StepScope
@Slf4j
public class MonthlyInvestAmountProcessor implements ItemProcessor<List<WeeklyInvestSummaryVo>, MonthlyInvestSummaryVo> {
    @Override
    public MonthlyInvestSummaryVo process(List<WeeklyInvestSummaryVo> memberWeeklyList) throws Exception {
        if (memberWeeklyList == null || memberWeeklyList.isEmpty()) {
            return null;
        }

        WeeklyInvestSummaryVo firstRecord = memberWeeklyList.get(0);
        Long memberId = firstRecord.getMemberId();

        if (memberId == null) {
            return null;
        }


        BigDecimal monthlyTotalAmount = BigDecimal.ZERO;
        BigDecimal monthlyValuationTotal = BigDecimal.ZERO;
        int monthlyTotalCount = 0;
        int weeklyDataCount = 0;

        for (WeeklyInvestSummaryVo weeklyData : memberWeeklyList) {
            if (weeklyData.getWeeklyTotalAmount() == null) {
                continue;
            }

            // 멤버 ID 일치 확인 (안전장치)
            if (!memberId.equals(weeklyData.getMemberId())) {
                log.warn("멤버 ID 불일치! 예상: {}, 실제: {} - 데이터 스킵", memberId, weeklyData.getMemberId());
                continue;
            }

            monthlyTotalAmount = monthlyTotalAmount.add(weeklyData.getWeeklyTotalAmount());
            monthlyValuationTotal = monthlyValuationTotal.add(weeklyData.getWeeklyValuationTotal());
            monthlyTotalCount += weeklyData.getWeeklyTotalCount();
            weeklyDataCount++;
        }

        if (weeklyDataCount == 0) {
            log.debug("멤버 {} - 유효한 주간 데이터가 없음", memberId);
            return null;
        }
        
        MonthlyInvestSummaryVo monthlyMemberSummary = new MonthlyInvestSummaryVo();
        monthlyMemberSummary.setMemberId(memberId);
        monthlyMemberSummary.setMonthlyTotalAmount(monthlyTotalAmount);
        monthlyMemberSummary.setMonthlyValuationTotal(monthlyValuationTotal);
        monthlyMemberSummary.setMonthlyTotalCount(monthlyTotalCount);

        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        monthlyMemberSummary.setCreatedAt(currentTime);
        monthlyMemberSummary.setUpdatedAt(currentTime);
        monthlyMemberSummary.setIsDeleted(false);

        return monthlyMemberSummary;
    }
}
