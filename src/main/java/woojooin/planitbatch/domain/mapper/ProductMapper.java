package woojooin.planitbatch.domain.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import woojooin.planitbatch.domain.vo.Product;

@Mapper
public interface ProductMapper {
	Product findById(long id);

	@MapKey("productId")
	Map<String, Product> getProductsByIds(@Param("productIds") List<String> productIds);
}
