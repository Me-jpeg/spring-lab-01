package kz.iitu.spring_lab_01.bank;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

// Task C: transaction 1 (test thread) reads the balance twice; between the reads
// transaction 2 (another thread) deposits 100.00 and commits. Database: PostgreSQL 16.
class IsolationTests extends AbstractBankTest {

    @Autowired
    private IsolationDemoService isolation;

    @Autowired
    private TransferService transfers;

    private ExecutorService otherThread;

    @BeforeEach
    void startOtherThread() {
        otherThread = Executors.newSingleThreadExecutor();
        report("C0", "database=%s, default isolation=%s",
                jdbc.queryForObject("select version()", String.class),
                jdbc.queryForObject("show default_transaction_isolation", String.class));
    }

    @AfterEach
    void stopOtherThread() {
        otherThread.shutdownNow();
    }

    @Test
    void readCommittedSeesCommittedChangeOnSecondRead() {
        List<BigDecimal> reads = isolation.readTwiceReadCommitted(aliceId, this::depositInOtherTransaction);

        report("C1", "READ_COMMITTED: first read=%s, second read=%s (non-repeatable read)",
                reads.get(0), reads.get(1));
        assertThat(reads.get(0)).isEqualByComparingTo("1000.00");
        assertThat(reads.get(1)).isEqualByComparingTo("1100.00");
    }

    @Test
    void repeatableReadKeepsSnapshotOnSecondRead() {
        List<BigDecimal> reads = isolation.readTwiceRepeatableRead(aliceId, this::depositInOtherTransaction);

        report("C2", "REPEATABLE_READ: first read=%s, second read=%s, committed value now=%s",
                reads.get(0), reads.get(1), balance(aliceId));
        assertThat(reads.get(0)).isEqualByComparingTo("1000.00");
        assertThat(reads.get(1)).isEqualByComparingTo("1000.00");
        assertThat(balance(aliceId)).isEqualByComparingTo("1100.00");
    }

    private void depositInOtherTransaction() {
        try {
            otherThread.submit(() -> transfers.deposit(aliceId, money("100.00"))).get();
            report("C*", "other thread committed deposit 100.00 for Alice");
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
