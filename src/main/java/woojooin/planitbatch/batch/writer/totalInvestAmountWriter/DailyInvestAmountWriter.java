package woojooin.planitbatch.batch.writer.totalInvestAmountWriter;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import woojooin.planitbatch.domain.mapper.totalInvestAmount.DailyInvestSummaryMapper;
import woojooin.planitbatch.domain.vo.DailyInvestSummaryVo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class DailyInvestAmountWriter implements ItemWriter<DailyInvestSummaryVo> {
    @Autowired
    private DailyInvestSummaryMapper dailyInvestSummaryMapper;

    @Override
    @Transactional
    public void write(List<? extends DailyInvestSummaryVo> items) throws Exception {

            try {
                for (DailyInvestSummaryVo item : items) {
                    if (item == null || item.getMemberId() == null) continue;
                    item.setUpdatedAt(new Date());
                    dailyInvestSummaryMapper.insert(item);
                }
            } catch (Exception e) {
                // rollback 원인 확인
                System.err.println("Writer 예외 발생: " + e.getMessage());
                throw e;
            }
        }
    }
