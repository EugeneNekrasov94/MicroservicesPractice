package com.example.account.dto;

import com.example.account.entity.Account;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class AccountResponseDTO {
    private Long accountId;
    private String name;
    private String phone;
    private String email;
    private OffsetDateTime creationDate;
    private List<Long> bills;

    public AccountResponseDTO(Account account) {
        this.accountId = account.getAccountId();
        this.name  = account.getName();
        this.phone = account.getPhone();
        this.email = account.getEmail();
        this.creationDate = account.getCreationDate();
        this.bills = account.getBills();
    }
}
