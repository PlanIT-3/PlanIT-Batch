package woojooin.planitbatch.batch.job;

import lombok.RequiredArgsConstructor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//import woojooin.planitbatch.batch.reader.DailyReturnReader;
import woojooin.planitbatch.batch.writer.DailyReturnWriter;
import woojooin.planitbatch.domain.mapper.DailyReturnMapper;
import woojooin.planitbatch.domain.vo.DailyReturn;
import org.springframework.beans.factory.annotation.Value;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@EnableBatchProcessing
@RequiredArgsConstructor
@Configuration
public class DailyReturnBatchJob {

    private final JobBuilderFactory jobs;
    private final StepBuilderFactory steps;
//    private final DailyReturnReader dailyReturnReader;
    private final DailyReturnWriter writer;
    private final SqlSessionFactory sqlSessionFactory;


    @Bean
    @StepScope
    public MyBatisPagingItemReader<DailyReturn> reader(
            @Value("#{jobParameters['date']}") String dateStr) {
        MyBatisPagingItemReader<DailyReturn> reader = new MyBatisPagingItemReader<>();

        reader.setSqlSessionFactory(sqlSessionFactory);
        reader.setQueryId("woojooin.planitbatch.domain.mapper.DailyReturnMapper.findAllForToday");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("date", LocalDate.parse(dateStr));
        reader.setParameterValues(parameters);
        reader.setPageSize(100);

        return reader;
    }


    @Bean
    public Job dailyReturnJob(Step dailyReturnStep) {
        return jobs.get("dailyReturnJob")
                .start(dailyReturnStep)
                .build();
    }

    @Bean
    public Step dailyReturnStep(MyBatisPagingItemReader<DailyReturn> reader) {
        return steps.get("dailyReturnStep")
                .<DailyReturn, DailyReturn>chunk(100)
                .reader(reader)
                .writer(writer)
                .build();
    }
}
