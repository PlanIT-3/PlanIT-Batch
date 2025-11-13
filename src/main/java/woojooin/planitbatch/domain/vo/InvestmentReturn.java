package woojooin.planitbatch.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvestmentReturn {
    private Long memberId;
    private String returnType;
    private LocalDate returnDate;
    private BigDecimal amountNow;
    private BigDecimal amountPast;
    private BigDecimal returnRate;
}
