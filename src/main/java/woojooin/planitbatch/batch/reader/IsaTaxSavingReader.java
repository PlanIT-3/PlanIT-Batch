package woojooin.planitbatch.batch.reader;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import woojooin.planitbatch.domain.dto.UserProductQuarterData;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class IsaTaxSavingReader {

	private final SqlSessionFactory sqlSessionFactory;

	@Bean(name = "isaTaxReader")
	@StepScope
	public MyBatisPagingItemReader<UserProductQuarterData> isaTaxReader() {

		MyBatisPagingItemReader<UserProductQuarterData> reader = new MyBatisPagingItemReader<>();
		reader.setSqlSessionFactory(sqlSessionFactory);
		reader.setQueryId("woojooin.planitbatch.domain.mapper.IsaTaxSavingMapper.selectIsaProductProfitByMember");
		reader.setPageSize(100);
		return reader;
	}
}