package woojooin.planitbatch.batch.processor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import woojooin.planitbatch.domain.mapper.AccountMapper;
import woojooin.planitbatch.domain.mapper.ActionMapper;
import woojooin.planitbatch.domain.mapper.MemberProductMapper;
import woojooin.planitbatch.domain.vo.Account;
import woojooin.planitbatch.domain.vo.Action;
import woojooin.planitbatch.domain.vo.DailyGoalProgress;
import woojooin.planitbatch.domain.vo.Goal;
import woojooin.planitbatch.domain.vo.MemberProduct;

@Slf4j
@StepScope
@Component
@RequiredArgsConstructor
public class GoalProgressProcessor implements ItemProcessor<List<Goal>, List<DailyGoalProgress>> {

    private final ActionMapper actionMapper;
    private final MemberProductMapper memberProductMapper;
    private final AccountMapper accountMapper;
    private LocalDate progressDate;

    @Override
    public List<DailyGoalProgress> process(List<Goal> goals) throws Exception {
        List<DailyGoalProgress> progressList = new ArrayList<>();
        
        log.info("GoalProgressProcessor - 처리할 Goal 데이터 수: {}", goals.size());

        List<Long> goalObjectIds = goals.stream()
            .map(Goal::getGoalId)
            .collect(Collectors.toList());
        
        log.info("GoalProgressProcessor - Goal ObjectId 목록: {}", goalObjectIds);

        List<Action> allActions = actionMapper.getActionsByGoalIds(goalObjectIds);
        log.info("GoalProgressProcessor - 조회된 Action 데이터 수: {}", allActions.size());

        List<Long> allMemberProductIds = allActions.stream()
            .filter(action -> action.getMemberProductId() != null)
            .map(Action::getMemberProductId)
            .distinct()
            .collect(Collectors.toList()); // 보유한 ISA 상품들 ID
        
        log.info("GoalProgressProcessor - 관련 MemberProduct ID 수: {}", allMemberProductIds.size());

        List<Long> allAccountIds = allActions.stream()
            .filter(action -> action.getAccountId() != null)
            .map(Action::getAccountId)
            .distinct()
            .collect(Collectors.toList()); // 보유한 예적금들 ID
        
        log.info("GoalProgressProcessor - 관련 Account ID 수: {}", allAccountIds.size());

        List<MemberProduct> allMemberProducts = allMemberProductIds.isEmpty() ? 
            new ArrayList<>() : memberProductMapper.getMemberProductsByIds(allMemberProductIds); // ISA
        log.info("GoalProgressProcessor - 조회된 MemberProduct 데이터 수: {}", allMemberProducts.size());

        List<Account> allAccounts = allAccountIds.isEmpty() ?
            new ArrayList<>() : accountMapper.getAccountsByAccountIds(allAccountIds); // 예적금
        log.info("GoalProgressProcessor - 조회된 Account 데이터 수: {}", allAccounts.size());

        for (Goal goal : goals) {
            try {
                DailyGoalProgress dailyProgress = calculateGoalProgress(goal, allActions, allMemberProducts, allAccounts);
                progressList.add(dailyProgress);
                
                log.debug("목표 ID: {}, ISA 진행률: {}, 예적금 진행률: {}", 
                    goal.getGoalId(), dailyProgress.getIsaProgress(), dailyProgress.getDepositProgress());
            } catch (Exception e) {
                log.error("목표 ID {}의 진행률 계산 실패: {}", goal.getGoalId(), e.getMessage());
            }
        }

        return progressList;
    }

    private DailyGoalProgress calculateGoalProgress(Goal goal, List<Action> allActions, 
                                                   List<MemberProduct> allMemberProducts, 
                                                   List<Account> allAccounts) {
        
        List<Action> goalActions = allActions.stream()
            .filter(action -> goal.getGoalId().equals(action.getGoalId()))
            .collect(Collectors.toList()); // 특정 목적에 할당한 Action 리스트들

        List<Long> memberProductIds = goalActions.stream()
            .filter(action -> action.getMemberProductId() != null)
            .map(Action::getMemberProductId)
            .collect(Collectors.toList()); // 해당 Action 리스트들 중 Member Product ISA 상품들
        
        List<Long> accountIds = goalActions.stream()
            .filter(action -> action.getActionId() != null)
            .map(Action::getAccountId)
            .collect(Collectors.toList()); // Action 리스트들 중 예적금 상품들

        // 목표 금액에서 ISA와 예적금 할당 금액 계산
        BigDecimal targetAmount = new BigDecimal(goal.getTargetAmount());
        BigDecimal isaTargetAmount = targetAmount.multiply(BigDecimal.valueOf(goal.getIsaRate())).divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);
        BigDecimal depositTargetAmount = targetAmount.multiply(BigDecimal.valueOf(goal.getDepositRate())).divide(BigDecimal.valueOf(100), RoundingMode.HALF_UP);

        double isaProgress = 0.0;
        double depositProgress = 0.0;

        // ISA 진행률 계산
        if (!memberProductIds.isEmpty() && isaTargetAmount.compareTo(BigDecimal.ZERO) > 0) {
            List<MemberProduct> goalMemberProducts = allMemberProducts.stream()
                .filter(mp -> memberProductIds.contains(mp.getMemberProductId()))
                .collect(Collectors.toList());

            BigDecimal currentIsaAmount = goalMemberProducts.stream()
                .map(mp -> BigDecimal.valueOf(mp.getPresentAmount()).multiply(BigDecimal.valueOf(mp.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            isaProgress = currentIsaAmount.divide(isaTargetAmount, 4, RoundingMode.HALF_UP).doubleValue();
        }

        // 예적금 진행률 계산
        if (!accountIds.isEmpty() && depositTargetAmount.compareTo(BigDecimal.ZERO) > 0) {
            List<Account> goalAccounts = allAccounts.stream()
                .filter(account -> accountIds.contains(account.getAccountId().longValue()))
                .collect(Collectors.toList());


            BigDecimal currentDepositAmount = goalAccounts.stream()
                .map(Account::getAccountBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            depositProgress = currentDepositAmount.divide(depositTargetAmount, 4, RoundingMode.HALF_UP).doubleValue();
        }
        return new DailyGoalProgress(goal.getGoalId(), isaProgress, depositProgress, progressDate, null, null);
    }
}