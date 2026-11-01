package kz.iitu.spring_lab_01.bank;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

// Task A: declarative transactions and rollback.
class TransferTransactionTests extends AbstractBankTest {

    @Autowired
    private TransferService transfers;

    @Test
    void successfulTransferMovesMoney() {
        transfers.transfer(aliceId, bobId, money("100.00"));

        report("A1", "transfer 100.00 Alice->Bob committed: Alice=%s Bob=%s", balance(aliceId), balance(bobId));
        assertThat(balance(aliceId)).isEqualByComparingTo("900.00");
        assertThat(balance(bobId)).isEqualByComparingTo("600.00");
    }

    @Test
    void insufficientFundsChangesNothing() {
        assertThatThrownBy(() -> transfers.transfer(aliceId, bobId, money("5000.00")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Insufficient funds");

        report("A2", "transfer 5000.00 rejected: Alice=%s Bob=%s", balance(aliceId), balance(bobId));
        assertThat(balance(aliceId)).isEqualByComparingTo("1000.00");
        assertThat(balance(bobId)).isEqualByComparingTo("500.00");
    }

    @Test
    void uncheckedExceptionAfterDebitRollsBack() {
        assertThatThrownBy(() -> transfers.transferWithFailure(aliceId, bobId, money("100.00")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("after debit, before credit");

        report("A3", "unchecked exception after flushed debit -> rollback: Alice=%s Bob=%s",
                balance(aliceId), balance(bobId));
        assertThat(balance(aliceId)).isEqualByComparingTo("1000.00");
        assertThat(balance(bobId)).isEqualByComparingTo("500.00");
    }

    @Test
    void checkedExceptionCommitsByDefault() {
        assertThatThrownBy(() -> transfers.transferCheckedDefault(aliceId, bobId, money("100.00")))
                .isInstanceOf(TransferInterruptedException.class);

        report("A4", "checked exception, default rules -> COMMIT, 100.00 lost: Alice=%s Bob=%s",
                balance(aliceId), balance(bobId));
        assertThat(balance(aliceId)).isEqualByComparingTo("900.00");
        assertThat(balance(bobId)).isEqualByComparingTo("500.00");
    }

    @Test
    void checkedExceptionRollsBackWithRollbackFor() {
        assertThatThrownBy(() -> transfers.transferCheckedRollbackFor(aliceId, bobId, money("100.00")))
                .isInstanceOf(TransferInterruptedException.class);

        report("A5", "checked exception + rollbackFor -> rollback: Alice=%s Bob=%s",
                balance(aliceId), balance(bobId));
        assertThat(balance(aliceId)).isEqualByComparingTo("1000.00");
        assertThat(balance(bobId)).isEqualByComparingTo("500.00");
    }
}
