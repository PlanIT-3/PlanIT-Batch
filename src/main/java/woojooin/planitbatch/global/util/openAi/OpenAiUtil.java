package woojooin.planitbatch.global.util.openAi;

import org.springframework.stereotype.Component;

import com.openai.client.OpenAIClient;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OpenAiUtil {

	private final OpenAIClient openAIClient;

	public String abstractKeyword(String productInfo) {
		ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
			.model(ChatModel.GPT_3_5_TURBO)
			.addSystemMessage("| 성향 유형 | 설명 |\n"
				+ "| --- | --- |\n"
				+ "|  안전형 | 원금 보존 최우선, 투자 경험 거의 없음 |\n"
				+ "|  안정추구형 | 예·적금 위주이되 소폭의 수익 추구 |\n"
				+ "|  위험중립형 | 손익 균형형, 일정 수준의 리스크 허용 |\n"
				+ "|  적극투자형 | 수익 기대치 높고, 리스크 수용 가능 |\n"
				+ "|  공격투자형 | 고수익 추구, 높은 리스크 감내 가능 |"
				+ "너는 투자 상품 성향 분류기야. 투자 상품을 어떤 투자 성향과 가장 잘 맞는지 분류해줘.")
			.addUserMessage("투자 상품 정보: " + productInfo)
			.build();

		return "response";
	}

	/**
	 * o3-mini 모델 기반 gpt user, assistant 메세지 통신 메서드
	 * @param userMessage
	 * @param assistantMessage
	 * @return
	 */
	public String basicChat(String userMessage, String assistantMessage) {
		ChatCompletionCreateParams params = ChatCompletionCreateParams.builder()
			.addAssistantMessage(assistantMessage)
			.addUserMessage(userMessage)
			.model(ChatModel.O3_MINI)
			.build();

		ChatCompletion chatCompletion = openAIClient.chat().completions().create(params);

		if (chatCompletion.choices().isEmpty()) {
			throw new RuntimeException("empty response");
		}

		String message = chatCompletion.choices().get(0).message().content()
			.orElseThrow(() -> new RuntimeException("no message"));

		return message;
	}

}
