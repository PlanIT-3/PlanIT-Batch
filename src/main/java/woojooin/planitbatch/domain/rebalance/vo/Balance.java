package woojooin.planitbatch.domain.rebalance.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import woojooin.planitbatch.domain.memberProduct.vo.MemberProduct;
import woojooin.planitbatch.domain.product.vo.Product;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Balance {
	private Long goalId;
	private Long actionId;

	private MemberProduct member_product;
	private Product product;
}
