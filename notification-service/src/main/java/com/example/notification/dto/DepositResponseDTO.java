package com.example.notification.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class DepositResponseDTO {
    private BigDecimal amount;
    private String email;
}
