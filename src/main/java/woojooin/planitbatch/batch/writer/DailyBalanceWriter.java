package woojooin.planitbatch.batch.writer;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import woojooin.planitbatch.domain.mapper.DailyBalanceMapper;
import woojooin.planitbatch.domain.vo.DailyBalance;

@Slf4j
@Component
@RequiredArgsConstructor
public class DailyBalanceWriter implements ItemWriter<List<DailyBalance>> {

    private final DailyBalanceMapper dailyBalanceMapper;

    @Override
    public void write(List<? extends List<DailyBalance>> items) throws Exception {
        for (List<DailyBalance> dailyBalances : items) {
            if (dailyBalances != null && !dailyBalances.isEmpty()) {
                try {
                    dailyBalanceMapper.insertDailyBalances(dailyBalances);
                    log.debug("Inserted {} daily balance records", dailyBalances.size());
                } catch (Exception e) {
                    log.error("Error inserting daily balance records: {}", e.getMessage(), e);
                    throw e;
                }
            }
        }
    }
}