package woojooin.planitbatch.batch.job;

import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;
import woojooin.planitbatch.batch.listener.JobExecutionTimeListener;
import woojooin.planitbatch.batch.processor.DailyBalanceProcessor;
import woojooin.planitbatch.batch.reader.MemberReader;
import woojooin.planitbatch.batch.writer.DailyBalanceWriter;
import woojooin.planitbatch.domain.vo.DailyBalance;
import woojooin.planitbatch.domain.vo.Member;

@Slf4j
@Configuration
public class DailyBalanceJob {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private MemberReader memberReader;

    @Autowired
    private DailyBalanceProcessor dailyBalanceProcessor;

    @Autowired
    private DailyBalanceWriter dailyBalanceWriter;

    @Autowired
    private JobExecutionTimeListener jobExecutionTimeListener;

    @Bean("memberDailyBalanceJob")  // Bean 이름 변경
    public Job createDailyBalanceJob() {
        return jobBuilderFactory.get("dailyBalanceJob")  // 내부 Job 이름은 유지
            .incrementer(new RunIdIncrementer())
            .listener(jobExecutionTimeListener)
            .start(createDailyBalanceStep())
            .build();
    }

    @Bean("memberDailyBalanceStep")  // Bean 이름 변경
    public Step createDailyBalanceStep() {
        return stepBuilderFactory.get("dailyBalanceStep")  // 내부 Step 이름은 유지
            .<List<Member>, List<DailyBalance>>chunk(1)
            .reader(memberReader)
            .processor(dailyBalanceProcessor)
            .writer(dailyBalanceWriter)
            .build();
    }
}