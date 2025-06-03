package com.squad2.Squad2_cobol.services;

import com.squad2.Squad2_cobol.exceptions.GlobalExceptionHandler;
import com.squad2.Squad2_cobol.models.entities.Account;
import com.squad2.Squad2_cobol.models.entities.Customer;
import com.squad2.Squad2_cobol.models.enums.AccountType;
import com.squad2.Squad2_cobol.repositories.AccountRepository;
import com.squad2.Squad2_cobol.repositories.CustomerRepository;
import com.squad2.Squad2_cobol.web.dtos.AccountDTO;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;

    private AccountDTO toDTO(Account account) {
        AccountDTO dto = new AccountDTO();
        dto.setId(account.getId());
        dto.setCustomerId(account.getCustomer().getId());
        dto.setType(account.getType());
        dto.setBalance(account.getBalance());
        dto.setTitle(account.getTitle());
        return dto;
    }

    private Account toEntity(AccountDTO dto, Customer customer) {
        return Account.builder()
                .id(dto.getId())
                .customer(customer)
                .type(dto.getType())
                .balance(dto.getBalance() != null ? dto.getBalance() : BigDecimal.ZERO)
                .title(dto.getTitle())
                .build();
    }

    @Transactional
    public AccountDTO createAccount(AccountDTO dto) {
        if (dto.getCustomerId() == null || dto.getType() == null) {
            throw new GlobalExceptionHandler.InvalidCredentialsException("Customer ID and Account type are required.");
        }
        if (dto.getBalance() != null && dto.getBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new GlobalExceptionHandler.InvalidCredentialsException("Initial balance cannot be negative.");
        }

        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with ID: " + dto.getCustomerId()));

        boolean exists = accountRepository.existsByCustomerIdAndType(dto.getCustomerId(), dto.getType());
        if (exists) {
            throw new GlobalExceptionHandler.CustomerUniqueViolationException(
                    "Customer already has an account of type: " + dto.getType()
            );
        }

        Account account = toEntity(dto, customer);

        try {
            return toDTO(accountRepository.save(account));
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            throw new GlobalExceptionHandler.CustomerUniqueViolationException(
                    "Integrity error: could not create account."
            );
        }
    }

    @Transactional(readOnly = true)
    public List<AccountDTO> getAccountsByCustomer(Long customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new EntityNotFoundException("Customer not found with ID: " + customerId);
        }
        return accountRepository.findByCustomerId(customerId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AccountDTO getAccountById(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Account not found with ID: " + id));
        return toDTO(account);
    }

    @Transactional
    public AccountDTO updateAccount(Long id, AccountDTO dto) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Account not found with ID: " + id));

        if (dto.getType() != null) {
            boolean exists = accountRepository.existsByCustomerIdAndType(account.getCustomer().getId(), dto.getType());
            if (exists && !account.getType().equals(dto.getType())) {
                throw new GlobalExceptionHandler.CustomerUniqueViolationException(
                        "Customer already has an account of type: " + dto.getType()
                );
            }
            account.setType(dto.getType());
        }

        if (dto.getBalance() != null) {
            if (dto.getBalance().compareTo(BigDecimal.ZERO) < 0) {
                throw new GlobalExceptionHandler.InvalidCredentialsException("Balance cannot be negative.");
            }
            account.setBalance(dto.getBalance());
        }

        account.setTitle(dto.getTitle());

        try {
            return toDTO(accountRepository.save(account));
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            throw new GlobalExceptionHandler.CustomerUniqueViolationException(
                    "Integrity error: could not update account."
            );
        }
    }

    @Transactional
    public void deleteAccount(Long id) {
        Account acc = accountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Account not found with ID: " + id));
        accountRepository.delete(acc);
    }
}