package woojooin.planitbatch.batch.job;

import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import woojooin.planitbatch.batch.listener.JobExecutionTimeListener;
import woojooin.planitbatch.batch.processor.CalculateInvestmentRatioProcessor;
import woojooin.planitbatch.batch.reader.MemberReader;
import woojooin.planitbatch.batch.writer.InvestmentRatioWriter;
import woojooin.planitbatch.domain.vo.InvestmentRatio;
import woojooin.planitbatch.domain.vo.Member;

@Configuration
@RequiredArgsConstructor
public class CalculateInvestmentRatioJob {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    private final MemberReader memberReader;
    private final CalculateInvestmentRatioProcessor calculateInvestmentRatioProcessor;
    private final InvestmentRatioWriter investmentRatioWriter;
    private final JobExecutionTimeListener jobExecutionTimeListener;


    @Bean
    public Job investmentRatioJob() {
        return jobBuilderFactory.get("investmentRatioJob")
            .listener(jobExecutionTimeListener)
            .start(investmentRatioStep())
            .build();
    }

    @Bean
    public Step investmentRatioStep() {
        return stepBuilderFactory.get("investmentRatioStep")
            .<List<Member>, List<InvestmentRatio>>chunk(1)
            .reader(memberReader)
            .processor(calculateInvestmentRatioProcessor)
            .writer(investmentRatioWriter)
            .build();
    }
}
