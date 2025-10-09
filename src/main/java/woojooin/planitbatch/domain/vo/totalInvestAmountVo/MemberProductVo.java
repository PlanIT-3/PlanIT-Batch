package woojooin.planitbatch.domain.vo.totalInvestAmountVo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberProductVo {
    private Long memberId;
    private String productId;
    private BigDecimal valuationAmount;
    private BigDecimal quantity;
    private BigDecimal purchaseAmount;
    private Timestamp createdAt;
    private Timestamp updatedAt;
}
