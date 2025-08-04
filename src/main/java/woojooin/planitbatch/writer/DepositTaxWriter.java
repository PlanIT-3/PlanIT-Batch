package woojooin.planitbatch.writer;

import java.util.List;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import woojooin.planitbatch.domain.mapper.DepositMapper;
import woojooin.planitbatch.domain.vo.DepositTaxResult;

/**
 * 예적금 세금 계산 결과를 DB에 저장하는 Writer
 */
@Component
public class DepositTaxWriter implements ItemWriter<DepositTaxResult> {

    @Autowired
    private DepositMapper depositMapper;

    @Override
    public void write(List<? extends DepositTaxResult> results) throws Exception {
        
        if (results == null || results.isEmpty()) {
            return;
        }
        
        for (DepositTaxResult result : results) {
            // 1. 세금 계산 결과 저장
            depositMapper.insertDepositTaxResult(result);
            
            // 2. 해당 계좌의 마지막 계산일 업데이트
            depositMapper.updateLastCalculationDate(result.getAccountId());
        }
        
        System.out.println("Processed " + results.size() + " deposit tax calculations");
    }
}