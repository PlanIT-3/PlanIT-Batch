package woojooin.planitbatch.domain.memberProduct.vo;

import java.math.BigDecimal;
import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberProduct {
	private Long memberProductId;         // 사용자 상품 ID
	private Long memberId;                // 회원 ID
	private String productId;             // 상품 ID
	private String productTypeCode;       // 상품유형코드
	private String accountExtends;        // 계좌번호 확장
	private String productType;           // 상품유형
	private BigDecimal avgPresentAmount;  // 평균매입가
	private BigDecimal presentAmount;     // 현재가
	private String balanceType;           // 잔고유형
	private String itemName;              // 상품/종목명
	private BigDecimal valuationPl;       // 평가손익
	private BigDecimal valuationAmount;   // 평가금액
	private BigDecimal quantity;          // 수량
	private BigDecimal purchaseAmount;    // 매입금액
	private BigDecimal earningsRate;      // 수익률(%)
	private String accountCurrency;       // 통화코드
	private BigDecimal settleQuantity;    // 정산수량
	private String itemCode;              // 상품/종목코드
	private BigDecimal depositReceived;   // 예수금
	private Boolean isIntegrated;         // codef 연동 여부
	private Timestamp createdAt;          // 생성일시
	private Timestamp updatedAt;          // 수정일시
}
