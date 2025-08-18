package woojooin.planitbatch.domain.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import woojooin.planitbatch.domain.vo.DailyGoalProgress;

@Mapper
public interface DailyGoalProgressMapper {
    void insertDailyGoalProgressBatch(List<DailyGoalProgress> dailyGoalProgressList);
}