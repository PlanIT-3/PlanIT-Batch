package woojooin.planitbatch.domain.mapper;

import org.apache.ibatis.annotations.Param;
import woojooin.planitbatch.domain.vo.DailyReturn;

import java.time.LocalDate;
import java.util.List;

public interface DailyReturnMapper {
    List<DailyReturn> findAllForToday(@Param("date")LocalDate date);
    void insert(DailyReturn dailyReturn);
}
