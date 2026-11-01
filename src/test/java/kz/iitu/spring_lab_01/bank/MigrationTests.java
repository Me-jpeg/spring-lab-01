package kz.iitu.spring_lab_01.bank;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

// Task E: every versioned migration is recorded once in flyway_schema_history.
class MigrationTests extends AbstractBankTest {

    @Autowired
    private Flyway flyway;

    @Test
    void allMigrationsAreAppliedAndNothingIsPending() {
        List<Map<String, Object>> history = jdbc.queryForList(
                "select version, script, success from flyway_schema_history "
                        + "where version is not null order by installed_rank");
        history.forEach(row -> report("E1", "%s", row));

        assertThat(history).extracting(row -> row.get("version"))
                .startsWith("1", "2", "3", "4")
                .doesNotHaveDuplicates();
        assertThat(history).allSatisfy(row -> assertThat(row.get("success")).isEqualTo(true));
        assertThat(flyway.info().pending()).isEmpty();
        report("E2", "current schema version=%s, pending=%d",
                flyway.info().current().getVersion(), flyway.info().pending().length);
    }
}
