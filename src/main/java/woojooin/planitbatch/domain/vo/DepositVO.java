package woojooin.planitbatch.domain.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DepositVO {

	private Long accountId;           // 계좌 ID
	private Long memberId;            // 회원 ID

	private String accountName;       // 계좌명
	private String accountNumber;     // 계좌번호
	private String accountCurrency;   // 통화 (KRW)

	private BigDecimal accountBalance;      // 현재 잔액
	private BigDecimal accountDeposit;      // 예치금액
	private BigDecimal earningsRate;        // 이자율
	private BigDecimal accountInvestedCost; // 투자 원금

	private LocalDateTime lastTranDate;     // 마지막 거래일
	private LocalDateTime accountStartDate; // 계좌 시작일
	private LocalDateTime accountEndDate;   // 계좌 만기일

	private Boolean isDeleted;              // 삭제 여부
	private Boolean isIntegrated;           // CODEF 연동 여부

	private LocalDateTime createdAt;        // 생성일시
	private LocalDateTime updatedAt;        // 수정일시

	// CODEF API 응답용 추가 필드 (임시 저장용)
	private String bankCode;               // 은행 코드
	private String bankName;               // 은행명
	private String productName;            // 상품명
	private BigDecimal interestAmount;     // 이자금액
	private BigDecimal taxAmount;          // 세금금액
	
	// 배치 처리용 세금 절약 내역 (저장용)
	private DepositTaxSavingVO depositTaxSaving; // 세금 절약 내역
} 