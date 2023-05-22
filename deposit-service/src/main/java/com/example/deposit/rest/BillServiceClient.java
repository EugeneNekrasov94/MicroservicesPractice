package com.example.deposit.rest;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(value = "bill-service")
public interface BillServiceClient {
    @GetMapping(value = "bills/{billId}")
    BillResponseDTO getBillById(@PathVariable("billId") Long billId);

    @PutMapping(value = "bills/{billId}")
    void update(@PathVariable("billId") Long billId, BillRequestDTO requestDTO);

    @GetMapping(value = "bills/account/{accountId}")
    List<BillResponseDTO> getBillsByAccountId(@PathVariable(value = "accountId") Long accountId);
}
