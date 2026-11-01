package kz.iitu.spring_lab_01.bank;

import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransferService {

    private final AccountRepository repository;

    public TransferService(AccountRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void transfer(Long fromId, Long toId, BigDecimal amount) {
        Account from = repository.findById(fromId).orElseThrow();
        Account to = repository.findById(toId).orElseThrow();

        from.debit(amount);
        to.credit(amount);
        repository.save(from);
        repository.save(to);
    }

    // Task A, case 3: unchecked exception after the debit but before the credit.
    @Transactional
    public void transferWithFailure(Long fromId, Long toId, BigDecimal amount) {
        Account from = repository.findById(fromId).orElseThrow();
        Account to = repository.findById(toId).orElseThrow();

        from.debit(amount);
        repository.saveAndFlush(from); // the UPDATE really reaches the database
        if (amount.signum() > 0) {
            throw new IllegalStateException("Simulated failure after debit, before credit");
        }
        to.credit(amount);
        repository.save(to);
    }

    // Task A, case 4a: checked exception, default rules -> the debit is COMMITTED.
    @Transactional
    public void transferCheckedDefault(Long fromId, Long toId, BigDecimal amount)
            throws TransferInterruptedException {
        debitThenInterrupt(fromId, toId, amount);
    }

    // Task A, case 4b: checked exception with rollbackFor -> the debit is rolled back.
    @Transactional(rollbackFor = TransferInterruptedException.class)
    public void transferCheckedRollbackFor(Long fromId, Long toId, BigDecimal amount)
            throws TransferInterruptedException {
        debitThenInterrupt(fromId, toId, amount);
    }

    // Used by the isolation experiment as the "other" transaction.
    @Transactional
    public void deposit(Long id, BigDecimal amount) {
        Account account = repository.findById(id).orElseThrow();
        account.credit(amount);
    }

    private void debitThenInterrupt(Long fromId, Long toId, BigDecimal amount)
            throws TransferInterruptedException {
        Account from = repository.findById(fromId).orElseThrow();
        repository.findById(toId).orElseThrow();
        from.debit(amount);
        repository.saveAndFlush(from);
        throw new TransferInterruptedException("Checked failure after debit, before credit");
    }
}
