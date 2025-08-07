package woojooin.planitbatch.batch.job.totalInvestAmount;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import woojooin.planitbatch.batch.processor.totalInvestProcessor.DailyInvestAmountProcessor;
import woojooin.planitbatch.batch.reader.totalInvestAmountReader.DailyInvestAmountReader;
import woojooin.planitbatch.batch.writer.totalInvestAmountWriter.DailyInvestAmountWriter;
import woojooin.planitbatch.domain.vo.DailyInvestSummaryVo;
import woojooin.planitbatch.domain.vo.totalInvestAmountVo.MemberVo;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class DailyInvestAmountJob {
    @Autowired
    private  JobBuilderFactory jobBuilderFactory;
    @Autowired
    private  StepBuilderFactory stepBuilderFactory;
    @Autowired
    private DailyInvestAmountReader dailyInvestAmountReader;
    @Autowired
    private DailyInvestAmountProcessor dailyInvestAmountProcessor;

    private final DailyInvestAmountWriter dailyInvestAmountWriter;

    @Bean
    public Job dailyInvestJob() {
        return jobBuilderFactory.get("dailyInvestAmountJob")
                .start(dailyInvestAmountStep())
                .build();
    }

    @Bean
    public Step dailyInvestAmountStep() {
        return stepBuilderFactory.get("dailyInvestAmountStep")
                .<MemberVo, DailyInvestSummaryVo>chunk(1000)
                .reader(dailyInvestAmountReader)
                .processor(dailyInvestAmountProcessor)
                .writer(dailyInvestAmountWriter)
                .build();
    }
}
