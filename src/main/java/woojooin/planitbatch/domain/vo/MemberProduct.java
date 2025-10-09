package woojooin.planitbatch.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberProduct {
    private Long memberProductId;
    private Long memberId;
    private String productId;
    private String shortenCode;
    private Double presentAmount;
    private Long quantity;
}