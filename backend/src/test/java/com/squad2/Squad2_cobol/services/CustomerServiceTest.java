package com.squad2.Squad2_cobol.services;

import com.squad2.Squad2_cobol.exceptions.GlobalExceptionHandler;
import com.squad2.Squad2_cobol.models.entities.Customer;
import com.squad2.Squad2_cobol.models.enums.UserRole;
import com.squad2.Squad2_cobol.repositories.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerServiceTest {

    private CustomerRepository customerRepository;
    private PasswordEncoder passwordEncoder;
    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        customerRepository = mock(CustomerRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        customerService = new CustomerService(customerRepository, passwordEncoder);
    }

    private Customer createValidCustomer() {
        Customer customer = new Customer();
        customer.setName("John Doe");
        customer.setEmail("john@example.com");
        customer.setPassword("password123");
        customer.setBirthdate(LocalDate.of(1990, 1, 1));
        customer.setRole(UserRole.valueOf("USER"));
        return customer;
    }

    @Test
    void testSaveCustomer_Successful() {
        Customer customer = createValidCustomer();
        when(customerRepository.findByEmail(customer.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(customer.getPassword())).thenReturn("encodedPassword");
        when(customerRepository.save(any(Customer.class))).thenAnswer(i -> i.getArgument(0));

        Customer saved = customerService.saveCustomer(customer);

        assertNotNull(saved);
        assertEquals("encodedPassword", saved.getPassword());
        verify(customerRepository).save(customer);
    }

    @Test
    void testSaveCustomer_ThrowsIfNameIsNull() {
        Customer customer = createValidCustomer();
        customer.setName(null);

        GlobalExceptionHandler.InvalidCredentialsException exception =
                assertThrows(GlobalExceptionHandler.InvalidCredentialsException.class,
                        () -> customerService.saveCustomer(customer));
        assertTrue(exception.getMessage().contains("Name cannot be null or empty"));
    }

    @Test
    void testSaveCustomer_ThrowsIfEmailExists() {
        Customer customer = createValidCustomer();
        when(customerRepository.findByEmail(customer.getEmail()))
                .thenReturn(Optional.of(new Customer()));

        GlobalExceptionHandler.CustomerUniqueViolationException exception =
                assertThrows(GlobalExceptionHandler.CustomerUniqueViolationException.class,
                        () -> customerService.saveCustomer(customer));
        assertTrue(exception.getMessage().contains("already registered"));
    }

    @Test
    void testSaveCustomer_ThrowsIfPasswordIsEmpty() {
        Customer customer = createValidCustomer();
        customer.setPassword("");

        GlobalExceptionHandler.InvalidCredentialsException exception =
                assertThrows(GlobalExceptionHandler.InvalidCredentialsException.class,
                        () -> customerService.saveCustomer(customer));
        assertTrue(exception.getMessage().contains("Password cannot be null or empty"));
    }
}
