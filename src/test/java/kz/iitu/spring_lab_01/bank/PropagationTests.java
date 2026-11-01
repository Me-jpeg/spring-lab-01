package kz.iitu.spring_lab_01.bank;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

// Task B: REQUIRED joins the outer transaction, REQUIRES_NEW runs in its own one.
class PropagationTests extends AbstractBankTest {

    @Autowired
    private PropagationDemoService propagation;

    @Test
    void requiredAuditIsRolledBackWithOuterTransaction() {
        assertThatThrownBy(() -> propagation.outerWithRequired(aliceId, money("50.00")))
                .isInstanceOf(IllegalStateException.class);

        int audit = auditRecords.findByAction(PropagationDemoService.ACTION_REQUIRED).size();
        report("B1", "REQUIRED: outer rolled back -> audit records=%d, Alice=%s", audit, balance(aliceId));
        assertThat(audit).isZero();
        assertThat(balance(aliceId)).isEqualByComparingTo("1000.00");
    }

    @Test
    void requiresNewAuditSurvivesOuterRollback() {
        assertThatThrownBy(() -> propagation.outerWithRequiresNew(aliceId, money("50.00")))
                .isInstanceOf(IllegalStateException.class);

        int audit = auditRecords.findByAction(PropagationDemoService.ACTION_REQUIRES_NEW).size();
        report("B2", "REQUIRES_NEW: outer rolled back -> audit records=%d, Alice=%s", audit, balance(aliceId));
        assertThat(audit).isEqualTo(1);
        assertThat(balance(aliceId)).isEqualByComparingTo("1000.00");
    }
}
