package kz.iitu.spring_lab_01.bank;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditRecordRepository extends JpaRepository<AuditRecord, Long> {

    List<AuditRecord> findByAction(String action);
}
