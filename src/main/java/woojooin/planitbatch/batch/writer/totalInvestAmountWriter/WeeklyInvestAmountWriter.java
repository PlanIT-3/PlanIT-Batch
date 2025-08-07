package woojooin.planitbatch.batch.writer.totalInvestAmountWriter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import woojooin.planitbatch.domain.mapper.totalInvestAmount.WeeklyInvestSummaryMapper;
import woojooin.planitbatch.domain.vo.totalInvestAmountVo.WeeklyInvestSummaryVo;

import java.sql.Timestamp;
import java.util.List;

@Component
@Slf4j
public class WeeklyInvestAmountWriter implements ItemWriter<WeeklyInvestSummaryVo> {

    @Autowired
    private WeeklyInvestSummaryMapper weeklyInvestSummaryMapper;

    @Override
    @Transactional
    public void write(List<? extends WeeklyInvestSummaryVo> items) throws Exception {
        if (items == null || items.isEmpty()) {
            return;
        }

        int successCount = 0;
        int skipCount = 0;
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());

        try {
            for (WeeklyInvestSummaryVo item : items) {
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

                // weekly_report 테이블에 INSERT
                weeklyInvestSummaryMapper.insert(item);
                successCount++;
            }

            log.info("주간 투자 집계 Writer 처리 완료 - 성공: {}건, 스킵: {}건", successCount, skipCount);

        } catch (Exception e) {
            log.error("주간 투자 집계 Writer 예외 발생 - 성공: {}건, 스킵: {}건, 오류: {}",
                    successCount, skipCount, e.getMessage(), e);

            // rollback 원인 확인
            System.err.println("WeeklyWriter 예외 발생: " + e.getMessage());
            throw e; // 예외 다시 던져서 Spring Batch가 인지하게
        }
    }
}
