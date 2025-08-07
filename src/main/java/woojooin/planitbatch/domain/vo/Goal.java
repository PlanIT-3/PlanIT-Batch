package woojooin.planitbatch.domain.vo;

import java.math.BigInteger;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Goal {
    private Long objectId;
    private Long memberId;
    private BigInteger targetAmount;
    private Integer depositRate;
    private Integer isaRate;
    private Integer goalRate;
}