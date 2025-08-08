package woojooin.planitbatch.domain.vo;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class MemberProduct {

    private Long memberProductId;

    private Long memberId;

    // ==== 상품 정보 ====
    // 상품유형코드
    private String itemName;                    // 상품/종목명
    private String itemCode;                    // 상품/종목코드

    // ==== 계좌 정보 ====
    private String accountNumber;               // 계좌번호
    private String accountExtends;              // 계좌번호 확장
    private String balanceType;                 // 잔고유형
    private String accountCurrency;             // 통화코드

    private BigDecimal avgPresentAmount;        // 평균매입가
    private BigDecimal presentAmount;           // 현재가
    private BigDecimal valuationPl;             // 평가손익
    private BigDecimal valuationAmount;         // 평가금액
    private BigDecimal purchaseAmount;          // 매입금액
    private BigDecimal earningsRate;            // 수익률 (%)
    private BigDecimal depositReceived;         // 예수금

    private BigDecimal quantity;                // 보유수량
    private BigDecimal settleQuantity;          // 정산수량

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
