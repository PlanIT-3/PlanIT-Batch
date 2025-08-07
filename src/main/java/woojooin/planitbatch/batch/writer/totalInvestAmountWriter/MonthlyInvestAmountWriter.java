package woojooin.planitbatch.batch.writer.totalInvestAmountWriter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import woojooin.planitbatch.domain.mapper.totalInvestAmount.MonthlyInvestSummaryMapper;
import woojooin.planitbatch.domain.vo.totalInvestAmountVo.MonthlyInvestSummaryVo;

import java.sql.Timestamp;
import java.util.List;

@Component
@Slf4j
public class MonthlyInvestAmountWriter implements ItemWriter<MonthlyInvestSummaryVo> {
    @Autowired
    private MonthlyInvestSummaryMapper monthlyInvestSummaryMapper;

    @Override
    @Transactional
    public void write(List<? extends MonthlyInvestSummaryVo> list) throws Exception {
        if (list == null || list.isEmpty()) {
            return;
        }

        int successCount = 0;
        int skipCount = 0;
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());

        try {
            for (MonthlyInvestSummaryVo item : list) {
                if (item == null || item.getMemberId() == null) {
                    skipCount++;
                    continue;
                }

                if (item.getUpdatedAt() == null) {
                    item.setUpdatedAt(currentTime);
                }
                if (item.getCreatedAt() == null) {
                    item.setCreatedAt(currentTime);
                }
                if (item.getIsDeleted() == null) {
                    item.setIsDeleted(false);
                }

                // monthly_report 테이블에 INSERT
                monthlyInvestSummaryMapper.insert(item);
                successCount++;
            }


        } catch (Exception e) {
            log.error("주간 투자 집계 Writer 예외 발생 - 성공: {}건, 스킵: {}건, 오류: {}",
                    successCount, skipCount, e.getMessage(), e);

            // rollback 원인 확인
            System.err.println("WeeklyWriter 예외 발생: " + e.getMessage());
            throw e;
        }
    }
}
