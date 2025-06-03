package com.squad2.Squad2_cobol.services;

import com.squad2.Squad2_cobol.exceptions.GlobalExceptionHandler;
import com.squad2.Squad2_cobol.models.entities.Account;
import com.squad2.Squad2_cobol.models.entities.Customer;
import com.squad2.Squad2_cobol.models.enums.AccountType;
import com.squad2.Squad2_cobol.repositories.AccountRepository;
import com.squad2.Squad2_cobol.repositories.CustomerRepository;
import com.squad2.Squad2_cobol.web.dtos.AccountDTO;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;
    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private AccountService accountService;

    private Customer customer;
    private Account account;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        customer = Customer.builder().id(1L).build();
        account = Account.builder()
                .id(10L)
                .customer(customer)
                .type(AccountType.CHECKING)
                .balance(BigDecimal.TEN)
                .build();
    }

    @Test
    void createAccount_success() {
        AccountDTO dto = new AccountDTO();
        dto.setCustomerId(1L);
        dto.setType(AccountType.CHECKING);
        dto.setBalance(BigDecimal.TEN);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(accountRepository.existsByCustomerIdAndType(1L, AccountType.CHECKING)).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        AccountDTO result = accountService.createAccount(dto);

        assertEquals(account.getId(), result.getId());
        assertEquals(account.getType(), result.getType());
        assertEquals(account.getBalance(), result.getBalance());
    }

    @Test
    void createAccount_throwsIfCustomerNotFound() {
        AccountDTO dto = new AccountDTO();
        dto.setCustomerId(2L);
        dto.setType(AccountType.SAVINGS);

        when(customerRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> accountService.createAccount(dto));
    }

    @Test
    void createAccount_throwsIfAccountExists() {
        AccountDTO dto = new AccountDTO();
        dto.setCustomerId(1L);
        dto.setType(AccountType.CHECKING);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(accountRepository.existsByCustomerIdAndType(1L, AccountType.CHECKING)).thenReturn(true);

        assertThrows(GlobalExceptionHandler.CustomerUniqueViolationException.class, () -> accountService.createAccount(dto));
    }

    @Test
    void getAccountsByCustomer_success() {
        when(customerRepository.existsById(1L)).thenReturn(true);
        when(accountRepository.findByCustomerId(1L)).thenReturn(List.of(account));

        List<AccountDTO> result = accountService.getAccountsByCustomer(1L);

        assertEquals(1, result.size());
        assertEquals(account.getId(), result.get(0).getId());
    }

    @Test
    void getAccountsByCustomer_throwsIfCustomerNotFound() {
        when(customerRepository.existsById(2L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> accountService.getAccountsByCustomer(2L));
    }

    @Test
    void getAccountById_success() {
        when(accountRepository.findById(10L)).thenReturn(Optional.of(account));

        AccountDTO result = accountService.getAccountById(10L);

        assertEquals(account.getId(), result.getId());
    }

    @Test
    void getAccountById_throwsIfNotFound() {
        when(accountRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> accountService.getAccountById(99L));
    }

    @Test
    void updateAccount_success() {
        AccountDTO dto = new AccountDTO();
        dto.setType(AccountType.SAVINGS);
        dto.setBalance(BigDecimal.valueOf(100));

        when(accountRepository.findById(10L)).thenReturn(Optional.of(account));
        when(accountRepository.existsByCustomerIdAndType(1L, AccountType.SAVINGS)).thenReturn(false);
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        AccountDTO result = accountService.updateAccount(10L, dto);

        assertEquals(AccountType.SAVINGS, result.getType());
        assertEquals(BigDecimal.valueOf(100), result.getBalance());
    }

    @Test
    void updateAccount_throwsIfAccountNotFound() {
        AccountDTO dto = new AccountDTO();
        when(accountRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> accountService.updateAccount(99L, dto));
    }

    @Test
    void deleteAccount_success() {
        when(accountRepository.findById(10L)).thenReturn(Optional.of(account));
        doNothing().when(accountRepository).delete(account);

        assertDoesNotThrow(() -> accountService.deleteAccount(10L));
        verify(accountRepository).delete(account);
    }

    @Test
    void deleteAccount_throwsIfNotFound() {
        when(accountRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> accountService.deleteAccount(99L));
    }
}