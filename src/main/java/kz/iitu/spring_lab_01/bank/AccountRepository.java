package kz.iitu.spring_lab_01.bank;

import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByOwnerName(String ownerName);

    // Scalar query always goes to the database, bypassing the persistence-context cache.
    @Query("select a.balance from Account a where a.id = :id")
    BigDecimal findBalanceById(Long id);
}
