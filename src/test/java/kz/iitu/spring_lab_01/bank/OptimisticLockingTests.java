package kz.iitu.spring_lab_01.bank;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

// Task D: the test method has no transaction, so every findById below runs in its own
// transaction and persistence context and returns an independent (detached) copy.
class OptimisticLockingTests extends AbstractBankTest {

    @Test
    void staleCopyCannotOverwriteNewerUpdate() {
        Account first = accounts.findById(aliceId).orElseThrow();
        Account stale = accounts.findById(aliceId).orElseThrow();
        Long loadedVersion = first.getVersion();

        first.credit(money("10.00"));
        accounts.save(first);
        Long committedVersion = accounts.findById(aliceId).orElseThrow().getVersion();
        report("D1", "first copy committed: version %d -> %d, Alice=%s", loadedVersion, committedVersion, balance(aliceId));

        stale.credit(money("20.00"));
        assertThatThrownBy(() -> accounts.save(stale))
                .isInstanceOf(ObjectOptimisticLockingFailureException.class)
                .satisfies(e -> report("D2", "stale copy (version %d) rejected: %s", stale.getVersion(), e.getMessage()));

        assertThat(committedVersion).isEqualTo(loadedVersion + 1);
        assertThat(balance(aliceId)).isEqualByComparingTo("1010.00");
    }

    @Test
    void conflictIsResolvedByReloadingAndRetrying() {
        Account first = accounts.findById(aliceId).orElseThrow();
        Account stale = accounts.findById(aliceId).orElseThrow();
        first.credit(money("10.00"));
        accounts.save(first);

        stale.credit(money("20.00"));
        try {
            accounts.save(stale);
        } catch (ObjectOptimisticLockingFailureException conflict) {
            // Safe response: re-read the current state and re-apply the (idempotent here) change once.
            Account fresh = accounts.findById(aliceId).orElseThrow();
            fresh.credit(money("20.00"));
            accounts.save(fresh);
            report("D3", "conflict detected -> reloaded version %d and retried once", fresh.getVersion());
        }

        report("D3", "after retry Alice=%s (both updates kept)", balance(aliceId));
        assertThat(balance(aliceId)).isEqualByComparingTo("1030.00");
    }
}
