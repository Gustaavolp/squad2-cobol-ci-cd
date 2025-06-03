package com.squad2.Squad2_cobol.controllers;

import com.squad2.Squad2_cobol.models.entities.Customer;
import com.squad2.Squad2_cobol.services.CustomerService;
import com.squad2.Squad2_cobol.web.controllers.CustomerController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerControllerTest {

    @InjectMocks
    private CustomerController customerController;

    @Mock
    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateCustomer() {
        Customer input = new Customer();
        input.setName("vitor kley");

        Customer saved = new Customer();
        saved.setId(1L);
        saved.setName("vitor kley");

        when(customerService.saveCustomer(input)).thenReturn(saved);

        ResponseEntity<Customer> response = customerController.createCustomer(input);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(saved, response.getBody());
        verify(customerService).saveCustomer(input);
    }

    @Test
    void testGetAllCustomers() {
        Customer c1 = new Customer();
        c1.setId(1L);
        c1.setName("João");

        Customer c2 = new Customer();
        c2.setId(2L);
        c2.setName("Jane");

        List<Customer> customers = Arrays.asList(c1, c2);

        when(customerService.getAllCustomers()).thenReturn(customers);

        ResponseEntity<List<Customer>> response = customerController.getAllCustomers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(customers, response.getBody());
        verify(customerService).getAllCustomers();
    }

    @Test
    void testGetCustomerById() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("John");

        when(customerService.getCustomerById(1L)).thenReturn(customer);

        ResponseEntity<Customer> response = customerController.getCustomerById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(customer, response.getBody());
        verify(customerService).getCustomerById(1L);
    }

    @Test
    void testUpdateCustomer() {
        Customer updateInfo = new Customer();
        updateInfo.setName("Updated Name");

        Customer updatedCustomer = new Customer();
        updatedCustomer.setId(1L);
        updatedCustomer.setName("Updated Name");

        when(customerService.updateCustomer(1L, updateInfo)).thenReturn(updatedCustomer);

        ResponseEntity<Customer> response = customerController.updateCustomer(1L, updateInfo);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedCustomer, response.getBody());
        verify(customerService).updateCustomer(1L, updateInfo);
    }

    @Test
    void testDeleteCustomer() {
        doNothing().when(customerService).deleteCustomer(1L);

        ResponseEntity<Void> response = customerController.deleteCustomer(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(customerService).deleteCustomer(1L);
    }
}
