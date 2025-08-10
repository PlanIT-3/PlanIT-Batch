package woojooin.planitbatch.batch.processor.totalInvestProcessor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import woojooin.planitbatch.domain.vo.DailyInvestSummaryVo;
import woojooin.planitbatch.domain.vo.totalInvestAmountVo.MemberVo;
import woojooin.planitbatch.domain.vo.totalInvestAmountVo.MemberProductVo;

import org.springframework.batch.item.ItemProcessor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;

@Slf4j
@Component
public class DailyInvestAmountProcessor implements ItemProcessor<MemberVo, DailyInvestSummaryVo> {

    @Override
    public DailyInvestSummaryVo process(MemberVo member) throws Exception {
        if (member == null || member.getProducts() == null || member.getProducts().isEmpty()) {
            return null;
        }
          Map<String, BigDecimal> dailyAmountMap = new HashMap<>();
          Map<String, Long> dailyCountMap = new HashMap<>();
          Map<String, BigDecimal> dailyValuationMap = new HashMap<>();


        for (MemberProductVo product : member.getProducts()) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(product.getUpdatedAt());

            String dailyKey = String.format("%d-%02d-%02d",
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH) + 1,
                    cal.get(Calendar.DAY_OF_MONTH));

            BigDecimal totalAmount = product.getPurchaseAmount().multiply(product.getQuantity());
            BigDecimal valuationAmount = product.getValuationAmount().multiply(product.getQuantity());
            dailyAmountMap.merge(dailyKey, totalAmount, BigDecimal::add);
            dailyValuationMap.merge(dailyKey, valuationAmount, BigDecimal::add);
            dailyCountMap.merge(dailyKey, 1L, Long::sum);
        }

        DailyInvestSummaryVo summaryVo = new DailyInvestSummaryVo();
        summaryVo.setMemberId(member.getMemberId());

        if (!dailyAmountMap.isEmpty()) {
            String latestDate = dailyAmountMap.keySet().stream()
                    .max(String::compareTo)
                    .orElse("");

            summaryVo.setDailyTotal(dailyAmountMap.getOrDefault(latestDate, BigDecimal.ZERO));
            summaryVo.setDailyValuationTotal(dailyValuationMap.getOrDefault(latestDate, BigDecimal.ZERO));
            summaryVo.setDailyCount(dailyCountMap.getOrDefault(latestDate, 0L).intValue());
        } else {
            summaryVo.setDailyTotal(BigDecimal.ZERO);
            summaryVo.setDailyValuationTotal(BigDecimal.ZERO);
            summaryVo.setDailyCount(0);
        }


        summaryVo.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

        return summaryVo;
    }

    }

