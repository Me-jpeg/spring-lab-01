package kz.iitu.spring_lab_01.bank;

import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

// Shared setup for the lab 9 tests: real PostgreSQL schema "lab9" created by Flyway,
// transaction manager in DEBUG so the log shows begin / suspend / commit / rollback.
@SpringBootTest(properties = {
        "spring.jpa.show-sql=false",
        "logging.level.org.springframework.orm.jpa.JpaTransactionManager=DEBUG"
})
abstract class AbstractBankTest {

    @Autowired
    protected AccountRepository accounts;

    @Autowired
    protected AuditRecordRepository auditRecords;

    @Autowired
    protected JdbcTemplate jdbc;

    protected Long aliceId;
    protected Long bobId;

    @BeforeEach
    void resetBalances() {
        jdbc.update("update accounts set balance = 1000.00 where owner_name = 'Alice'");
        jdbc.update("update accounts set balance = 500.00 where owner_name = 'Bob'");
        jdbc.update("delete from audit_records");
        aliceId = accounts.findByOwnerName("Alice").orElseThrow().getId();
        bobId = accounts.findByOwnerName("Bob").orElseThrow().getId();
    }

    protected BigDecimal balance(Long id) {
        return accounts.findBalanceById(id);
    }

    protected static BigDecimal money(String value) {
        return new BigDecimal(value);
    }

    protected static void report(String task, String format, Object... args) {
        System.out.printf("[LAB9 " + task + "] " + format + "%n", args);
    }
}
