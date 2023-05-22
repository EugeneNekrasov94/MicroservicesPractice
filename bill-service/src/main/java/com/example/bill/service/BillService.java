package com.example.bill.service;

import com.example.bill.entity.Bill;
import com.example.bill.exception.BillNotFoundException;
import com.example.bill.repository.BillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class BillService {
    private final BillRepository billRepository;

    @Autowired
    public BillService(BillRepository billRepository) {
        this.billRepository = billRepository;
    }

    public Bill getBillById(Long id){
        Iterable<Bill> all = billRepository.findAll();
        all.forEach(System.out::println);
        return billRepository.findById(id).orElseThrow(() -> new BillNotFoundException(String.format("Unable find bill with %d ID",id)));
    }

    public Long createBill(Long accountId, BigDecimal amount,Boolean isDefault,Boolean isOverdraftEnabled) {
        return billRepository.save(new Bill(accountId,amount,isDefault, OffsetDateTime.now(),isOverdraftEnabled)).getBillId();
    }

    public Bill updateBill(Long billId,Long accountId,BigDecimal amount,Boolean isDefault,Boolean isOverdraftEnabled) {
        return billRepository.save(new Bill(billId,accountId,amount,isDefault,isOverdraftEnabled));
    }

    public Bill deleteBill(Long id) {
        Bill billById = this.getBillById(id);
        billRepository.delete(billById);
        return billById;
    }

    public List<Bill> getBillsByAccountId(Long accountId) {
        return billRepository.getBillsByAccountId(accountId);
    }
}
