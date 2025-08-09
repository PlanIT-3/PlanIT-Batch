package woojooin.planitbatch.batch.reader;

import java.util.Collections;
import java.util.List;

import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import woojooin.planitbatch.domain.product.repository.ProductRepository;
import woojooin.planitbatch.domain.product.vo.Product;

@Component
@RequiredArgsConstructor
public class ProductReader implements ItemReader<Product> {

	private final ProductRepository productRepository;
	public static int CHUNK_SIZE = 10;

	// 현재 페이지, 페이지 내 커서, 전체 건수, 현재 리스트 상태
	private int currentPage = 0;
	private int cursorInPage = 0;
	private int totalCount = -1;
	private List<Product> currentList = Collections.emptyList();

	@Override
	public Product read() {
		// 최초 호출 시 전체 건수 조회
		if (totalCount < 0) {
			totalCount = productRepository.countAll();
		}

		// 현재 페이지 리스트 소진 시 다음 페이지 로드
		if (cursorInPage >= currentList.size()) {
			int offset = currentPage * CHUNK_SIZE;
			if (offset >= totalCount) {
				return null;             // 더 이상 읽을 데이터 없음
			}
			currentList = productRepository.findPage(offset, CHUNK_SIZE);
			currentPage++;
			cursorInPage = 0;
			if (currentList.isEmpty()) {
				return null;
			}
		}

		// 페이지 내 다음 아이템 반환
		return currentList.get(cursorInPage++);
	}
}
