package woojooin.planitbatch.domain.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import woojooin.planitbatch.domain.vo.Action;

@Mapper
public interface ActionMapper {
    List<Action> getActionsByGoalIds(@Param("goalIds") List<Long> goalIds);
}