package com.example.deposit.service;

import com.example.deposit.dto.DepositResponseDTO;
import com.example.deposit.entity.Deposit;
import com.example.deposit.exception.DepositServiceException;
import com.example.deposit.repository.DepositRepository;
import com.example.deposit.rest.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class DepositService {
    @Value("${kafka.topics.test-topic}")
    private String kafkaTopic;
    private final KafkaTemplate<Object, Object> kafkaTemplate;
    private final DepositRepository depositRepository;
    private final AccountServiceClient accountServiceClient;
    private final BillServiceClient billServiceClient;

    @Autowired
    public DepositService(DepositRepository depositRepository, AccountServiceClient accountServiceClient, BillServiceClient billServiceClient, KafkaTemplate<Object, Object> kafkaTemplate) {
        this.depositRepository = depositRepository;
        this.accountServiceClient = accountServiceClient;
        this.billServiceClient = billServiceClient;
        this.kafkaTemplate = kafkaTemplate;
    }

    public DepositResponseDTO deposit(Long accountId, Long billId, BigDecimal amount) {
        if (accountId == null && billId == null) {
            throw new DepositServiceException("Account is null and bill is null");
        }
        if (billId != null) {
            BillResponseDTO billResponseDTO = billServiceClient.getBillById(billId);
            BillRequestDTO billRequestDTO = getBillRequestDTO(amount, billResponseDTO);

            billServiceClient.update(billId, billRequestDTO);
            AccountResponseDTO accountResponseDTO = accountServiceClient.getAccountById(billResponseDTO.getAccountId());
            depositRepository.save(new Deposit(amount, billId, OffsetDateTime.now(), accountResponseDTO.getEmail()));
            return getDepositResponseDTO(amount, accountResponseDTO);
        }
        BillResponseDTO defaultBill = getDefaultBill(accountId);
        BillRequestDTO billRequestDTO = getBillRequestDTO(amount, defaultBill);
        billServiceClient.update(defaultBill.getBillId(), billRequestDTO);
        AccountResponseDTO accountById = accountServiceClient.getAccountById(accountId);
        depositRepository.save(new Deposit(amount, defaultBill.getBillId(), OffsetDateTime.now(), accountById.getEmail()));
        return getDepositResponseDTO(amount, accountById);

    }

    private DepositResponseDTO getDepositResponseDTO(BigDecimal amount, AccountResponseDTO accountResponseDTO) {
        DepositResponseDTO responseDTO = new DepositResponseDTO(amount, accountResponseDTO.getEmail());
        kafkaTemplate.send(kafkaTopic, responseDTO);
        return responseDTO;
    }

    private BillRequestDTO getBillRequestDTO(BigDecimal amount, BillResponseDTO billResponseDTO) {
        BillRequestDTO billRequestDTO = new BillRequestDTO();
        billRequestDTO.setAccountId(billResponseDTO.getAccountId());
        billRequestDTO.setCreationDate(billResponseDTO.getCreationDate());
        billRequestDTO.setIsDefault(billResponseDTO.getIsDefault());
        billRequestDTO.setOverdraftEnabled(billResponseDTO.getOverdraftEnabled());
        billRequestDTO.setAmount(billResponseDTO.getAmount().add(amount));
        return billRequestDTO;
    }

    private BillResponseDTO getDefaultBill(Long accountId) {
        return billServiceClient.getBillsByAccountId(accountId).stream()
                .filter(BillResponseDTO::getIsDefault)
                .findAny()
                .orElseThrow(() -> new DepositServiceException("Unable to find default account with id: " + accountId));
    }
}
