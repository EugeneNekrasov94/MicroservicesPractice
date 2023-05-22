package com.example.bill.dto;

import com.example.bill.entity.Bill;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
@Getter//без геттера сущность не получить,406 ошибка
public class BillResponseDTO {
    private Long billId;

    private Long accountId;
    private BigDecimal amount;
    private Boolean isDefault;
    private OffsetDateTime creationDate;
    private Boolean overdraftEnabled;

    public BillResponseDTO(Bill bill) {
        this.billId = bill.getBillId();
        this.accountId = bill.getAccountId();
        this.amount = bill.getAmount();
        this.isDefault = bill.getIsDefault();
        this.creationDate = bill.getCreationDate();
        this.overdraftEnabled = bill.getOverdraftEnabled();
    }
}
