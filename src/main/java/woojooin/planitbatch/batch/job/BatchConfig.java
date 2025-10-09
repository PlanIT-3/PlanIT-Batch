package woojooin.planitbatch.batch.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import woojooin.planitbatch.batch.processor.DepositProcessor;
import woojooin.planitbatch.batch.processor.TaxCalculationProcessor;
import woojooin.planitbatch.batch.reader.DepositReader;
import woojooin.planitbatch.batch.reader.TaxCalculationReader;
import woojooin.planitbatch.batch.writer.DepositWriter;
import woojooin.planitbatch.batch.writer.TaxCalculationWriter;
import woojooin.planitbatch.domain.vo.DepositVO;
import woojooin.planitbatch.domain.vo.DepositTaxSavingVO;

@Configuration
public class BatchConfig {
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	@Autowired
	private DepositReader depositReader;
	@Autowired
	private DepositProcessor depositProcessor;
	@Autowired
	private DepositWriter depositWriter;

	@Autowired
	private StepBuilderFactory steps;

	private TaxCalculationReader taxCalculationReader;
	@Autowired
	private TaxCalculationProcessor taxCalculationProcessor;
	@Autowired
	private TaxCalculationWriter taxCalculationWriter;

	@Autowired
	@Qualifier("batchTransactionManager")  // 명시적 트랜잭션 매니저 지정
	private PlatformTransactionManager transactionManager;

	// 예적금 계좌 정보 수집 Job (Chunk 방식) - 청크 1000개 처리
	@Bean
	public Step depositCollectionStep() {
		return stepBuilderFactory.get("depositCollectionStep")
			.<DepositVO, DepositVO>chunk(1000)       // 청크 크기 1000개로 변경 (대용량 처리)
			.reader(depositReader)
			.processor(depositProcessor)
			.writer(depositWriter)
			.transactionManager(transactionManager)  // 명시적 트랜잭션 매니저 설정
			.build();
	}

	@Bean
	public Job depositCollectionJob() {
		return jobBuilderFactory.get("depositCollectionJob")
			.start(depositCollectionStep())
			.build();
	}

	// 세금 계산 Job (Chunk 방식) - 청크 1000개 처리
	@Bean
	public Step taxCalculationStep() {
		return stepBuilderFactory.get("taxCalculationStep")
			.<DepositVO, DepositTaxSavingVO>chunk(1000)  // 청크 크기 1000개 (대용량 처리)
			.reader(taxCalculationReader)
			.processor(taxCalculationProcessor)
			.writer(taxCalculationWriter)
			.transactionManager(transactionManager)      // 명시적 트랜잭션 매니저 설정
			.build();
	}

	@Bean
	public Job taxCalculationJob() {
		return jobBuilderFactory.get("taxCalculationJob")
			.start(taxCalculationStep())
			.build();
	}

}
