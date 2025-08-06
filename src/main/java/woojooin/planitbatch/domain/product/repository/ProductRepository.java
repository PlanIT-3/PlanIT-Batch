package woojooin.planitbatch.domain.product.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import woojooin.planitbatch.domain.product.mapper.ProductMapper;
import woojooin.planitbatch.domain.product.vo.Product;

@Repository
public class ProductRepository {

	@Autowired
	private ProductMapper productMapper;

	public Optional<Product> findById(int id) {
		return Optional.ofNullable(productMapper.findById(id));
	}

	public int save(Product product) {
		return productMapper.save(product);
	}

	public int saveAll(List<Product> products) {
		return productMapper.saveAll(products);
	}

}
