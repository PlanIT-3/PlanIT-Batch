package woojooin.planitbatch.batch.job.investreturn;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import woojooin.planitbatch.batch.writer.DailyReturnWriter;
import woojooin.planitbatch.domain.vo.DailyReturn;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableBatchProcessing
@Slf4j
@RequiredArgsConstructor
public class PastBatch {
    private final JobBuilderFactory jobs;
    private final StepBuilderFactory steps;
    private final DailyReturnWriter writer;
    private final SqlSessionFactory sqlSessionFactory;


    @Bean
    @StepScope
    public MyBatisPagingItemReader<DailyReturn> etfDailyReader(
            @Value("#{jobParameters['startDate']}") String startDate,
            @Value("#{jobParameters['endDate']}") String endDate)  {

        MyBatisPagingItemReader<DailyReturn> reader = new MyBatisPagingItemReader<>();
        log.info("Reader 생성됨 - startDate={}, endDate={}", startDate, endDate); // 추가 로그
        reader.setSqlSessionFactory(sqlSessionFactory);
        reader.setQueryId("woojooin.planitbatch.domain.mapper.DailyReturnMapper.findpast");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("startDate", LocalDate.parse(startDate));
        parameters.put("endDate", LocalDate.parse(endDate));
        reader.setParameterValues(parameters);
        reader.setPageSize(100);

        return reader;
    }


    @Bean
    public Job etfDailyReturnJob(Step etfDailyReturnStep) {
        return jobs.get("etfDailyReturnJob")
                .start(etfDailyReturnStep)
                .build();
    }

    @Bean
    public Step etfDailyReturnStep(MyBatisPagingItemReader<DailyReturn> etfDailyReader) {
        return steps.get("etfDailyReturnStep")
                .<DailyReturn, DailyReturn>chunk(100)
                .reader(etfDailyReader)
                .writer(writer)
                .build();
    }
}
