package com.squad2.Squad2_cobol.jwt;

import com.squad2.Squad2_cobol.repositories.CustomerRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class JwtUserDetailService implements UserDetailsService {

    private final CustomerRepository customerRepository;

    @Override
    public JwtUserDetails loadUserByUsername(String email) {
        var customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o email: " + email));

        return new JwtUserDetails(customer);
    }
}
