package woojooin.planitbatch.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;

@Configuration
public class OpenAiConfig {

	@Value("${openai.api-key}")
	private String apiKey;

	@Value("${openai.base-url}")
	private String baseUrl;

	@Value("${openai.project-id}")
	private String projectId;

	@Bean
	public OpenAIClient openAIClient() {
		OpenAIClient client = OpenAIOkHttpClient.builder()
			.baseUrl(baseUrl)
			.apiKey(apiKey)
			.project(projectId)
			.build();

		return client;
	}

}
