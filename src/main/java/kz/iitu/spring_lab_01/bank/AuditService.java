package kz.iitu.spring_lab_01.bank;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

// Inner methods of the propagation experiment. They live in a separate bean so that
// calls from PropagationDemoService go through the Spring transactional proxy.
@Service
public class AuditService {

    private final AuditRecordRepository repository;

    public AuditService(AuditRecordRepository repository) {
        this.repository = repository;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void logRequired(String action, String details) {
        repository.save(new AuditRecord(action, details));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logRequiresNew(String action, String details) {
        repository.save(new AuditRecord(action, details));
    }
}
