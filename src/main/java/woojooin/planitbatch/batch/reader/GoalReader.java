package woojooin.planitbatch.batch.reader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import woojooin.planitbatch.domain.mapper.GoalMapper;
import woojooin.planitbatch.domain.vo.Goal;

@Slf4j
@Component
public class GoalReader implements ItemReader<List<Goal>> {

    private final GoalMapper goalMapper;
    private static final int CHUNK_SIZE = 500;
    private Long lastObjectId = null;
    private boolean hasMoreData = true;

    public GoalReader(GoalMapper goalMapper) {
        this.goalMapper = goalMapper;
    }

    @Override
    public List<Goal> read() throws Exception {
        if (!hasMoreData) {
            return null;
        }

        try {
            Map<String, Object> params = new HashMap<>();
            params.put("limit", CHUNK_SIZE);
            if (lastObjectId != null) {
                params.put("lastObjectId", lastObjectId);
            }

            List<Goal> goals = goalMapper.getGoalsPaginated(params);
            
            log.info("GoalReader - 조회된 Goal 데이터 수: {}", goals.size());

            if (goals.isEmpty() || goals.size() < CHUNK_SIZE) {
                hasMoreData = false;
                log.info("GoalReader - 모든 데이터 조회 완료. hasMoreData: false");
            }

            if (!goals.isEmpty()) {
                lastObjectId = goals.get(goals.size() - 1).getObjectId().longValue();
                log.info("GoalReader - 마지막 조회된 objectId: {}", lastObjectId);
            } else {
                log.warn("GoalReader - 조회된 데이터가 없습니다.");
            }

            log.debug("조회된 Goal 수: {}, 마지막 objectId: {}", goals.size(), lastObjectId);

            return goals.isEmpty() ? null : goals;

        } catch (Exception e) {
            log.error("Goal 데이터 조회 실패: {}", e.getMessage());
            throw e;
        }
    }
}