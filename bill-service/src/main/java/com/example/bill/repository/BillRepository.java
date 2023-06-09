package com.example.bill.repository;

import com.example.bill.entity.Bill;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillRepository extends CrudRepository<Bill,Long> {
    List<Bill> getBillsByAccountId(Long accountId);
}
