package com.squad2.Squad2_cobol.web.controllers;

import com.squad2.Squad2_cobol.services.TransactionService;
import com.squad2.Squad2_cobol.web.dtos.TransactionDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @Operation(summary = "Create a new transaction",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Transaction created successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TransactionDTO.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid data for transaction creation",
                            content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content)
            })
    @PostMapping
    public ResponseEntity<TransactionDTO> create(@Valid @RequestBody TransactionDTO transactionDTO) {
        return ResponseEntity.status(201).body(transactionService.createTransaction(transactionDTO));
    }

    @Operation(summary = "List transactions by account",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of transactions retrieved successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TransactionDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Account not found",
                            content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content)
            })
    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<TransactionDTO>> listByAccount(@PathVariable Long accountId) {
        return ResponseEntity.ok(transactionService.getTransactionsByAccount(accountId));
    }

    @Operation(summary = "Get transaction by ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Transaction found successfully",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = TransactionDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Transaction not found",
                            content = @Content),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content)
            })
    @GetMapping("/{id}")
    public ResponseEntity<TransactionDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(transactionService.getTransactionById(id));
    }
}
