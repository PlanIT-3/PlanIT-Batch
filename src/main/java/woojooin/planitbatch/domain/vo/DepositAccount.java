package woojooin.planitbatch.domain.vo;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 예적금 계좌 정보 VO
 */
public class DepositAccount {
    private Long accountId;
    private Long userId;
    private String accountNumber;
    private String bankName;
    private String productName;
    private BigDecimal principal;           // 원금
    private BigDecimal interestRate;        // 연 이자율 (%)
    private LocalDate startDate;            // 가입일
    private LocalDate maturityDate;         // 만기일
    private String accountType;             // DEPOSIT(예금) / SAVINGS(적금)
    private BigDecimal currentBalance;      // 현재 잔액
    private LocalDate lastCalculationDate;  // 마지막 계산일
    
    // 기본 생성자
    public DepositAccount() {}
    
    // Getter & Setter
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
    
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    
    public LocalDate getMaturityDate() { return maturityDate; }
    public void setMaturityDate(LocalDate maturityDate) { this.maturityDate = maturityDate; }
    
    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }
    
    public BigDecimal getCurrentBalance() { return currentBalance; }
    public void setCurrentBalance(BigDecimal currentBalance) { this.currentBalance = currentBalance; }
    
    public LocalDate getLastCalculationDate() { return lastCalculationDate; }
    public void setLastCalculationDate(LocalDate lastCalculationDate) { this.lastCalculationDate = lastCalculationDate; }
}