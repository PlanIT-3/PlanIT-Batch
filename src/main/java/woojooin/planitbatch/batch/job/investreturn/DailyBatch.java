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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import woojooin.planitbatch.batch.writer.InvestMentWriter;
import woojooin.planitbatch.domain.vo.InvestmentReturn;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class DailyBatch {

    private final JobBuilderFactory jobs;
    private final StepBuilderFactory steps;
    private final SqlSessionFactory sqlSessionFactory;
    private final InvestMentWriter writer;


    @Bean
    @StepScope
    public MyBatisPagingItemReader<InvestmentReturn> dailyReturnReader(
            @Value("#{jobParameters['today']}") String todayStr)
          {
        MyBatisPagingItemReader<InvestmentReturn> reader = new MyBatisPagingItemReader<>();
        reader.setSqlSessionFactory(sqlSessionFactory);
        reader.setQueryId("woojooin.planitbatch.domain.mapper.InvestmentReturnMapper.findDailyReturn");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("today", LocalDate.parse(todayStr));

        reader.setParameterValues(parameters);
        reader.setPageSize(100);

        return reader;
    }

    @Bean
    public Step dailybatchStep(MyBatisPagingItemReader<InvestmentReturn> dailyReturnReader) {
        return steps.get("dailybatchStep")
                .<InvestmentReturn, InvestmentReturn>chunk(100)
                .reader(dailyReturnReader)
                .writer(writer)
                .build();
    }

    @Bean
    public Job dailybatchJob(Step dailybatchStep) {
        return jobs.get("dailybatchJob")
                .start(dailybatchStep)
                .build();
    }

}

