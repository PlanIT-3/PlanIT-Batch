package woojooin.planitbatch.domain.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import woojooin.planitbatch.domain.vo.DailyBalance;

@Mapper
public interface DailyBalanceMapper {
    void insertDailyBalances(List<DailyBalance> dailyBalances);
}