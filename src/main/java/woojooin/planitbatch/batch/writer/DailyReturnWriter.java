package woojooin.planitbatch.batch.writer;

import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;
import woojooin.planitbatch.domain.mapper.DailyReturnMapper;
import woojooin.planitbatch.domain.vo.DailyReturn;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DailyReturnWriter implements ItemWriter<DailyReturn> {
    private final SqlSessionFactory sqlSessionFactory;

    @Override
    public void write(List<? extends DailyReturn> items) {
        try (SqlSession session = sqlSessionFactory.openSession(ExecutorType.BATCH)) {
            for (DailyReturn item : items) {
                session.insert("woojooin.planitbatch.domain.mapper.DailyReturnMapper.insert", item);
            }
            session.flushStatements();
        }
    }
}