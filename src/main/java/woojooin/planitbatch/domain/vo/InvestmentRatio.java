package woojooin.planitbatch.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestmentRatio {
    private Long memberId;
    private Double stable;      // 안정형
    private Double income;      // 안정추구형
    private Double liquid;      // 위험중립형
    private Double growth;      // 적극투자형
    private Double diversified; // 공격투자형
}