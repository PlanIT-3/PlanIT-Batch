package woojooin.planitbatch.batch.job.totalInvestAmount;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import woojooin.planitbatch.batch.processor.totalInvestProcessor.WeeklyInvestAmountProcessor;
import woojooin.planitbatch.batch.reader.totalInvestAmountReader.WeeklyInvestAmountReader;
import woojooin.planitbatch.batch.writer.totalInvestAmountWriter.WeeklyInvestAmountWriter;
import woojooin.planitbatch.domain.vo.DailyInvestSummaryVo;
import woojooin.planitbatch.domain.vo.totalInvestAmountVo.WeeklyInvestSummaryVo;

import java.util.List;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class WeeklyInvestAmountJob {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final WeeklyInvestAmountReader weeklyInvestAmountReader;
    private final WeeklyInvestAmountProcessor weeklyInvestAmountProcessor;
    private final WeeklyInvestAmountWriter weeklyInvestAmountWriter;

    @Bean
    public Job weeklyInvestJob() {
        return jobBuilderFactory.get("weeklyInvestAmountJob")
                .start(weeklyInvestAmountStep())
                .build();
    }

    @Bean
    public Step weeklyInvestAmountStep() {
        return stepBuilderFactory.get("weeklyInvestAmountStep")
                .<List<DailyInvestSummaryVo>, WeeklyInvestSummaryVo>chunk(1000)
                .reader(weeklyInvestAmountReader)
                .processor(weeklyInvestAmountProcessor)
                .writer(weeklyInvestAmountWriter)
                .build();
    }
}