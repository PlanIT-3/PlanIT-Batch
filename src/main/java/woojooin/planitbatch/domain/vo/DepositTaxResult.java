package woojooin.planitbatch.domain.vo;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 예적금 세금 계산 결과 VO
 */
public class DepositTaxResult {
    private Long resultId;
    private Long accountId;
    private Long userId;
    private String accountNumber;
    private String bankName;
    private String productName;
    private BigDecimal principal;           // 원금
    private BigDecimal interestRate;        // 연 이자율
    private BigDecimal interestIncome;      // 이자 수익
    private BigDecimal taxAmount;           // 세금 (15.4%)
    private BigDecimal netIncome;           // 세후 수익
    private LocalDate calculationDate;      // 계산일
    private String period;                  // MONTHLY / YEARLY
    private LocalDate periodStartDate;      // 기간 시작일
    private LocalDate periodEndDate;        // 기간 종료일
    
    // 세금 계산 상수
    public static final BigDecimal TAX_RATE = new BigDecimal("0.154"); // 15.4%
    
    // 기본 생성자
    public DepositTaxResult() {}
    
    // 계산용 생성자
    public DepositTaxResult(DepositAccount account, BigDecimal interestIncome, String period) {
        this.accountId = account.getAccountId();
        this.userId = account.getUserId();
        this.accountNumber = account.getAccountNumber();
        this.bankName = account.getBankName();
        this.productName = account.getProductName();
        this.principal = account.getPrincipal();
        this.interestRate = account.getInterestRate();
        this.interestIncome = interestIncome;
        this.period = period;
        this.calculationDate = LocalDate.now();
        
        // 세금 계산: 이자수익 × 15.4%
        this.taxAmount = interestIncome.multiply(TAX_RATE);
        this.netIncome = interestIncome.subtract(this.taxAmount);
    }
    
    // Getter & Setter
    public Long getResultId() { return resultId; }
    public void setResultId(Long resultId) { this.resultId = resultId; }
    
    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    
    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }
    
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    
    public BigDecimal getPrincipal() { return principal; }
    public void setPrincipal(BigDecimal principal) { this.principal = principal; }
    
    public BigDecimal getInterestRate() { return interestRate; }
    public void setInterestRate(BigDecimal interestRate) { this.interestRate = interestRate; }
    
    public BigDecimal getInterestIncome() { return interestIncome; }
    public void setInterestIncome(BigDecimal interestIncome) { this.interestIncome = interestIncome; }
    
    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }
    
    public BigDecimal getNetIncome() { return netIncome; }
    public void setNetIncome(BigDecimal netIncome) { this.netIncome = netIncome; }
    
    public LocalDate getCalculationDate() { return calculationDate; }
    public void setCalculationDate(LocalDate calculationDate) { this.calculationDate = calculationDate; }
    
    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }
    
    public LocalDate getPeriodStartDate() { return periodStartDate; }
    public void setPeriodStartDate(LocalDate periodStartDate) { this.periodStartDate = periodStartDate; }
    
    public LocalDate getPeriodEndDate() { return periodEndDate; }
    public void setPeriodEndDate(LocalDate periodEndDate) { this.periodEndDate = periodEndDate; }
}