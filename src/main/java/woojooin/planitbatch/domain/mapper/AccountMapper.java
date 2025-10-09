package woojooin.planitbatch.domain.mapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import woojooin.planitbatch.domain.vo.Account;
import woojooin.planitbatch.domain.vo.MemberBalance;

@Mapper
public interface AccountMapper {
    List<Account> getAccountsByAccountIds(@Param("accountIds") List<Long> accountIds);
    
    List<MemberBalance> getTotalBalanceByMemberIds(@Param("memberIds") List<Long> memberIds);
}