package com.squad2.Squad2_cobol.repositories;

import com.squad2.Squad2_cobol.models.entities.Account;
import com.squad2.Squad2_cobol.models.enums.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Long> {
    List<Account> findByCustomerId(Long customerId);
    boolean existsByCustomerIdAndType(Long customerId, AccountType type);
}