package com.squad2.Squad2_cobol.controllers;

import com.squad2.Squad2_cobol.web.controllers.TransactionController;
import com.squad2.Squad2_cobol.web.dtos.TransactionDTO;
import com.squad2.Squad2_cobol.services.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionControllerTest {

    @InjectMocks
    private TransactionController transactionController;

    @Mock
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateTransaction() {
        TransactionDTO input = new TransactionDTO();
        input.setAccountId(1L);

        TransactionDTO saved = new TransactionDTO();
        saved.setId(100L);
        saved.setAccountId(1L);

        when(transactionService.createTransaction(input)).thenReturn(saved);

        ResponseEntity<TransactionDTO> response = transactionController.create(input);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(saved, response.getBody());
        verify(transactionService).createTransaction(input);
    }

    @Test
    void testGetTransactionsByAccount() {
        TransactionDTO t1 = new TransactionDTO();
        t1.setId(100L);
        t1.setAccountId(1L);

        List<TransactionDTO> transactions = Arrays.asList(t1);

        when(transactionService.getTransactionsByAccount(1L)).thenReturn(transactions);

        ResponseEntity<List<TransactionDTO>> response = transactionController.listByAccount(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(transactions, response.getBody());
        verify(transactionService).getTransactionsByAccount(1L);
    }

    @Test
    void testGetTransactionById() {
        TransactionDTO transaction = new TransactionDTO();
        transaction.setId(100L);

        when(transactionService.getTransactionById(100L)).thenReturn(transaction);

        ResponseEntity<TransactionDTO> response = transactionController.getById(100L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(transaction, response.getBody());
        verify(transactionService).getTransactionById(100L);
    }
}