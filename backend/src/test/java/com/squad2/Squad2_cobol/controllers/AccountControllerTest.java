package com.squad2.Squad2_cobol.controllers;

import com.squad2.Squad2_cobol.web.controllers.AccountController;
import com.squad2.Squad2_cobol.web.dtos.AccountDTO;
import com.squad2.Squad2_cobol.services.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountControllerTest {

    @InjectMocks
    private AccountController accountController;

    @Mock
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateAccount() {
        AccountDTO input = new AccountDTO();
        input.setCustomerId(1L);

        AccountDTO saved = new AccountDTO();
        saved.setId(10L);
        saved.setCustomerId(1L);

        when(accountService.createAccount(input)).thenReturn(saved);

        ResponseEntity<AccountDTO> response = accountController.create(input);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(saved, response.getBody());
        verify(accountService).createAccount(input);
    }

    @Test
    void testGetAccountsByCustomer() {
        AccountDTO a1 = new AccountDTO();
        a1.setId(10L);
        a1.setCustomerId(1L);

        List<AccountDTO> accounts = Arrays.asList(a1);

        when(accountService.getAccountsByCustomer(1L)).thenReturn(accounts);

        ResponseEntity<List<AccountDTO>> response = accountController.listByCustomer(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(accounts, response.getBody());
        verify(accountService).getAccountsByCustomer(1L);
    }

    @Test
    void testGetAccountById() {
        AccountDTO account = new AccountDTO();
        account.setId(10L);

        when(accountService.getAccountById(10L)).thenReturn(account);

        ResponseEntity<AccountDTO> response = accountController.getById(10L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(account, response.getBody());
        verify(accountService).getAccountById(10L);
    }
}