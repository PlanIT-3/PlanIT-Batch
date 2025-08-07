package woojooin.planitbatch.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DailyInvestSummaryVo {
    private Long memberId;
    private BigDecimal dailyTotal;
    private int dailyCount;
    private Date updatedAt;
    private Date createdAt;
    private int isDeleted;
}
