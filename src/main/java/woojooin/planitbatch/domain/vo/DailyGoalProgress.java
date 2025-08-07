package woojooin.planitbatch.domain.vo;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DailyGoalProgress {
	private Long objectId;
	private Double isaProgress;
	private Double depositProgress;
	private LocalDate progressDate;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
