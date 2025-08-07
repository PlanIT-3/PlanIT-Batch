package woojooin.planitbatch.domain.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepositTaxSavingVO {
    
    private Long id;                    // 기본키 (AUTO_INCREMENT)
    private Long memberId;              // 회원 ID
    private Long accountId;             // 계좌 ID
    private String quarter;             // 분기 (예: "2024Q4")
    
    // 수익 정보
    private BigDecimal interestIncome;  // 이자 수익
    
    // 세금 계산 결과
    private BigDecimal incomeTax;       // 소득세 (14%)
    private BigDecimal localIncomeTax;  // 지방소득세 (1.4%)
    private BigDecimal totalTax;        // 총 세금 (15.4%)
    private BigDecimal netIncome;       // 세후 실수익
    
    // 메타 정보
    private LocalDateTime createdAt;    // 생성일시
    
    /**
     * 현재 연도와 분기를 기준으로 quarter 문자열 생성
     */
    public static String getCurrentQuarter() {
        LocalDateTime now = LocalDateTime.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        int quarter = (month - 1) / 3 + 1;
        return year + "Q" + quarter;
    }
    
    /**
     * 이자소득 기준으로 세금 계산
     * 소득세 14%, 지방소득세 1.4% (총 15.4%)
     */
    public void calculateTax() {
        if (this.interestIncome != null && this.interestIncome.compareTo(BigDecimal.ZERO) > 0) {
            // 소득세 14%
            this.incomeTax = this.interestIncome
                .multiply(new BigDecimal("0.14"))
                .setScale(4, BigDecimal.ROUND_HALF_UP);
            
            // 지방소득세 1.4%  
            this.localIncomeTax = this.interestIncome
                .multiply(new BigDecimal("0.014"))
                .setScale(4, BigDecimal.ROUND_HALF_UP);
            
            // 총 세금 15.4%
            this.totalTax = this.incomeTax.add(this.localIncomeTax);
            
            // 세후 실수익
            this.netIncome = this.interestIncome.subtract(this.totalTax);
        } else {
            this.incomeTax = BigDecimal.ZERO;
            this.localIncomeTax = BigDecimal.ZERO;
            this.totalTax = BigDecimal.ZERO;
            this.netIncome = BigDecimal.ZERO;
        }
    }
}