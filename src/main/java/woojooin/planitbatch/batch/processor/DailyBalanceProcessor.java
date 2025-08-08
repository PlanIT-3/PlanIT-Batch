package woojooin.planitbatch.batch.processor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import woojooin.planitbatch.domain.mapper.AccountMapper;
import woojooin.planitbatch.domain.vo.DailyBalance;
import woojooin.planitbatch.domain.vo.Member;
import woojooin.planitbatch.domain.vo.MemberBalance;

@Slf4j
@Component
@RequiredArgsConstructor
public class DailyBalanceProcessor implements ItemProcessor<List<Member>, List<DailyBalance>> {

    private final AccountMapper accountMapper;

    @Override
    public List<DailyBalance> process(List<Member> members) throws Exception {
        log.debug("Processing {} members for daily balance calculation", members.size());
        
        List<DailyBalance> dailyBalances = new ArrayList<>();
        
        List<Long> memberIds = members.stream()
                .map(Member::getMemberId)
                .collect(Collectors.toList());

        try {
            List<MemberBalance> balances = accountMapper.getTotalBalanceByMemberIds(memberIds);
            Map<Long, Long> balanceMap = balances.stream()
                .collect(Collectors.toMap(
                    MemberBalance::getMemberId,
                    MemberBalance::getValue
                ));
            
            for (Member member : members) {
                Long balanceValue = balanceMap.getOrDefault(member.getMemberId(), 0L);
                
                DailyBalance dailyBalance = new DailyBalance(
                    member.getMemberId(),
                    balanceValue,
                    LocalDateTime.now(),
                    LocalDateTime.now()
                );
                dailyBalances.add(dailyBalance);
            }
            
            log.debug("Created {} daily balance records", dailyBalances.size());
            
        } catch (Exception e) {
            log.error("Error processing daily balance for members: {}", memberIds, e);
            throw e;
        }
        
        return dailyBalances;
    }
}