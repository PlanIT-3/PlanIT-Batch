package woojooin.planitbatch.domain.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import woojooin.planitbatch.domain.vo.Account;

@Mapper
public interface AccountMapper {
    List<Account> getAccountsByAccountIds(@Param("accountIds") List<Long> accountIds);
}