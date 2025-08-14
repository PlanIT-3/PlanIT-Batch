package woojooin.planitbatch.global.util.rabbitMQ.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoalMsg {

	private Long memberId;
	private Long goalId;
	private String goalName;
	private Integer achievementRate;
}
