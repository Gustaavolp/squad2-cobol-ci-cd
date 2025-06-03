
package com.squad2.Squad2_cobol.services;

import com.squad2.Squad2_cobol.models.entities.Account;
import com.squad2.Squad2_cobol.models.entities.Transaction;
import com.squad2.Squad2_cobol.models.enums.TransactionType;
import com.squad2.Squad2_cobol.repositories.AccountRepository;
import com.squad2.Squad2_cobol.repositories.TransactionRepository;
import com.squad2.Squad2_cobol.web.dtos.TransactionDTO;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TransactionService transactionService;

    private Account account;
    private Transaction transaction;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        account = Account.builder().id(1L).balance(BigDecimal.valueOf(1000)).build();
        transaction = Transaction.builder()
                .id(10L)
                .account(account)
                .type(TransactionType.DEPOSIT)
                .amount(BigDecimal.valueOf(100))
                .build();
    }

    @Test
    void createTransaction_success() {
        TransactionDTO dto = new TransactionDTO();
        dto.setAccountId(1L);
        dto.setType(TransactionType.DEPOSIT);
        dto.setAmount(BigDecimal.valueOf(100));

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        TransactionDTO result = transactionService.createTransaction(dto);

        assertEquals(transaction.getId(), result.getId());
        assertEquals(transaction.getAmount(), result.getAmount());
        assertEquals(transaction.getType(), result.getType());
    }

    @Test
    void createTransaction_throwsIfAccountNotFound() {
        TransactionDTO dto = new TransactionDTO();
        dto.setAccountId(2L);
        dto.setType(TransactionType.DEPOSIT);
        dto.setAmount(BigDecimal.valueOf(100));

        when(accountRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> transactionService.createTransaction(dto));
    }

    @Test
    void createTransaction_throwsIfMissingFields() {
        TransactionDTO dto = new TransactionDTO();

        assertThrows(IllegalArgumentException.class, () -> transactionService.createTransaction(dto));
    }

    @Test
    void getTransactionById_success() {
        when(transactionRepository.findById(10L)).thenReturn(Optional.of(transaction));

        TransactionDTO result = transactionService.getTransactionById(10L);

        assertEquals(transaction.getId(), result.getId());
    }

    @Test
    void getTransactionById_throwsIfNotFound() {
        when(transactionRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> transactionService.getTransactionById(99L));
    }

    @Test
    void getTransactionsByAccount_success() {
        when(transactionRepository.findByAccountId(1L)).thenReturn(List.of(transaction));

        List<TransactionDTO> result = transactionService.getTransactionsByAccount(1L);

        assertEquals(1, result.size());
        assertEquals(transaction.getId(), result.get(0).getId());
    }
}