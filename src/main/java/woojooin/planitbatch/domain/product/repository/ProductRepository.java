package woojooin.planitbatch.domain.product.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import woojooin.planitbatch.domain.product.enums.InvestType;
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

	public int update(Product product) {
		return productMapper.update(product);
	}

	public List<Product> findAll() {
		return productMapper.findAll();
	}

	public Optional<Product> getHighestDifferenceProductByInvestType(InvestType investType) {
		return Optional.ofNullable(productMapper.getHighestDifferenceProductByInvestType(investType));
	}

	public List<Product> findPage(int offset, int limit) {
		return productMapper.findPage(offset, limit);
	}

	public int countAll() {
		return productMapper.countAll();
	}
}
