package kz.iitu.spring_lab_01.bank;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// Lab 9 endpoints for manual checks and screenshots. The controller itself is NOT
// transactional: every service/repository call below is its own transaction.
@RestController
@RequestMapping("/api/lab9")
public class BankController {

    private final AccountRepository accounts;
    private final AuditRecordRepository auditRecords;
    private final TransferService transfers;
    private final PropagationDemoService propagation;
    private final JdbcTemplate jdbc;

    public BankController(AccountRepository accounts, AuditRecordRepository auditRecords,
            TransferService transfers, PropagationDemoService propagation, JdbcTemplate jdbc) {
        this.accounts = accounts;
        this.auditRecords = auditRecords;
        this.transfers = transfers;
        this.propagation = propagation;
        this.jdbc = jdbc;
    }

    @GetMapping("/accounts")
    public List<Account> accounts() {
        return accounts.findAll();
    }

    @PostMapping("/reset")
    public Map<String, Object> reset() {
        jdbc.update("update accounts set balance = 1000.00 where owner_name = 'Alice'");
        jdbc.update("update accounts set balance = 500.00 where owner_name = 'Bob'");
        jdbc.update("delete from audit_records");
        return result("reset", "balances restored to 1000.00 / 500.00, audit cleared");
    }

    @PostMapping("/transfer")
    public Map<String, Object> transfer(@RequestParam Long from, @RequestParam Long to,
            @RequestParam BigDecimal amount) {
        transfers.transfer(from, to, amount);
        return result("transfer", "committed: " + amount + " from " + from + " to " + to);
    }

    @PostMapping("/transfer/fail")
    public Map<String, Object> transferWithFailure(@RequestParam Long from, @RequestParam Long to,
            @RequestParam BigDecimal amount) {
        transfers.transferWithFailure(from, to, amount);
        return result("transfer/fail", "unreachable");
    }

    @PostMapping("/propagation/required")
    public Map<String, Object> propagationRequired(@RequestParam Long account,
            @RequestParam BigDecimal amount) {
        propagation.outerWithRequired(account, amount);
        return result("propagation/required", "unreachable");
    }

    @PostMapping("/propagation/requires-new")
    public Map<String, Object> propagationRequiresNew(@RequestParam Long account,
            @RequestParam BigDecimal amount) {
        propagation.outerWithRequiresNew(account, amount);
        return result("propagation/requires-new", "unreachable");
    }

    @GetMapping("/audit")
    public List<AuditRecord> audit() {
        return auditRecords.findAll();
    }

    // Task D: two copies loaded in two separate persistence contexts (two transactions).
    @PostMapping("/optimistic")
    public Map<String, Object> optimistic(@RequestParam Long account, @RequestParam BigDecimal amount) {
        Account first = accounts.findById(account).orElseThrow();
        Account stale = accounts.findById(account).orElseThrow();
        Long loadedVersion = first.getVersion();

        first.credit(amount);
        accounts.save(first);

        stale.credit(amount);
        accounts.save(stale); // throws ObjectOptimisticLockingFailureException
        return result("optimistic", "unreachable, loaded version " + loadedVersion);
    }

    @GetMapping("/migrations")
    public List<Map<String, Object>> migrations() {
        return jdbc.queryForList("select installed_rank, version, description, script, checksum, "
                + "installed_on, execution_time, success from flyway_schema_history order by installed_rank");
    }

    @ExceptionHandler({IllegalStateException.class, IllegalArgumentException.class})
    public ResponseEntity<Map<String, Object>> rolledBack(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(result("rolled back", ex.getClass().getSimpleName() + ": " + ex.getMessage()));
    }

    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<Map<String, Object>> optimisticConflict(ObjectOptimisticLockingFailureException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(result("optimistic lock conflict", ex.getMessage()));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, Object>> notFound(NoSuchElementException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result("not found", ex.getMessage()));
    }

    private Map<String, Object> result(String operation, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("operation", operation);
        body.put("message", message);
        body.put("accounts", accounts.findAll());
        body.put("auditRecords", auditRecords.count());
        return body;
    }
}
