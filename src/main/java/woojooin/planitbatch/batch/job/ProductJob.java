package woojooin.planitbatch.batch.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import woojooin.planitbatch.batch.reader.ProductReader;
import woojooin.planitbatch.domain.product.vo.Product;

@Configuration
@RequiredArgsConstructor
public class ProductJob {
	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	private final ProductReader productReader;
	private final ItemProcessor<Product, Product> productProcessor;
	private final ItemWriter<Product> productWriter;

	@Bean("productExpectedReturnRateJob")
	public Job productJob() {
		return jobBuilderFactory.get("productExpectedReturnRateJob")
			.start(productStep())
			.build();
	}

	@Bean("productExpectedReturnRateStep")
	public Step productStep() {
		return stepBuilderFactory.get("productExpectedReturnRateStep")
			.<Product, Product>chunk(productReader.CHUNK_SIZE)
			.reader(productReader)
			.processor(productProcessor)
			.writer(productWriter)
			.build();
	}
}
