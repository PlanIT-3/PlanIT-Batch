package woojooin.planitbatch.domain.product.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import woojooin.planitbatch.domain.product.enums.InvestType;
import woojooin.planitbatch.domain.product.vo.Product;

@Mapper
public interface ProductMapper {
	Product findById(long id);

	int save(Product product);

	int saveAll(List<Product> products);

	int update(Product product);

	List<Product> findAll();

	@MapKey("shortenCode")
	Map<String, Product> getProductsByShortenCodes(@Param("productIds") List<String> productIds);

	Product getHighestDifferenceProductByInvestType(@Param("investType") InvestType investType);

	List<Product> findPage(@Param("offset") int offset,
		@Param("limit") int limit);

	int countAll();
	@MapKey("productId")
	Map<String, Product> getProductsByIds(@Param("productIds") List<String> productIds);
}
