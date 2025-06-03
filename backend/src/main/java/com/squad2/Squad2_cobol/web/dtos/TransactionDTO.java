package com.squad2.Squad2_cobol.web.dtos;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.squad2.Squad2_cobol.models.enums.TransactionType;

@Data
public class TransactionDTO {
    private Long id;
    private Long accountId;
    private Long destinationAccountId; // NOVO CAMPO
    private TransactionType type;
    private BigDecimal amount;
    private LocalDateTime dateTime;
}