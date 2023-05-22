package com.example.deposit.rest;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(value = "account-service")
public interface AccountServiceClient {
@RequestMapping(value = "/accounts/{accountId}")
    AccountResponseDTO getAccountById(@PathVariable("accountId") Long accountId);

}
