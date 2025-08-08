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

import woojooin.planitbatch.batch.listener.JobExecutionTimeListener;
import woojooin.planitbatch.batch.processor.CalculateInvestmentRatioProcessor;
import woojooin.planitbatch.batch.reader.MemberReader;
import woojooin.planitbatch.batch.writer.InvestmentRatioWriter;
import woojooin.planitbatch.domain.vo.InvestmentRatio;
import woojooin.planitbatch.domain.vo.Member;

@Configuration
public class CalculateInvestmentRatioJob {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private MemberReader memberReader;

    @Autowired
    private CalculateInvestmentRatioProcessor calculateInvestmentRatioProcessor;

    @Autowired
    private InvestmentRatioWriter investmentRatioWriter;

    @Autowired
    private JobExecutionTimeListener jobExecutionTimeListener;

    @Bean("memberInvestmentRatioJob")  // Bean 이름 변경
    public Job createInvestmentRatioJob() {
        return jobBuilderFactory.get("investmentRatioJob")  // 내부 Job 이름은 유지
            .listener(jobExecutionTimeListener)
            .start(createInvestmentRatioStep())
            .incrementer(new RunIdIncrementer())
            .build();
    }

    @Bean("memberInvestmentRatioStep")  // Bean 이름 변경
    public Step createInvestmentRatioStep() {
        return stepBuilderFactory.get("investmentRatioStep")  // 내부 Step 이름은 유지
            .<List<Member>, List<InvestmentRatio>>chunk(1)
            .reader(memberReader)
            .processor(calculateInvestmentRatioProcessor)
            .writer(investmentRatioWriter)
            .build();
    }
}