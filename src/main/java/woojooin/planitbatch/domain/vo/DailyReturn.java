package woojooin.planitbatch.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyReturn {
    private Long memberId;
    private String itemCode;
    private LocalDate returnDate;
    private BigDecimal amount;
}
