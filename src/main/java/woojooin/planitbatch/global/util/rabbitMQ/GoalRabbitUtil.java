package woojooin.planitbatch.global.util.rabbitMQ;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import woojooin.planitbatch.global.util.rabbitMQ.util.GoalMsg;

@Component
@RequiredArgsConstructor
public class GoalRabbitUtil {

	private final RabbitTemplate rabbitTemplate;

	@Value("${rabbit-mq.queue.goal}")
	private String goalQueue;

	/**
	 * 특정 목표를 달성한 사용자 notification 이벤트 produce
	 * @param goalMsg
	 */
	public void sendGoalMessage(GoalMsg goalMsg) {

		rabbitTemplate.convertAndSend(goalQueue, goalMsg);
	}
}
