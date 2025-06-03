package com.squad2.Squad2_cobol.web.dtos;

import com.squad2.Squad2_cobol.models.enums.AccountType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountDTO {
    private Long id;
    private Long customerId;
    private AccountType type;
    private BigDecimal balance;
    private String title;
}