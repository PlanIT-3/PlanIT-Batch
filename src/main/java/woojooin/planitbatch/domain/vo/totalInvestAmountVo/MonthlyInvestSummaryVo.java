package woojooin.planitbatch.domain.vo.totalInvestAmountVo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyInvestSummaryVo {
    private Long memberId;
    private BigDecimal monthlyTotalAmount;
    private int monthlyTotalCount;
    private Timestamp updatedAt;
    private Timestamp createdAt;
    private Boolean isDeleted;
}
