package woojooin.planitbatch.domain.vo;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ProductDto {

    /** 상품코드 */
    private String productIdentifier;

    /** 상품명 */
    private String productName;

    /** 기준일자 */
    private LocalDate baseDate;

    /** 종가 */
    private BigDecimal closingPrice;


}
