package com.squad2.Squad2_cobol.services;

import com.squad2.Squad2_cobol.exceptions.GlobalExceptionHandler;
import com.squad2.Squad2_cobol.models.entities.Customer;
import com.squad2.Squad2_cobol.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Customer saveCustomer(Customer customer) {
        try {

            if (customer.getName() == null || customer.getName().isEmpty()) {
                throw new GlobalExceptionHandler.InvalidCredentialsException("Name cannot be null or empty. Please check and try again.");
            }
            if (customer.getEmail() == null || customer.getEmail().isEmpty()) {
                throw new GlobalExceptionHandler.InvalidCredentialsException("Email cannot be null or empty. Please check and try again.");
            }
            if (customer.getPassword() == null || customer.getPassword().isEmpty()) {
                throw new GlobalExceptionHandler.InvalidCredentialsException("Password cannot be null or empty. Please check and try again.");
            }
            if (customer.getBirthdate() == null) {
                throw new GlobalExceptionHandler.InvalidCredentialsException("Birthdate cannot be null. Please check and try again.");
            }
            if (customer.getRole() == null) {
                throw new GlobalExceptionHandler.InvalidCredentialsException("Role cannot be null. Please check and try again.");
            }


            Optional<Customer> existingCustomer = customerRepository.findByEmail(customer.getEmail());
            if (existingCustomer.isPresent()) {
                throw new GlobalExceptionHandler.CustomerUniqueViolationException(
                        "A customer is already registered with this email address."
                );
            }


            customer.setPassword(passwordEncoder.encode(customer.getPassword()));
            return customerRepository.save(customer);
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            throw new GlobalExceptionHandler.CustomerUniqueViolationException(
                    "Integrity error: this email address is already in use."
            );
        }
    }

    @Transactional(readOnly = true)
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Customer getCustomerById(Long id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with ID: " + id));
    }

    @Transactional
    public Customer updateCustomer(Long id, Customer customerDetails) {
        Customer customer = getCustomerById(id);

        customer.setName(customerDetails.getName());
        customer.setEmail(customerDetails.getEmail());
        customer.setBirthdate(customerDetails.getBirthdate());

        if (customerDetails.getPassword() != null && !customerDetails.getPassword().isEmpty()) {
            customer.setPassword(passwordEncoder.encode(customerDetails.getPassword()));
        }

        try {
            return customerRepository.save(customer);
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            throw new GlobalExceptionHandler.CustomerUniqueViolationException(
                    "Email address is already associated with another customer."
            );
        }
    }

    @Transactional
    public void deleteCustomer(Long id) {
        Customer customer = getCustomerById(id);
        customerRepository.delete(customer);
    }
}
