package com.squad2.Squad2_cobol.web.controllers;

import com.squad2.Squad2_cobol.services.AccountService;
import com.squad2.Squad2_cobol.web.dtos.AccountDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @Operation(summary = "Create a new account",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Account created successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccountDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input data",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal error",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            })
    @PostMapping("/create")
    public ResponseEntity<AccountDTO> create(@Valid @RequestBody AccountDTO accountDTO) {
        return ResponseEntity.status(201).body(accountService.createAccount(accountDTO));
    }

    @Operation(summary = "Get accounts by customer ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of accounts retrieved successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccountDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Customer not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal error",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            })
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<AccountDTO>> listByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(accountService.getAccountsByCustomer(customerId));
    }

    @Operation(summary = "Get account by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Account found successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccountDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Account not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal error",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            })
    @GetMapping("/{id}")
    public ResponseEntity<AccountDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.getAccountById(id));
    }

    @Operation(summary = "Update account",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Account updated successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = AccountDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input data",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Account not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal error",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            })
    @PutMapping("/{id}")
    public ResponseEntity<AccountDTO> update(@PathVariable Long id, @Valid @RequestBody AccountDTO accountDTO) {
        return ResponseEntity.ok(accountService.updateAccount(id, accountDTO));
    }

    @Operation(summary = "Delete account",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Account deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Account not found",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(responseCode = "500", description = "Internal error",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
            })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }
}