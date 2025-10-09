package woojooin.planitbatch.batch.reader;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class GoalAchieveReader {

	private static final int CHUNK_SIZE = 500;
	private Long lastGoalId = null;
	private boolean hasMoreData = true;

}
