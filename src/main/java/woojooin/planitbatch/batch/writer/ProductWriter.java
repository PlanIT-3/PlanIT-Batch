package woojooin.planitbatch.batch.writer;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import woojooin.planitbatch.domain.product.repository.ProductRepository;
import woojooin.planitbatch.domain.product.vo.Product;

@Component
@RequiredArgsConstructor
public class ProductWriter implements ItemWriter<Product> {

	private final ProductRepository productRepository;

	@Override
	public void write(List<? extends Product> items) throws Exception {
		for (Product product : items) {
			productRepository.update(product);
		}
	}
}
