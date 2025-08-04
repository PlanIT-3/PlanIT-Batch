package woojooin.planitbatch.processor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import woojooin.planitbatch.domain.vo.DepositAccount;
import woojooin.planitbatch.domain.vo.DepositTaxResult;

/**
 * 예적금 세금 계산 Processor
 * 이자 수익을 계산하고 15.4% 세금을 적용
 */
@Component
public class DepositTaxProcessor implements ItemProcessor<DepositAccount, DepositTaxResult> {

    @Override
    public DepositTaxResult process(DepositAccount account) throws Exception {
        
        // 이자 수익 계산 (일별 계산 후 연간 환산)
        BigDecimal interestIncome = calculateInterestIncome(account);
        
        // 세금 계산 결과 생성 (연간 기준)
        DepositTaxResult result = new DepositTaxResult(account, interestIncome, "YEARLY");
        
        // 계산 기간 설정
        LocalDate now = LocalDate.now();
        result.setPeriodStartDate(account.getStartDate());
        result.setPeriodEndDate(now);
        
        return result;
    }
    
    /**
     * 이자 수익 계산
     * - 예금: 단리 계산
     * - 적금: 복리 계산 (월 납입 가정)
     */
    private BigDecimal calculateInterestIncome(DepositAccount account) {
        
        LocalDate startDate = account.getStartDate();
        LocalDate currentDate = LocalDate.now();
        BigDecimal principal = account.getPrincipal();
        BigDecimal interestRate = account.getInterestRate().divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP); // % → 소수
        
        if ("DEPOSIT".equals(account.getAccountType())) {
            // 예금: 단리 계산
            return calculateSimpleInterest(principal, interestRate, startDate, currentDate);
        } else {
            // 적금: 복리 계산 (월 납입)
            return calculateCompoundInterest(principal, interestRate, startDate, currentDate);
        }
    }
    
    /**
     * 단리 계산 (예금)
     * 이자 = 원금 × 연이율 × (경과일수 / 365)
     */
    private BigDecimal calculateSimpleInterest(BigDecimal principal, BigDecimal rate, 
                                             LocalDate startDate, LocalDate endDate) {
        
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        BigDecimal daysInYear = new BigDecimal("365");
        
        return principal
                .multiply(rate)
                .multiply(new BigDecimal(daysBetween))
                .divide(daysInYear, 2, RoundingMode.HALF_UP);
    }
    
    /**
     * 복리 계산 (적금)
     * 월 납입액 기준으로 각 월별 이자 계산 후 합산
     */
    private BigDecimal calculateCompoundInterest(BigDecimal monthlyAmount, BigDecimal rate, 
                                               LocalDate startDate, LocalDate endDate) {
        
        long monthsBetween = ChronoUnit.MONTHS.between(startDate, endDate);
        BigDecimal monthlyRate = rate.divide(new BigDecimal("12"), 10, RoundingMode.HALF_UP);
        BigDecimal totalInterest = BigDecimal.ZERO;
        
        // 각 월별 납입액에 대한 이자 계산
        for (int i = 0; i < monthsBetween; i++) {
            BigDecimal monthsRemaining = new BigDecimal(monthsBetween - i);
            BigDecimal interest = monthlyAmount
                    .multiply(monthlyRate)
                    .multiply(monthsRemaining);
            totalInterest = totalInterest.add(interest);
        }
        
        return totalInterest.setScale(2, RoundingMode.HALF_UP);
    }
}