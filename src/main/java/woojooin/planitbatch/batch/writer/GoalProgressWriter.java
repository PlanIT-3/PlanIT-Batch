package woojooin.planitbatch.batch.writer;

import java.util.List;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import woojooin.planitbatch.domain.mapper.DailyGoalProgressMapper;
import woojooin.planitbatch.domain.vo.DailyGoalProgress;

@Slf4j
@StepScope
@Component
@RequiredArgsConstructor
public class GoalProgressWriter implements ItemWriter<List<DailyGoalProgress>> {

    private final DailyGoalProgressMapper dailyGoalProgressMapper;

    @Override
    public void write(List<? extends List<DailyGoalProgress>> items) throws Exception {
        for (List<DailyGoalProgress> progressList : items) {
            if (!progressList.isEmpty()) {
                dailyGoalProgressMapper.insertDailyGoalProgressBatch(progressList);
                log.info("목표 진행률 {} 건 저장 완료", progressList.size());
            }
        }
    }
}