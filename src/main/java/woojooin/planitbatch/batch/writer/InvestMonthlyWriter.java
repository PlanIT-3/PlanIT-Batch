package woojooin.planitbatch.batch.writer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import woojooin.planitbatch.domain.vo.InvestmentReturn;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;


@Slf4j
@Component
@RequiredArgsConstructor
public class InvestMonthlyWriter implements ItemWriter<InvestmentReturn>{

    private final SqlSessionFactory sqlSessionFactory;

    @Override
    public void write(List<? extends InvestmentReturn> items) throws Exception {
        try (SqlSession session = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
            log.info("▶ Writer 실행됨. item 수: {}", items.size());
            for(InvestmentReturn item : items) {
                if(item.getAmountPast() != null&&item.getAmountPast().compareTo(BigDecimal.ZERO)>0) {
                    BigDecimal rate = item.getAmountNow()
                            .subtract(item.getAmountPast())
                            .divide(item.getAmountPast(), 4, RoundingMode.HALF_UP)
                            .multiply(new BigDecimal("100"));
                    item.setReturnRate(rate);
                } else {
                    item.setReturnRate(BigDecimal.ZERO); // 과거 금액이 0이면 0%
                }
                session.insert("woojooin.planitbatch.domain.mapper.InvestmentReturnMapper.upsertMonthlyReturn", item);
            }
            session.flushStatements(); // 모든 INSERT 실행
        }
    }
}