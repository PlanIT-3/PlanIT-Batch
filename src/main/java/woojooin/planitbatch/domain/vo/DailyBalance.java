package woojooin.planitbatch.domain.vo;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DailyBalance {
	private Long memberId;
	private Long amount;
	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;
}
