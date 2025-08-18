package woojooin.planitbatch.batch.job.totalInvestAmount;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import woojooin.planitbatch.batch.processor.totalInvestProcessor.MonthlyInvestAmountProcessor;
import woojooin.planitbatch.batch.reader.totalInvestAmountReader.MonthlyInvestAmountReader;
import woojooin.planitbatch.batch.writer.totalInvestAmountWriter.MonthlyInvestAmountWriter;
import woojooin.planitbatch.domain.vo.totalInvestAmountVo.MonthlyInvestSummaryVo;
import woojooin.planitbatch.domain.vo.totalInvestAmountVo.WeeklyInvestSummaryVo;

import java.util.List;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class MonthlyInvestAmountJob {
    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final MonthlyInvestAmountReader monthlyInvestAmountReader;
     private final MonthlyInvestAmountProcessor monthlyInvestAmountProcessor;
     private final MonthlyInvestAmountWriter monthlyInvestAmountWriter;

     @Bean
    public Job monthlyInvestJob() {
        return jobBuilderFactory.get("monthlyInvestAmountJob")
                .start(monthlyInvestAmountStep())
                .build();
    }

    @Bean
    public Step monthlyInvestAmountStep() {
        return stepBuilderFactory.get("monthlyInvestAmountStep")
                .<List<WeeklyInvestSummaryVo>, MonthlyInvestSummaryVo>chunk(1000)
                .reader(monthlyInvestAmountReader)
                .processor(monthlyInvestAmountProcessor)
                .writer(monthlyInvestAmountWriter)
                .build();
    }
}
