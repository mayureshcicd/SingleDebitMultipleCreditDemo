package com.example.demo.controller;

import com.example.demo.dto.TransactionRequest;
import com.example.demo.service.ConcurrentTransactionService;
import com.example.demo.service.TransactionService;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private ConcurrentTransactionService concurrentTransactionService;

    @PostMapping("/single-debit-and-multiple-credit")
    public String debitAndCredit(@RequestParam Long debitAccountId,
                                 @RequestParam List<Long> creditAccountIds,
                                 @RequestParam Double amount) {
        transactionService.debitAndCredit(debitAccountId, creditAccountIds, amount);
        return "Transaction completed successfully!";
    }

    @Hidden
    @PostMapping("/concurrent-debit-credit")
    public String concurrentTransactions(@RequestBody TransactionRequest request) {
        concurrentTransactionService.checkDebitAndCreditConcurrentRequest(request);
        return "Transaction completed successfully!";
    }
}
