package woojooin.planitbatch.domain.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DepositCalculationVO {

    private Long calculationId;         // 계산 결과 고유 ID
    private Long accountId;             // 계좌 ID (account 테이블 참조)
    
    // 계산 결과
    private BigDecimal interestAmount;     // 총 이자금액 (세전)
    private BigDecimal taxAmount;          // 이자소득세 (15.4%)
    private BigDecimal netInterestAmount;  // 세후 실수령액
    
    // 시간 정보
    private LocalDateTime createdAt;       // 계산 실행일시
    
    /**
     * 계산 결과를 설정하는 편의 메서드
     */
    public void calculateAmounts(BigDecimal accountBalance, BigDecimal earningsRate) {
        if (accountBalance != null && earningsRate != null) {
            // 이자 계산: 잔액 × 이자율 ÷ 100
            this.interestAmount = accountBalance
                .multiply(earningsRate)
                .divide(new BigDecimal("100"), 2, BigDecimal.ROUND_HALF_UP);
            
            // 세금 계산: 이자금액 × 15.4%
            this.taxAmount = this.interestAmount
                .multiply(new BigDecimal("0.154"))
                .setScale(2, BigDecimal.ROUND_HALF_UP);
            
            // 실수령액 계산: 이자금액 - 세금금액
            this.netInterestAmount = this.interestAmount.subtract(this.taxAmount);
        }
    }
    
    /**
     * DepositVO로부터 계산 결과를 생성하는 정적 팩토리 메서드
     */
    public static DepositCalculationVO fromDepositVO(DepositVO depositVO) {
        DepositCalculationVO calculation = new DepositCalculationVO();
        calculation.setAccountId(depositVO.getAccountId());
        calculation.calculateAmounts(depositVO.getAccountBalance(), depositVO.getEarningsRate());
        calculation.setCreatedAt(LocalDateTime.now());
        return calculation;
    }
}