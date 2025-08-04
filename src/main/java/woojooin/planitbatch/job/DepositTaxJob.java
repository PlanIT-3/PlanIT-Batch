package woojooin.planitbatch.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import woojooin.planitbatch.domain.vo.DepositAccount;
import woojooin.planitbatch.domain.vo.DepositTaxResult;

/**
 * 예적금 세금 계산 배치 Job 설정
 */
@Configuration
public class DepositTaxJob {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    /**
     * 예적금 세금 계산 Job
     */
    @Bean("depositTaxCalculationJob")
    public Job depositTaxCalculationJob() {
        return jobBuilderFactory.get("depositTaxCalculationJob")
                .start(depositTaxCalculationStep())
                .build();
    }

    /**
     * 예적금 세금 계산 Step
     * - Chunk 기반 처리 (100개씩)
     * - Reader: 예적금 계좌 조회
     * - Processor: 세금 계산
     * - Writer: 결과 저장
     */
    @Bean
    public Step depositTaxCalculationStep() {
        return stepBuilderFactory.get("depositTaxCalculationStep")
                .<DepositAccount, DepositTaxResult>chunk(100)
                .reader(depositAccountReader())
                .processor(depositTaxProcessor())
                .writer(depositTaxWriter())
                .build();
    }

    /**
     * 예적금 계좌 Reader
     */
    @Bean
    public ItemReader<DepositAccount> depositAccountReader() {
        // Reader 구현체는 별도 파일에서 정의
        return new woojooin.planitbatch.reader.DepositAccountReader();
    }

    /**
     * 예적금 세금 계산 Processor
     */
    @Bean
    public ItemProcessor<DepositAccount, DepositTaxResult> depositTaxProcessor() {
        // Processor 구현체는 별도 파일에서 정의
        return new woojooin.planitbatch.processor.DepositTaxProcessor();
    }

    /**
     * 예적금 세금 계산 결과 Writer
     */
    @Bean
    public ItemWriter<DepositTaxResult> depositTaxWriter() {
        // Writer 구현체는 별도 파일에서 정의
        return new woojooin.planitbatch.writer.DepositTaxWriter();
    }
}