package com.squad2.Squad2_cobol.repositories;

import com.squad2.Squad2_cobol.models.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional <Customer>findById(Long id);
    Optional<Customer> findByEmail(String email);
}
