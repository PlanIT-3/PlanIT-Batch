package woojooin.planitbatch.reader;

import java.util.Iterator;
import java.util.List;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import woojooin.planitbatch.domain.mapper.DepositMapper;
import woojooin.planitbatch.domain.vo.DepositAccount;

/**
 * 예적금 계좌 정보를 읽어오는 Reader
 */
@Component
public class DepositAccountReader implements ItemReader<DepositAccount> {

    @Autowired
    private DepositMapper depositMapper;
    
    private Iterator<DepositAccount> accountIterator;
    private boolean isInitialized = false;

    @Override
    public DepositAccount read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        
        // 최초 실행시 데이터 로드
        if (!isInitialized) {
            List<DepositAccount> accounts = depositMapper.selectDepositAccountsForTaxCalculation();
            accountIterator = accounts.iterator();
            isInitialized = true;
        }
        
        // 다음 데이터 반환, 없으면 null (배치 종료)
        return accountIterator.hasNext() ? accountIterator.next() : null;
    }
}