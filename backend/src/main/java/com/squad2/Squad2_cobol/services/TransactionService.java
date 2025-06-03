package com.squad2.Squad2_cobol.services;

import com.squad2.Squad2_cobol.models.entities.Account;
import com.squad2.Squad2_cobol.models.entities.Transaction;
import com.squad2.Squad2_cobol.models.enums.TransactionType;
import com.squad2.Squad2_cobol.repositories.AccountRepository;
import com.squad2.Squad2_cobol.repositories.TransactionRepository;
import com.squad2.Squad2_cobol.web.dtos.TransactionDTO;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    private TransactionDTO toDTO(Transaction transaction) {
        TransactionDTO dto = new TransactionDTO();
        dto.setId(transaction.getId());
        dto.setAccountId(transaction.getAccount().getId());
        dto.setType(transaction.getType());
        dto.setAmount(transaction.getAmount());
        dto.setDateTime(transaction.getDateTime());
        return dto;
    }

    private Transaction toEntity(TransactionDTO dto, Account account) {
        return Transaction.builder()
                .id(dto.getId())
                .account(account)
                .type(dto.getType())
                .amount(dto.getAmount())
                .dateTime(dto.getDateTime() != null ? dto.getDateTime() : LocalDateTime.now())
                .build();
    }

    @Transactional
    public TransactionDTO createTransaction(TransactionDTO dto) {
        if (dto.getAccountId() == null || dto.getType() == null || dto.getAmount() == null) {
            throw new IllegalArgumentException("Account ID, type and amount are required.");
        }
        if (dto.getType() != TransactionType.DEPOSIT &&
            dto.getType() != TransactionType.WITHDRAWAL &&
            dto.getType() != TransactionType.TRANSFER) {
            throw new IllegalArgumentException("Type must be DEPOSIT, WITHDRAWAL or TRANSFER.");
        }
        if (dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive.");
        }

        Account originAccount = accountRepository.findById(dto.getAccountId())
                .orElseThrow(() -> new EntityNotFoundException("Origin account not found with ID: " + dto.getAccountId()));

        if (dto.getType() == TransactionType.TRANSFER) {
            if (dto.getDestinationAccountId() == null) {
                throw new IllegalArgumentException("Destination account ID is required for transfer.");
            }
            if (dto.getDestinationAccountId().equals(dto.getAccountId())) {
                throw new IllegalArgumentException("Origin and destination accounts must be different.");
            }
            Account destinationAccount = accountRepository.findById(dto.getDestinationAccountId())
                    .orElseThrow(() -> new EntityNotFoundException("Destination account not found with ID: " + dto.getDestinationAccountId()));

            if (originAccount.getBalance().compareTo(dto.getAmount()) < 0) {
                throw new IllegalArgumentException("Insufficient balance for this operation.");
            }

            // Debita da origem
            originAccount.setBalance(originAccount.getBalance().subtract(dto.getAmount()));
            accountRepository.save(originAccount);

            // Credita na conta de destino
            destinationAccount.setBalance(destinationAccount.getBalance().add(dto.getAmount()));
            accountRepository.save(destinationAccount);

            // Registra a transação na conta de origem
            Transaction originTransaction = toEntity(dto, originAccount);
            transactionRepository.save(originTransaction);

            // Registra a transação na conta de destino (pode customizar o DTO se quiser diferenciar)
            TransactionDTO destDto = new TransactionDTO();
            destDto.setAccountId(destinationAccount.getId());
            destDto.setType(TransactionType.DEPOSIT); // Ou TRANSFER, se preferir
            destDto.setAmount(dto.getAmount());
            destDto.setDateTime(dto.getDateTime() != null ? dto.getDateTime() : LocalDateTime.now());
            Transaction destTransaction = toEntity(destDto, destinationAccount);
            transactionRepository.save(destTransaction);

            return toDTO(originTransaction);
        }

        // Validação de saldo para saque
        if (dto.getType() == TransactionType.WITHDRAWAL && originAccount.getBalance().compareTo(dto.getAmount()) < 0) {
            throw new IllegalArgumentException("Insufficient balance for this operation.");
        }

        // Atualiza saldo da conta
        if (dto.getType() == TransactionType.DEPOSIT) {
            originAccount.setBalance(originAccount.getBalance().add(dto.getAmount()));
        } else if (dto.getType() == TransactionType.WITHDRAWAL) {
            originAccount.setBalance(originAccount.getBalance().subtract(dto.getAmount()));
        }
        accountRepository.save(originAccount);

        Transaction transaction = toEntity(dto, originAccount);
        return toDTO(transactionRepository.save(transaction));
    }

    @Transactional(readOnly = true)
    public List<TransactionDTO> getTransactionsByAccount(Long accountId) {
        return transactionRepository.findByAccountId(accountId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TransactionDTO getTransactionById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found with ID: " + id));
        return toDTO(transaction);
    }
}