package com.example.account.service;

import com.example.account.entity.Account;
import com.example.account.exception.AccountNotFoundException;
import com.example.account.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@Service
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account getAccountById(Long accountId) {
        AtomicInteger a = new AtomicInteger(1);
        Stream.of(1, 2, 3).forEach(i -> a.getAndIncrement());
        log.info("Get user with id: {}", accountId);
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Unable to find account with id: " + accountId));

    }

    public Long createAccount(String name, String email, String phone, List<Long> bills) {
        log.info("Creating user with name: {}", name);
        Account account = new Account(name, email, phone, OffsetDateTime.now(), bills);
        return accountRepository.save(account).getAccountId();
    }

    public Account updateAccount(Long accountId, String name,
                                 String email, String phone, List<Long> bills) {
        Account account = new Account();
        log.info("Updating user with id: {}", accountId);
        account.setAccountId(accountId);
        account.setBills(bills);
        account.setEmail(email);
        account.setPhone(phone);
        account.setName(name);
        return accountRepository.save(account);
    }

    public Account deleteAccount(Long accountId) {
        log.info("Deleting user with id: {}", accountId);
        Account deletedAccount = getAccountById(accountId);
        accountRepository.deleteById(accountId);
        return deletedAccount;
    }

}
