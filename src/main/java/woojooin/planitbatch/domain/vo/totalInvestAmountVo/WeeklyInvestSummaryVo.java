package woojooin.planitbatch.domain.vo.totalInvestAmountVo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyInvestSummaryVo {
    private Long memberId;
    private BigDecimal weeklyTotalAmount;
    private Integer weeklyTotalCount;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Boolean isDeleted;
}