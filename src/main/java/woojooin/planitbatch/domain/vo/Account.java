package woojooin.planitbatch.domain.vo;

import java.math.BigDecimal;
import java.math.BigInteger;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    private Long accountId;
    private BigInteger memberId;
    private BigDecimal accountBalance;
}
