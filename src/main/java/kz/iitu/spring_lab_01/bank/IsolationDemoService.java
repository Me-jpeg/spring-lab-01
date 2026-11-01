package kz.iitu.spring_lab_01.bank;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

// Task C: read the same balance twice inside ONE transaction; between the reads
// another transaction (another thread) updates and commits the row.
@Service
public class IsolationDemoService {

    private final AccountRepository accounts;

    public IsolationDemoService(AccountRepository accounts) {
        this.accounts = accounts;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED, readOnly = true)
    public List<BigDecimal> readTwiceReadCommitted(Long id, Runnable betweenReads) {
        return readTwice(id, betweenReads);
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ, readOnly = true)
    public List<BigDecimal> readTwiceRepeatableRead(Long id, Runnable betweenReads) {
        return readTwice(id, betweenReads);
    }

    private List<BigDecimal> readTwice(Long id, Runnable betweenReads) {
        BigDecimal first = accounts.findBalanceById(id);
        betweenReads.run();
        BigDecimal second = accounts.findBalanceById(id);
        return List.of(first, second);
    }
}
