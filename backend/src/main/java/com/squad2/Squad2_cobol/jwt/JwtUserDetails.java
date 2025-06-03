package com.squad2.Squad2_cobol.jwt;

import com.squad2.Squad2_cobol.models.entities.Customer;
import lombok.Data;
import lombok.Getter;

import java.util.Collections;

@Getter
public class JwtUserDetails extends org.springframework.security.core.userdetails.User {
    private Customer customer;

    public JwtUserDetails(Customer customer) {
        super(customer.getEmail(), customer.getPassword(), Collections.emptyList());
        this.customer = customer;
    }
}
