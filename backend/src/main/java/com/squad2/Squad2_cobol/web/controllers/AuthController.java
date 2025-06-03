package com.squad2.Squad2_cobol.web.controllers;

import com.squad2.Squad2_cobol.jwt.JwtToken;
import com.squad2.Squad2_cobol.jwt.JwtUtill;
import com.squad2.Squad2_cobol.models.entities.Customer;
import com.squad2.Squad2_cobol.repositories.CustomerRepository;
import com.squad2.Squad2_cobol.services.BlackListService;
import com.squad2.Squad2_cobol.services.CustomerService;
import com.squad2.Squad2_cobol.web.dtos.LoginRequest;
import com.squad2.Squad2_cobol.web.dtos.LoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final BlackListService blackListService;
    private final AuthenticationManager authenticationManager;
    private final CustomerService customerService;
    private final CustomerRepository customerRepository;

    @Operation(summary = "login",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User connected successfully",
                            content = @Content(mediaType = "application/Json", schema = @Schema(implementation = LoginResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal error",
                            content = @Content(mediaType = "application/Json", schema = @Schema(implementation = ErrorResponse.class))),
            })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());

        Authentication auth = authenticationManager.authenticate(authToken);
        SecurityContextHolder.getContext().setAuthentication(auth);

        JwtToken token = JwtUtill.createToken(request.getEmail());


        Customer customer = customerRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Customer not found"));


        LoginResponse response = LoginResponse.builder()
                .name(customer.getName())
                .email(customer.getEmail())
                .token(token.getToken())
                .build();

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "logout",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User disconnected successfully",
                            content = @Content(mediaType = "application/Json", schema = @Schema(implementation = Customer.class))),
                    @ApiResponse(responseCode = "403", description = "Action unaltorized",
                            content = @Content(mediaType = "application/Json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal error",
                            content = @Content(mediaType = "application/Json", schema = @Schema(implementation = ErrorResponse.class))),
            })
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith(JwtUtill.JWT_BEARER)) {
            String jwtToken = token.substring(JwtUtill.JWT_BEARER.length());

            blackListService.BlacklistToken(jwtToken);

            SecurityContextHolder.clearContext();

            Map<String, String> response = new HashMap<>();
            response.put("message", "Logout successful");
            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}