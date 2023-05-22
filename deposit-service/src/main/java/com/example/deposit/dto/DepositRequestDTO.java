package com.example.deposit.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Setter
@Getter
public class DepositRequestDTO {
    private Long accountId;
    private Long billId;
    private BigDecimal amount;
}
