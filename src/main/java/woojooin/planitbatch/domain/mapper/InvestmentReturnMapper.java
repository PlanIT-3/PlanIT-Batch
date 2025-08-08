package woojooin.planitbatch.domain.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.cglib.core.Local;
import woojooin.planitbatch.domain.vo.DailyReturn;
import woojooin.planitbatch.domain.vo.InvestmentReturn;

import java.time.LocalDate;
import java.util.List;

public interface InvestmentReturnMapper {
    List<InvestmentReturn> findDailyReturn(@Param("today") LocalDate today);
    List<InvestmentReturn> findWeeklyReturn(@Param("today") LocalDate today);
    List<InvestmentReturn>findMonthlyReturn(@Param("today") LocalDate today);
    void insert(InvestmentReturn returnResult);
    void upsertMonthlyReturn(InvestmentReturn returnResult);

}




