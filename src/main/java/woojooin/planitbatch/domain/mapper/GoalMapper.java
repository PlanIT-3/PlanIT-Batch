package woojooin.planitbatch.domain.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import woojooin.planitbatch.domain.vo.Goal;

@Mapper
public interface GoalMapper {
    List<Goal> getGoalsPaginated(Map<String, Object> params);
}