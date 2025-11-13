package woojooin.planitbatch.domain.mapper;

import org.apache.ibatis.annotations.Param;
import woojooin.planitbatch.domain.vo.DailyReturn;

import java.time.LocalDate;
import java.util.List;

public interface DailyReturnMapper {
    List<DailyReturn> findAllForToday(@Param("date")LocalDate date);

    List<DailyReturn> findpast(@Param("startDate") LocalDate startDate,
                               @Param("endDate") LocalDate endDate);


    void insert(DailyReturn dailyReturn);
}
