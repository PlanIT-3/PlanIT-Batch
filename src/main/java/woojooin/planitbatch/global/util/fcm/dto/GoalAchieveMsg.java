package woojooin.planitbatch.global.util.fcm.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoalAchieveMsg {

	private String body;
	private String title;

	public static String bodyMessage(String goalName, Integer achieveRate) {
		return String.format("%s 목표를 %d%% 달성했어요!", goalName, achieveRate);
	}

}
