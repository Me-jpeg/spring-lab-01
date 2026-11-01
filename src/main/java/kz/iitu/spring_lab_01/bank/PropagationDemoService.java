package kz.iitu.spring_lab_01.bank;

import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Outer methods of the propagation experiment: debit, write audit, then fail.
@Service
public class PropagationDemoService {

    public static final String ACTION_REQUIRED = "OUTER_REQUIRED";
    public static final String ACTION_REQUIRES_NEW = "OUTER_REQUIRES_NEW";

    private final AccountRepository accounts;
    private final AuditService audit;

    public PropagationDemoService(AccountRepository accounts, AuditService audit) {
        this.accounts = accounts;
        this.audit = audit;
    }

    @Transactional
    public void outerWithRequired(Long accountId, BigDecimal amount) {
        Account account = accounts.findById(accountId).orElseThrow();
        account.debit(amount);
        audit.logRequired(ACTION_REQUIRED, "debit " + amount + " from account " + accountId);
        throw new IllegalStateException("Outer transaction fails after inner REQUIRED returned");
    }

    @Transactional
    public void outerWithRequiresNew(Long accountId, BigDecimal amount) {
        Account account = accounts.findById(accountId).orElseThrow();
        account.debit(amount);
        audit.logRequiresNew(ACTION_REQUIRES_NEW, "debit " + amount + " from account " + accountId);
        throw new IllegalStateException("Outer transaction fails after inner REQUIRES_NEW returned");
    }
}
